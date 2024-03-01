package com.paperlessdesktop.imageenhancer;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Engine that process pictures to make a contrast between 
 * background and foreground.
 */
public class ImageEnhancer {
    private BufferedImage inputImage = null;
    private boolean isProcessed;
    
    private ImageEnhancer() {
    	this.isProcessed = false;
    }

    public ImageEnhancer(File image) {
    	this();
        try {
            this.inputImage = ImageIO.read(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ImageEnhancer(BufferedImage image){
        this.inputImage = image;
    }
    
    public ImageEnhancer(String path) {
    	this();
    	try {
			this.inputImage = ImageIO.read(new FileInputStream(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public boolean isPictureProcessed() {
    	return this.isProcessed;
    }

    private BufferedImage reworkImage(BufferedImage inputImage, float scaleFactor, float offset){
        //image buffer to store image to be outputted later
        //Output image with 8-bit encoding
        BufferedImage outputImage = null;
        try {
            outputImage = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(), inputImage.getType());
        } catch (IllegalArgumentException e) {
            System.out.println("Unknown image Type");
            return inputImage;
        }
        
        Graphics2D graphic = outputImage.createGraphics();
        graphic.drawImage(inputImage, 0, 0, inputImage.getWidth(), inputImage.getHeight(), null);
        graphic.dispose();
        
        //rescaling and copying the filtered image according to its scale factor and offset
        RescaleOp rescale = new RescaleOp(scaleFactor, offset, null);
        BufferedImage fopimage = null;
        try {
            fopimage = rescale.filter(outputImage, null);
        } catch (IllegalArgumentException e) {
            System.err.println("Cannot rescale indexed image");
            return inputImage;
        }
        
        
        return fopimage;
    }

    /**
     * Process image.
     * @return A reference on the processed image or the same input image if the image
     * couldn't be processed
     */
    public BufferedImage processImage(){  
        BufferedImage outputImage = null;
        double d = inputImage.getRGB(inputImage.getTileWidth() / 2, inputImage.getTileHeight() / 2);
        if (d >= -1.4211511E7 && d < -7254228) 
            outputImage = reworkImage(inputImage, 3f, -10f);
        else if (d >= -7254228 && d < -2171170) 
            outputImage = reworkImage(inputImage, 1.455f, -47f);
        else if (d >= -2171170 && d < -1907998) 
            outputImage = reworkImage(inputImage, 1.35f, -10f);
        else if (d >= -1907998 && d < -257) 
            outputImage = reworkImage(inputImage, 1.19f, 0.5f);
        else if (d >= -257 && d < -1) 
            outputImage = reworkImage(inputImage, 1f, 0.5f);
        else if (d >= -1 && d < 2) 
            outputImage = reworkImage(inputImage, 1f, 0.35f);
        
        if(outputImage != null) 
        	this.isProcessed = true;
        
      //return output image if it can be processed, otherwise the original input image
        return isProcessed ? inputImage : null;
    }
}
