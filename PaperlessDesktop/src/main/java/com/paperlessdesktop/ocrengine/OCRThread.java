package com.paperlessdesktop.ocrengine;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;

import com.paperlessdesktop.finder.automation.AutomationToken;
import com.paperlessdesktop.finder.automation.Language;
import com.paperlessdesktop.finder.PatternFinder;
import com.paperlessdesktop.gui.App;
import com.paperlessdesktop.util.Settings;
import com.paperlessdesktop.util.TimerThread;
import com.paperlessdesktop.util.Utility;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;

/**
 * This class represents an OCR working thread.
 */
public class OCRThread extends Thread{
    /**
     * Source listView
     */
    private final ListView<String> sourceListView;
    /**
     * Destination listView
     */
    private final ListView<String> destListView;
    /**
     * Progress indicator component
     */
    private final ProgressIndicator progressIndicator;
    /**
     * Progress bar component
     */
    private final ProgressBar progressBar;
    /**
     * OCR button component
     */
    private final Button ocrButton;
    /**
     * Result label component
     */
    private final Label resultLabel;
    /**
     * Running flag
     */
    private final AtomicBoolean ocrIsRunning;
    /**
     * Keep track of hits
     */
    private final AtomicInteger hits;
    /**
     * Keep track of processed files
     */
    private final AtomicInteger processed;
    /**
     * Destination path string
     */
    private final String destPath;
    /**
     * Queue
     */
    private final List<File> queueSourceFiles;
    /**
     * List that contains instances of files in destination
     */
    private final List<File> destFiles;
    /**
     * List that contains instances of files in source
     */
    private final List<File> sourceFiles;
    /**
     * Tesseract instance for this thread
     */
    private final TesseractOCR tesseractInstance;
    /**
     * Image enhancer flag
     */
    private final boolean imageEnhancer;
    /**
     * Cut paste flag
     */
    private final boolean cut_paste;
    /**
     * Barrier reference
     */
    private final CyclicBarrier barrier;

    private final PatternFinder patternFinder = PatternFinder.getInstance();

    private final TimerThread timerThread;

    private final int fileCount = OCRTask.getInstance().getFileCount().get();

    private final AtomicBoolean shutdownFlag;

    public OCRThread(ListView<String> sourceListView, ListView<String> destListView, Button ocrButton, Label resultLabel,
                     AtomicBoolean ocrIsRunning, String destPath, List<File> queueSourceFiles, List<File> destFiles, Language language, int oem, String trainedData, 
                     CyclicBarrier barrier, AtomicInteger hits, AtomicInteger processed, boolean imageEnhancer, boolean cut_paste,
                     ProgressIndicator progressIndicator, ProgressBar progressBar, TimerThread timerThread, List<File> sourceFiles, AtomicBoolean shutdownFlag) {
        if(sourceListView == null)      throw new NullPointerException("Source list view cannot be null!");
        if(destListView == null)        throw new NullPointerException("Dest list view cannot null!");
        if(ocrButton == null)           throw new NullPointerException("OCR Button cannot be null!");
        if(resultLabel == null)         throw new NullPointerException("Result Label cannot be null!");
        if(ocrIsRunning == null)        throw new NullPointerException("Atomic-boolean flag cannot be null!");
        if(queueSourceFiles == null)    throw new NullPointerException("Queue list cannot be left null!");
        if(destFiles == null)           throw new NullPointerException("Destination list cannot be left null");
        if(language == null)            throw new NullPointerException("Language cannot be null!");
        if(trainedData == null)         throw new NullPointerException("Path for trained data cannot be null!");
        if(hits == null)                throw new NullPointerException("Hits reference cannot be null!");
        if(processed == null)           throw new NullPointerException("Processed cannot be null!");
        if(progressIndicator == null)   throw new NullPointerException("ProgressIndicator cannot be null!");
        if(sourceFiles == null)         throw new NullPointerException("Source files list cannot be null!");

        tesseractInstance = new TesseractOCR(oem, trainedData, language);

        this.sourceListView = sourceListView;
        this.destListView = destListView;
        this.ocrButton = ocrButton;
        this.resultLabel = resultLabel;
        this.ocrIsRunning = ocrIsRunning;
        this.destPath = destPath;
        this.queueSourceFiles = queueSourceFiles;
        this.imageEnhancer = imageEnhancer;
        this.cut_paste = cut_paste;
        this.timerThread = timerThread;
        this.sourceFiles = sourceFiles;
        this.processed = processed;
        this.hits = hits;
        this.destFiles = destFiles;
        this.progressBar = progressBar;
        this.progressIndicator = progressIndicator;
        this.barrier = barrier;
        this.shutdownFlag = shutdownFlag;
    }

