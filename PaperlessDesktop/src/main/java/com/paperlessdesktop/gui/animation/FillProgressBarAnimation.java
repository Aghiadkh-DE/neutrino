package com.paperlessdesktop.gui.animation;

import javafx.application.Platform;
import javafx.scene.control.ProgressBar;

/**
 * Class that runs in a thread to create a smooth bar filling animations.
 */
public class FillProgressBarAnimation implements Runnable{
    private final ProgressBar progressBar;
    private int from = 0;
    private int to = 1;

    
    public FillProgressBarAnimation(ProgressBar progressBar) {
        super();
        this.progressBar = progressBar;
    }

    public FillProgressBarAnimation(ProgressBar progressBar, int from){
        this(progressBar);
        this.from = from;
    }

    public FillProgressBarAnimation(ProgressBar progressBar, int from, int to){
        this(progressBar, from);
        this.to = to;
    }

    @Override
    public void run() {
        Platform.runLater(() -> {
            progressBar.setProgress(from);
        });
        double[] progress = {from};
        while (progress[0] <= to) {
            progress[0] += 0.03;
            Platform.runLater(() -> {  
                progressBar.setProgress(progress[0]);
            });
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } 
    }
    
}
