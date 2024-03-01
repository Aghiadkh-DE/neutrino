package com.paperlessdesktop.util;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.paperlessdesktop.finder.automation.Language;
import com.paperlessdesktop.ocrengine.CPUPerformance;
import com.paperlessdesktop.ocrengine.OCRTask;

import net.sourceforge.tess4j.ITessAPI.TessOcrEngineMode;

public final class Settings implements Serializable{
    @Serial
    private static final long serialVersionUID = -498603858953829564L;

    private static Settings instance;

    public Language ocrLanguage = Language.German;
    public int ocrEngineMode = TessOcrEngineMode.OEM_DEFAULT;

    public String sourceDir = null;
    public String destinationDir = null;

    public int tokensCount = 0;

    public int quickSortParallelism = Runtime.getRuntime().availableProcessors();

    public List<Language> availableLanguagesList = new ArrayList<>();
    public List<Language> quickmenuLanguagesList = new ArrayList<>();

    public void setSourceDir(String str){
        sourceDir = str;
        OCRTask.getInstance().setSourcePath(str);
    }

    public void setDestinationDir(String str){
        destinationDir = str;
        OCRTask.getInstance().setDstPath(str);
    }

    private Settings(){
        for (Language temp  : Language.values()) {
            if(!(temp.getIdentifier().equals("eng") || temp.getIdentifier().equals("deu"))){
                availableLanguagesList.add(temp);
            }else{
                quickmenuLanguagesList.add(temp);
            }
        }
    }

    public static Settings getInstance(){
        return instance == null ? (instance = new Settings()) : instance;
    }

    static void setInstance(Settings instance) {
        Settings.instance = instance;
    }

    public CPUPerformance cpuPerformance = CPUPerformance.BALANCED;
    public String trainedDataPath = Parameter.DEFAULT_TRAINED_DATA_PATH;
    public boolean withEnhancer = true;
    public boolean cut_paste = false;

    public boolean loadSourcePathOnStartUp = false;
    public boolean loadDestinationPathOnStartUp = false;

    public volatile AtomicBoolean shutdownAfterOCR = new AtomicBoolean(false);

    public boolean startMinimized = false;

    public boolean openOnStartup = false;
}
