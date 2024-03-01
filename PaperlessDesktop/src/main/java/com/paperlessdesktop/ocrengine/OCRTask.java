package com.paperlessdesktop.ocrengine;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.paperlessdesktop.finder.automation.Language;
import com.paperlessdesktop.finder.fileloader.LoadFilesThread;
import com.paperlessdesktop.gui.App;
import com.paperlessdesktop.util.ArrayOperations;
import com.paperlessdesktop.util.Settings;
import com.paperlessdesktop.util.TimerThread;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;

public class OCRTask {
    /**
     * Source list files
     */
    private volatile List<File> sourceFileList;
    /**
     * Destination list files
     */
    private volatile List<File> dstFileList;
    /**
     * Number of files found in folder.
     */
    private final AtomicInteger fileCount;
    /**
     * Singleton instance
     */
    private static OCRTask instance;

    private final Settings settings = Settings.getInstance();
    /**
     * Source path read from settings.
     */
    private String sourcePath = settings.sourceDir;
    /**
     * Destination path read from settings.
     */
    private String dstPath = settings.destDir;
    /**
     * Volatile atomic boolean to keep track if loading task is running
     */
    private volatile AtomicBoolean loadingIsRunning = new AtomicBoolean(false);
    /**
     * Volatile atomic boolean to keep track if ocr task is running
     */
    private volatile AtomicBoolean ocrIsRunning = new AtomicBoolean(false);
    /**
     * Variable to keep track of hits found
     */
    private volatile AtomicInteger hits = new AtomicInteger(0);
    /**
     * Variable to keep track of files processed
     */
    private volatile AtomicInteger processed = new AtomicInteger(0);
    /**
     * List that keep ocr Threads
     */
    private final List<Thread> ocrThreads = new ArrayList<>();

    public static OCRTask getInstance(){
        return instance == null ? instance = new OCRTask() : instance;
    } 

    private OCRTask() {
        super();
        sourceFileList = new Vector<>();
        dstFileList = new Vector<>();
        fileCount = new AtomicInteger();
    }

    
    /**
     * Start OCR task
     * @param sourceListView SourceListView to update
     * @param destListView Destination listview to update
     * @param resultLabel Result Label to update
     * @param progressBar progress bar to update
     * @param ocrButton OCR button status to update
     * @param progressIndicator Progress indicator components to update
     * @param timerLabel Timer label to update.
     */
    public void startOCRTask(ListView<String> sourceListView,  ListView<String> destListView,Label resultLabel, ProgressBar progressBar,
                             Button ocrButton, ProgressIndicator progressIndicator, Label timerLabel){
        //reset ocrIsRunning flag
        ocrIsRunning.set(true);
        //Reset lists
        hits.set(0);
        processed.set(0);
        ocrThreads.clear();
        dstFileList.clear();

        //start a timer thread.
        TimerThread thread = new TimerThread(timerLabel);
        Thread timerThread = new Thread(thread, "Timer Thread");
        timerThread.setDaemon(true);
        timerThread.start();
        
        //read parallelism from settings.
        int parallelism = settings.cpuPerformance.getCores();
        CyclicBarrier barrier = new CyclicBarrier(parallelism);

        Platform.runLater(() -> {
            destListView.getItems().clear();
            resultLabel.setText("Doing OCR...");
            progressBar.setProgress(0);
            ocrButton.setDisable(true);
            progressBar.setProgress(0);
            progressIndicator.setOpacity(1);
        });

        final File path = new File(settings.destDir + File.separator + "filters");
        if(!path.exists()){
            path.mkdirs();
        }

        final AtomicBoolean shutdownFlag = new AtomicBoolean(false);
        final List<File> queue = new Vector<>(sourceFileList);
        for (int i = 0; i < parallelism; i++) {
            ocrThreads.add(new OCRThread(sourceListView, destListView, ocrButton, resultLabel, ocrIsRunning, 
            dstPath, queue, dstFileList, settings.ocrLanguage, settings.ocrEngineMode, settings.trainedDataPath, barrier, hits, processed, settings.withEnhancer,
            settings.cut_paste, progressIndicator, progressBar, thread, sourceFileList, shutdownFlag));
            ocrThreads.get(i).setName("OCR " + i + ":");
        }

        for (Thread t : ocrThreads) {
            t.setDaemon(true);
            t.start();
        }
    }

    /**
     * Load files method
     * @param sourceListView ListView to use.
     * @param resultLabel Result label to update.
     * @param progressBar ProgressBar to update
     * @return Returns true if loading is still running, otherwise false.
     */
    public AtomicBoolean loadFiles(ListView<String> sourceListView, Label resultLabel, ProgressBar progressBar){
        if(loadingIsRunning.get()){
            return loadingIsRunning;
        }
        //clearing parameters
        sourceListView.getItems().clear();
        resultLabel.setText("");
        sourceFileList.clear();
        fileCount.set(0);

        LoadFilesThread loadFilesRunnable = new LoadFilesThread(sourceListView, resultLabel, sourceFileList, fileCount, sourcePath, progressBar, loadingIsRunning);
        Thread loadThread = new Thread(loadFilesRunnable, "FilesLoaderThread");
        loadThread.setDaemon(true);
        loadThread.start();

        return loadingIsRunning;
    }

    public void addFileToCounter(int files){
        fileCount.set(fileCount.get() + files);
    }

    public int incrementFileCounter(){
        return fileCount.getAndIncrement();
    }

