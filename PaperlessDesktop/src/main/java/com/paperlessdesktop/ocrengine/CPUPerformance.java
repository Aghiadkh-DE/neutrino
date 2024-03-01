package com.paperlessdesktop.ocrengine;

import com.paperlessdesktop.util.Parameter;

/**
 * Has 3 values and based on the number of cores
 * a CPU has.
 */
public enum CPUPerformance {


    MAX(Parameter.AVAILABLE_CORES / 2),
    BALANCED((Parameter.AVAILABLE_CORES / 4) == 1 ? 2 : Parameter.AVAILABLE_CORES),
    LOW(1);

    private final int cores;

    CPUPerformance(int cores){
        this.cores = cores;
    }

    public int getCores() {
        return cores;
    }

}
