package com.paperlessdesktop.finder.fileloader;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Vector;
import java.util.Objects;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.paperlessdesktop.gui.animation.FillProgressBarAnimation;
import com.paperlessdesktop.util.Settings;
import com.paperlessdesktop.util.Utility;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;

/**
 * A Class that implements the runnable interface and Loads files
 * from a specific given source dir to be processed later by the OCR-Engine.
 * <p>
 * This class also makes an instance of the class {@link ThreadedQuickSort}
 * to sort all the files in the list and later update the GUI.
 * <p>
 * The default number of parallelism for the sorting algorithm is the number of the host
 * CPU cores.
 * @author Aghiad Khertabeel
 * @version 1.0
 */
public class LoadFilesThread implements Runnable{
    /**
     * ListView GUI component to be edited.
     */
    private final ListView<String> sourceListView;
    /**
     * Label GUI component to be edited
     */
    private final Label resultLabel;
    /**
     * Progress bar to change
     */
    private final ProgressBar progressBar;
    /**
     * Files that are loaded from a specific directory
     * are added here and then sorted in the exact same
     * list
     */
    private final List<File> sourceList;
    /**
     * Source directory
     */
    private final String sourceDir;

    private final AtomicInteger countFiles;

    /**
     * Parallelism variable loaded from {@link Settings} class.
     */
    private final int parallelism = Settings.getInstance().quickSortParallelism;

    /**
     * CyclicBarrier that holds 2 tokens:
     * <p>
     * The first one is consumed by this thread
     * <p>
     * The other one is consumed by {@link ThreadedQuickSort} when it's done recurring
     */
    private final CyclicBarrier cyclicBarrier = new CyclicBarrier(2);
    /**
     * CyclicBarrier that holds 2 tokens:
     * <p>
     * The first one is consumed by this thread
     * <p>
     * The other one is consumed by JavaFx GUI Thread
     * <p>
     * The purpose of this barrier is to wait until the JavaFx Thread
     * has finished adding the items to the {@link Vector}
     */
    private final CyclicBarrier listBarrier = new CyclicBarrier(2);
    /**
     * Queue that holds directories that need to be searched later
     */
    private final Queue<File> dirsQueue = new LinkedList<>();

    private final AtomicBoolean loadingIsRunning;

    /**
     * Standard-Constructor that initiates all the variable and references needed to start this Thread.
     * @param sourceListView ListView from JavaFx GUI that holds String collection of the items.
     * @param resultLabel Result label from JavaFx GUI
     * @param sourceList List with File Instances that keeps track of the files in the system.
     * @param countFiles AtomicInteger to keep track of the number of files discovered.
     * @param sourceDir String that represents the source directory
     * @param progressBar ProgressBar from JavaFx GUI
     * @param threadIsRunning AtomicBoolean to check whether the thread is running
     */
    public LoadFilesThread(ListView<String> sourceListView, Label resultLabel, List<File> sourceList, 
    AtomicInteger countFiles, String sourceDir, ProgressBar progressBar, AtomicBoolean threadIsRunning){
        if(sourceListView == null){
            throw new NullPointerException("Source listview component used in LoadFilesThread but it's null!");
        }
        if(resultLabel == null){
            throw new NullPointerException("Result-Label component used in LoadFilesThread but it's null!");
        }
        if(sourceList == null){
            throw new NullPointerException("List that contains List instances is null!");
        }
        if(countFiles == null){
            throw new NullPointerException("The atomic integer that counts files is null!");
        }
        if(sourceDir == null || sourceDir.isBlank()){
            throw new NullPointerException("Source directory can't be used because it's empty or null!");
        }
        if(progressBar == null){
            throw new NullPointerException("ProgressBar cannot be used because it's null!");
        }
        this.sourceListView = sourceListView;
        this.resultLabel = resultLabel;
        this.sourceList = sourceList;
        this.countFiles = countFiles;
        this.sourceDir = sourceDir;
        this.progressBar = progressBar;
        this.loadingIsRunning = threadIsRunning;
    }