	@Override
    public void run() {
        //check if empty and end loop if true
        while (true) {
            File file;
            synchronized (queueSourceFiles){
                if(queueSourceFiles.isEmpty()){
                    break;
                }
                file = queueSourceFiles.remove(queueSourceFiles.size() - 1);
                App.logger.info(this + " doing -> " + file.getName());
            }
            startOCR(file);
        }
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
        synchronized(queueSourceFiles){
            if(!timerThread.isDone()){
                timerThread.stop();
            }
            if(progressIndicator.getOpacity() == 1){
                progressIndicator.setOpacity(0);
            }
        }
        Platform.runLater(() -> resultLabel.setText("OCR result -> " + hits.get() + " hits out of " + processed.get() + " file(s)"));
        ocrButton.setDisable(false);
        ocrIsRunning.set(false);
        
        if(!shutdownFlag.get()){
            shutdownFlag.set(true);
            if(Settings.getInstance().shutdownAfterOCR.get()){
                try {
                    Runtime.getRuntime().exec("shutdown -s -t 10");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                App.closeApp();
            }
        }
    }

    /**
     * Start OCR for this file.
     * @param file File instance.
     */
    public void startOCR(File file){
        if(file == null)
            return;

        //read extension
        String ext = Utility.getFileExtensionApache(file);
        //if file is pdf
        if(ext.equals("pdf")){
            //read text from OCR
            String text = tesseractInstance.doOCR(file);
            //get Automation token
            AutomationToken token = patternFinder.foundByThisToken(text == null ? "" : text);
            //if match
            if(token != null){
                //increment hits
                hits.incrementAndGet();
                //make dirs if needed
                File path = new File(destPath + File.separator + "filters" + File.separator + token.getFolderName());
                if(!path.exists()){
                    path.mkdirs();
                }
                File pathWithFileName = new File(path.toString() + File.separator + file.getName());
                try {
                    //copy file to path
                    Files.copy(file.toPath(), pathWithFileName.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    System.out.println("Error copying files");
                    e.printStackTrace();
                }

                //add file to destination list
                destFiles.add(new File(path.getPath() + File.separator + file.getName()));
                //remove from source if flag is activated
                if(cut_paste){
                    file.delete();
                }

                sourceFiles.remove(file);
            }
        }
        //if extension is for a picture
        else if(Utility.validPictureExtension(ext)){
            String text = null;  
            AutomationToken token = null;
            try {
                BufferedImage image = ImageIO.read(file);
                //System.out.println("Image: " + image);
                if(image != null){
                    //System.out.println("Processing");
                    text = tesseractInstance.doOCR(image, imageEnhancer);
                    token = patternFinder.foundByThisToken(text == null ? "" : text);
                }
            } catch (IOException e) {
                System.err.println("Image couldn't be read!");
                e.printStackTrace();
            }
            
            if(token != null){
                hits.incrementAndGet();

                File path = new File(destPath + File.separator + "filters" + File.separator + token.getFolderName() + File.separator);
                if(!path.exists()){
                    path.mkdirs();
                }
                File pathWithFileName = new File(path.toString() + File.separator + file.getName());
                try {
                    Files.copy(file.toPath(), pathWithFileName.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    System.out.println("Error copying files");
                    e.printStackTrace();
                }

                destFiles.add(new File(path.getPath() + File.separator + file.getName()));

                if(cut_paste){
                    file.delete();
                }

                sourceFiles.remove(file);
            }
        }
        processed.incrementAndGet();
        Platform.runLater(() -> {
            resultLabel.setText("Doing OCR -> processed " + processed.get() + " out of " + fileCount + " (" + hits.get() + " hits)");
            destListView.getItems().clear();
            sourceListView.getItems().clear();
            destFiles.forEach(f -> destListView.getItems().add(f.getName()));
            sourceFiles.forEach(f -> sourceListView.getItems().add(f.getName()));
            progressBar.setProgress((double)(processed.get()) / (double)fileCount);
        });
    }
    
}
