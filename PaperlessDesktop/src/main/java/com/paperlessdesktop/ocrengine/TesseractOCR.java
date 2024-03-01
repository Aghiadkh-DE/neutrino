package com.paperlessdesktop.ocrengine;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.awt.image.BufferedImage;
import java.io.File;

import com.paperlessdesktop.finder.automation.Language;
import com.paperlessdesktop.imageenhancer.ImageEnhancer;
import com.paperlessdesktop.util.Parameter;
import com.paperlessdesktop.util.Settings;

public class TesseractOCR {
    private final Tesseract tesseract = new Tesseract();

    private final Settings setting = Settings.getInstance();

    public TesseractOCR() {
        tesseract.setOcrEngineMode(setting.ocrEngineMode);
        tesseract.setDatapath(Parameter.DEFAULT_TRAINED_DATA_PATH);
        tesseract.setLanguage(setting.ocrLanguage.getIdentifier());
    }

    public TesseractOCR(int engineMode){
        if(engineMode > 3 || engineMode < 0)
            tesseract.setOcrEngineMode(setting.ocrEngineMode);
        else
            tesseract.setOcrEngineMode(engineMode);
        tesseract.setDatapath(Parameter.DEFAULT_TRAINED_DATA_PATH);
        tesseract.setLanguage(setting.ocrLanguage.getIdentifier());
    }

    public TesseractOCR(String dataPath){
        if(dataPath == null || dataPath.isEmpty())
            tesseract.setDatapath(Parameter.DEFAULT_TRAINED_DATA_PATH);
        else
            tesseract.setDatapath(dataPath);
        tesseract.setOcrEngineMode(setting.ocrEngineMode);
        tesseract.setLanguage(setting.ocrLanguage.getIdentifier());
    }

    public TesseractOCR(int engineMode, String dataPath){
        if(engineMode > 3 || engineMode < 0)
            tesseract.setOcrEngineMode(setting.ocrEngineMode);
        else
            tesseract.setOcrEngineMode(engineMode);
        if(dataPath == null || dataPath.isEmpty())
            tesseract.setDatapath(Parameter.DEFAULT_TRAINED_DATA_PATH);
        else
            tesseract.setDatapath(dataPath);
        tesseract.setLanguage(setting.ocrLanguage.getIdentifier());
    }

    public TesseractOCR(int engineMode, String dataPath, Language language){
        if(engineMode > 3 || engineMode < 0)
            tesseract.setOcrEngineMode(setting.ocrEngineMode);
        else
            tesseract.setOcrEngineMode(engineMode);
        if(dataPath == null || dataPath.isEmpty())
            tesseract.setDatapath(Parameter.DEFAULT_TRAINED_DATA_PATH);
        else
            tesseract.setDatapath(dataPath);
        if(language == null)
            tesseract.setLanguage("eng");
        else
            tesseract.setLanguage(language.getIdentifier());
    }

    public String doOCR(BufferedImage image, boolean enhancer){
        if(enhancer){
            ImageEnhancer imageEnhancer = new ImageEnhancer(image);
            image = imageEnhancer.processImage();
        }

        try {
            return tesseract.doOCR(image);
        } catch (TesseractException e) {
            System.out.println("Cannot resolve image: " + image);
        }
        return null;
    }

    public String doOCR(File file){
        try {
            return tesseract.doOCR(file);
        } catch (TesseractException e) {
            System.out.println("Cannot resolve file: " + file);
        }
        return null;
    }
}
