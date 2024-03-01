package com.paperlessdesktop.util;

import java.io.File;

public final class Parameter {

    private Parameter(){}
    private static final String TRAINED_DATA_PATH = "tess_trained_data" + File.separator;
    public static final String DEFAULT_TRAINED_DATA_PATH = TRAINED_DATA_PATH + "tessdata-main" + File.separator;
    public static final String BEST_TRAINED_DATA_PATH = TRAINED_DATA_PATH + "tessdata_best-main" + File.separator;
    public static final String FASTEST_TRAINED_DATA_PATH = TRAINED_DATA_PATH + "tessdata_fast-main" + File.separator;

    private static final String USER_DIRECTORY = System.getProperty("user.home") + File.separator;
    public static final String NEUTRINO_DATA_PATH = USER_DIRECTORY + ".neutrino" + File.separator;
    public static final String LOG_DATA_PATH = USER_DIRECTORY + "neutrino_logs" + File.separator;

    public static final int AVAILABLE_CORES = Runtime.getRuntime().availableProcessors();
}
