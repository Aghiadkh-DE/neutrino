package com.paperlessdesktop.ocrengine;

/**
 * Has 3 values and based on the number of cores
 * a CPU has.
 */
public enum CPUPerformance {
    MAX(Runtime.getRuntime().availableProcessors()), 
    BALANCED(Runtime.getRuntime().availableProcessors() / 2),
    LOW(1);

    private final int cores;

    CPUPerformance(int cores){
        this.cores = cores;
    }

    public int getCores() {
        return cores;
    }
}
