package com.paperlessdesktop.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.paperlessdesktop.finder.automation.AutomationToken;
import com.paperlessdesktop.finder.PatternFinder;

import org.apache.commons.io.FilenameUtils;

import javafx.scene.control.Alert.AlertType;

public class Utility {
    public static boolean isValidPath(String str){
        File file = new File(str);
        if(!file.exists()){
            return false;
        }
        return file.isDirectory();
    }

    /**
    * <pre>
    * Checks if a string is a valid path.
    * Null safe.
    *  
    * Calling examples:
    *    isValidPath("c:/test");      //returns true
    *    isValidPath("c:/te:t");      //returns false
    *    isValidPath("c:/te?t");      //returns false
    *    isValidPath("c/te*t");       //returns false
    *    isValidPath("good.txt");     //returns true
    *    isValidPath("not|good.txt"); //returns false
    *    isValidPath("not:good.txt"); //returns false
    * </pre>
    */
    public static boolean isValidPathFormat(String str){
        try {
            Paths.get(str);
        } catch (InvalidPathException | NullPointerException e) {
            return false;
        }
        return true;
    }

    public static boolean pathAllowed(String str){
        if(str.isBlank()){
            return false;
        }
        return isValidPath(str) && isValidPathFormat(str);
    }

    public static String getFileExtensionApache(String path){
        return FilenameUtils.getExtension(path);
    }

    public static String getFileExtensionApache(File file){
        return FilenameUtils.getExtension(file.getName());
    }

    public static String getFileExtensionJava(File file){
        return getFileExtensionJava(file.getName());
    }

    public static String getFileExtensionJava(String fileName){
        String extension = "";
        int index = fileName.lastIndexOf(".");

        if(index > 0){
            extension = fileName.substring(index + 1);
        }
        return extension;
    }

    public static boolean hasValidExtension(File file){
        String extension = getFileExtensionJava(file.getName());
        for (Extension extTemp : Extension.values()) {
            if(extTemp.getExtension().equals(extension)){
                return true;
            }
        }
        return false;
    }

    public static boolean validPictureExtension(String ext){
        for(Extension extension : Extension.values()){
            if(extension.getExtension().equals(ext)){
                return true;
            }
        }
        return false;
    }

    public static String formatTimeWithSeconds(LocalDateTime instance){
        return (instance.getHour() < 10 ? (instance.getHour() == 0 ? "00" : "0" + instance.getHour()) : instance.getHour()) + ":" + 
        (instance.getMinute() < 10 ? (instance.getMinute() == 0 ? "00" : "0" + instance.getMinute()) : instance.getMinute()) + ":" +
        (instance.getSecond() < 10 ? (instance.getSecond() == 0 ? "00" : "0" + instance.getSecond()) : instance.getSecond());
    }

    public static void serializeSettings(Settings settings){
        File path = new File(Parameter.NEUTRINO_DATA_PATH);
        if(!path.exists()){
            path.mkdirs();
        }
        path = new File(path.toString() + File.separator + "settings.serial");
        if(!path.exists()){
            try {
                path.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (final OutputStream file = new FileOutputStream(path)) {
            ObjectOutputStream oos = new ObjectOutputStream(file);
            oos.writeObject(settings);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deserializeSettings(){
        try (final InputStream file = new FileInputStream(Parameter.NEUTRINO_DATA_PATH + "settings.serial")) {
            ObjectInputStream ois = new ObjectInputStream(file);
            Settings.setInstance((Settings) ois.readObject());
        } catch (FileNotFoundException e) {
            serializeSettings(Settings.getInstance());
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void serializeAutomationList(List<AutomationToken> list){
        File path = new File(Parameter.NEUTRINO_DATA_PATH);
        if(!path.exists()){
            path.mkdirs();
        }
        path = new File(path.toString() + File.separator + "automation_list.serial");
        if(!path.exists()){
            try {
                path.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (final OutputStream file = new FileOutputStream(path)) {
            ObjectOutputStream oos = new ObjectOutputStream(file);
            oos.writeObject(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static void deserializeList(){
        try (final InputStream file = new FileInputStream(Parameter.NEUTRINO_DATA_PATH + "automation_list.serial")) {
            ObjectInputStream ois = new ObjectInputStream(file);
            PatternFinder.getInstance().setTokenList((List<AutomationToken>) ois.readObject());
        } catch (FileNotFoundException e) {
            serializeAutomationList(PatternFinder.getInstance().getTokenList());
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void openConfigDir(){
        File path = new File(Parameter.NEUTRINO_DATA_PATH);
        if(!path.exists()){
            InputDialogs.alertDialog(AlertType.ERROR, "Error", "Config files weren't found", "Reload the program and try again");
        }else{
            try {
                Runtime.getRuntime().exec("explorer.exe /open, " + path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void deleteDir(Path path){
        if(Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)){
            try (DirectoryStream<Path> entries = Files.newDirectoryStream(path)) {
                for(Path entry : entries){
                    deleteDir(entry);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            Files.delete(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <T> List<T> buildListFromArray(T[] arrayInput, boolean ignoreNull){
        List<T> list = new ArrayList<>();
        if(ignoreNull){
            for (T t : arrayInput) {
                if (t != null) {
                    list.add(t);
                }
            }

            return list;
        }

        return Arrays.asList(arrayInput);
    }

    public static boolean checkTrainedData(){
        File path1 = new File(Parameter.DEFAULT_TRAINED_DATA_PATH);
        File path2 = new File(Parameter.BEST_TRAINED_DATA_PATH);
        File path3 = new File(Parameter.FASTEST_TRAINED_DATA_PATH);

        return path1.exists() && path2.exists() && path3.exists();
    }
}
