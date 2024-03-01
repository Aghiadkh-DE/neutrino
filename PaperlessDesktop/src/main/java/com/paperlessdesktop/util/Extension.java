package com.paperlessdesktop.util;

/**
 * Enum that holds the extensions
 */
public enum Extension {
    PDF("pdf"), JPEG("jpeg"), JPG("jpg"), PNG("png"), TIF("tif"),
    PDF_CAPITAL("PDF"), JPEG_CAPITAL("JPEG"), JPG_CAPITAL("JPG"), PNG_CAPITAL("PNG"),
    TIF_CAPITAL("TIF");

    private final String extension;

    Extension(String ext){
        this.extension = ext;
    }

    public String getExtension() {
        return extension;
    }
}