    public List<File> getDstFileList() {
        return dstFileList;
    }

    public String getDstPath() {
        return dstPath;
    }

    public List<File> getSourceFileList() {
        return sourceFileList;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public void setDstPath(String dstPath) {
        this.dstPath = dstPath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    /**
     * Clear GUI fields.
     */
    public void clearFields(){
        sourceFileList.clear();
        dstFileList.clear();
        fileCount.set(0);

        Platform.runLater(() -> {
            App.getPrimaryController().getStatusLabel().setText("");
            App.getPrimaryController().getResultLabel().setText("");
            App.getPrimaryController().getSourceListView().getItems().clear();
            App.getPrimaryController().getDestListView().getItems().clear();
        }); 
    }

    /**
     * Moves an element on one index from available list to quick list.
     * @param index index to remove
     * @param availableList available list to remove element from.
     * @param quickList quick list to add element to.
     * @param languageChoiceBox Choice box that has same elements as the quick list view.
     *
     * @deprecated This method is deprecated because it's inheritantly useless to move one element at a time, because the newer
     * method {@link #fromAvailableToQuick(ObservableList, ListView, ListView, ChoiceBox)} accomplishes the same task but
     * is far better.
     */
    public void fromAvailableToQuick(int index, ListView<String> availableList, ListView<String> quickList, ChoiceBox<Language> languageChoiceBox){
        Language temp = settings.availableLanguagesList.remove(index);
        settings.quickmenuLanguagesList.add(temp);
        settings.quickmenuLanguagesList.sort((a2, a1) -> a1.toString().compareTo(a2.toString()));
        Platform.runLater(() -> {
            availableList.getItems().remove(index);
            quickList.getItems().clear();
            languageChoiceBox.getItems().clear();
            settings.quickmenuLanguagesList.forEach(e -> languageChoiceBox.getItems().add(e));
            settings.quickmenuLanguagesList.forEach(e -> quickList.getItems().add(e.toString()));
        });
    }

    /**
     * Moves one or more languages from one side to the other. Available(languages) => quick list(languages) + languagesCheckBox
     * @param languages Movable languages as a list.
     * @param availableList Available-list to abduct elements from
     * @param quickList Quick gets the new element from the available-list.
     * @param languageChoiceBox Should have the same elements as the quick list.
     */
    public void fromAvailableToQuick(ObservableList<Language> languages, ListView<Language> availableList, ListView<Language> quickList, ChoiceBox<Language> languageChoiceBox){
        ArrayOperations.removeAll(languages, settings.availableLanguagesList);
        ArrayOperations.addAll(languages, settings.quickmenuLanguagesList);

        settings.availableLanguagesList.sort(Comparator.comparing(Language::toString));
        settings.quickmenuLanguagesList.sort(Comparator.comparing(Language::toString));

        Platform.runLater(() -> {
            availableList.getItems().clear();
            quickList.getItems().clear();
            languageChoiceBox.getItems().clear();

            availableList.getItems().addAll(settings.availableLanguagesList);
            quickList.getItems().addAll(settings.quickmenuLanguagesList);
            languageChoiceBox.getItems().addAll(settings.quickmenuLanguagesList);
        });
    }

    public void fromQuickToAvailable(ObservableList<Language> languages, ListView<Language> availableList, ListView<Language> quickList, ChoiceBox<Language> languageChoiceBox){
        ArrayOperations.addAll(languages, settings.availableLanguagesList);
        ArrayOperations.removeAll(languages, settings.quickmenuLanguagesList);

        settings.availableLanguagesList.sort(Comparator.comparing(Language::toString));
        settings.quickmenuLanguagesList.sort(Comparator.comparing(Language::toString));

        Platform.runLater(() -> {
            availableList.getItems().clear();
            quickList.getItems().clear();
            languageChoiceBox.getItems().clear();

            availableList.getItems().addAll(settings.availableLanguagesList);
            quickList.getItems().addAll(settings.quickmenuLanguagesList);
            languageChoiceBox.getItems().addAll(settings.quickmenuLanguagesList);
        });
    }

    /**
     * Moves one element from quick list to available list.
     * @param index Index of the element to be moved
     * @param availableList Available list to get the new elements
     * @param quickList Quick-List to abduct the new elements from.
     * @param languageChoiceBox Languages choice box that has the same elements as the quick list.
     *
     * @deprecated This method is deprecated because it moves only one element from one list to another. Use
     * {@link #fromQuickToAvailable(ObservableList, ListView, ListView, ChoiceBox)} instead.
     */
    public void fromQuickToAvailable(int index, ListView<String> availableList, ListView<String> quickList, ChoiceBox<Language> languageChoiceBox){
        Language temp = settings.quickmenuLanguagesList.remove(index);
        settings.availableLanguagesList.add(temp);
        settings.availableLanguagesList.sort(Comparator.comparing(Enum::toString));
        Platform.runLater(() -> {
            quickList.getItems().remove(index);
            languageChoiceBox.getItems().remove(index);
            availableList.getItems().clear();
            settings.availableLanguagesList.forEach(e -> availableList.getItems().add(e.toString()));
        });
    }

    public AtomicInteger getFileCount() {
        return fileCount;
    }

    public AtomicBoolean getOcrIsRunning() {
        return ocrIsRunning;
    }

    public AtomicBoolean getLoadingIsRunning() {
        return loadingIsRunning;
    }
}
