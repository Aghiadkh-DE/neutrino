package com.paperlessdesktop.util;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicBoolean;

import javafx.application.Platform;
import javafx.scene.control.Label;

public class TimerThread implements Runnable{
    private final Label timerLabel;

    private final AtomicBoolean running = new AtomicBoolean(false);

    public void stop(){
        running.set(false);
    }

    public TimerThread(Label timerLabel) {
        super();
        this.timerLabel = timerLabel;
    }

    public boolean isDone(){
        return !running.get();
    }

    @Override
    public void run() {
        running.set(true);
        LocalDateTime instance = LocalDateTime.of(2020, 12, 5, 00, 00, 00);
        while (running.get()) {
            try {
                final LocalDateTime temp = instance;
                Platform.runLater(() -> {
                    timerLabel.setText(Utility.formatTimeWithSeconds(temp));
                });
                instance = instance.plusSeconds(1);
                Thread.sleep(1000);    
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }    
    }
}