    @Override
    public void run() {
        loadingIsRunning.set(true);
        //updates GUI components
        Platform.runLater(() -> {
            resultLabel.setText("Searching for files");
            progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        });

        //search for files and instance the start directory
        File mainDir = new File(sourceDir);
        scanFilesInDirectory(mainDir, dirsQueue);

        Platform.runLater(() -> resultLabel.setText("Sorting (" + countFiles.get() + ") file(s)"));

        //ForkJoinPoll with number of threads
        ForkJoinPool forkPool = new ForkJoinPool(parallelism);
        List<String> itemsList = new Vector<>();

        Platform.runLater(() -> {
            //load listview items onto the vector
            itemsList.addAll(sourceListView.getItems());
            try {
                listBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        });

        try {
            //wait until loading from gui is done
            listBarrier.await();
        } catch (InterruptedException | BrokenBarrierException e1) {
            e1.printStackTrace();
        }

        ThreadedQuickSort quickSort = new ThreadedQuickSort(sourceList, itemsList, cyclicBarrier);
        forkPool.submit(quickSort);
        try {
            cyclicBarrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }

        Platform.runLater(() -> {
            sourceListView.getItems().clear();
            sourceListView.getItems().addAll(quickSort.getFileList());
        });
        

        FillProgressBarAnimation progressBarAnimation = new FillProgressBarAnimation(progressBar);
        Thread animationThread = new Thread(progressBarAnimation);
        animationThread.start();
        Platform.runLater(() -> resultLabel.setText(countFiles.get() + " file(s) has been scanned and found"));
        loadingIsRunning.set(false);   
    }

    /**
     * Scans the given directory for supported files and scans all subdirectories.
     * @param mainDir Directory to start with.
     * @param queue Queue instance to use.
     */
    private void scanFilesInDirectory(File mainDir, Queue<File> queue){
        //search all content of the mainDir
        for (final File fileEntry : Objects.requireNonNull(mainDir.listFiles())) {
            //directory found then queue it and call queueDirectory
            if(fileEntry.isDirectory()){
                queue.add(fileEntry);
                while (!queue.isEmpty()) {
                    queueDirectory(queue.poll(), queue);
                }
            }
            //if other files were found then check extension for validation
            else if(Utility.hasValidExtension(fileEntry)){
                //increment counter and add file-entry to list
                countFiles.incrementAndGet();
                sourceList.add(fileEntry);

                //update GUI
                Platform.runLater(() -> {
                    sourceListView.getItems().add(fileEntry.getName());
                    resultLabel.setText("Searching for files -> " + countFiles.get() + " file(s) has been found!");
                });
            }
        }
    }

    /**
     * Queue a directory's files into the list to load the initial files.
     * This method only queues files that have a valid extension ({@code PNG}, {@code JPEG}, {@code PDF}, 
     * {@code JPG}, {@code TIF})
     * @param dir Directory to be searched
     * @param queue queue to be used.
     */
    private void queueDirectory(File dir, Queue<File> queue){
        //if directory is empty then ignore and dequeue
        if(dir.listFiles() == null || Objects.requireNonNull(dir.listFiles()).length == 0){
            queue.remove(dir);
            return;
        }
        //Search in this directory for further Dirs and compatible files
        for (final File file : Objects.requireNonNull(dir.listFiles())) {
            //if entry is directory then queue it
            if(file.isDirectory()){
                queue.add(file);
            }else{
                //check file extension
                //then update lists and GUI
                if(Utility.hasValidExtension(file)){
                    countFiles.incrementAndGet();
                    sourceList.add(file);

                    //update GUI
                    Platform.runLater(() -> {
                        sourceListView.getItems().add(file.getName());
                        resultLabel.setText("Searching for files -> " + countFiles.get() + " file(s) has been found!");
                    });
                }
            }
        }
    }   
}
