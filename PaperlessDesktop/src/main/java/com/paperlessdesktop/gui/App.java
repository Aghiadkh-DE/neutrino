package com.paperlessdesktop.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.awt.SystemTray;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.paperlessdesktop.finder.PatternFinder;
import com.paperlessdesktop.gui.controllers.GUIMinimizer;
import com.paperlessdesktop.gui.controllers.HelpController;
import com.paperlessdesktop.gui.controllers.PrimaryController;
import com.paperlessdesktop.gui.controllers.SecondaryController;
import com.paperlessdesktop.ocrengine.OCRTask;
import com.paperlessdesktop.util.InputDialogs;
import com.paperlessdesktop.util.Parameter;
import com.paperlessdesktop.util.Settings;
import com.paperlessdesktop.util.Utility;

/**
 * JavaFX App
 */
public class App extends Application{
    private static Scene scene;
    private static Stage primaryStage;
    private static Stage secondaryStage;
    private static Stage helpStage;

    private static PrimaryController primaryController;
    private static SecondaryController secondaryController;
    private static HelpController helpController;

    public static final Logger logger = Logger.getLogger(App.class.getName());

    public static void closeApp(){
        logger.info("Closing application");
        Utility.serializeAutomationList(PatternFinder.getInstance().getTokenList());
        Utility.serializeSettings(Settings.getInstance());
        Platform.runLater(Platform::exit);

        SystemTray.getSystemTray().remove(GUIMinimizer.trayIcon);
    }

    public static void minimizeApp(){
        primaryStage.hide();
        secondaryStage.hide();
        helpStage.hide();
    }

    public static void restartApp(){
        logger.info("Restarting application");
        ProcessBuilder processBuilder = new ProcessBuilder("restart.bat");
        try {
            processBuilder.start();
        } catch (Throwable e) {
            logger.throwing(App.class.toString(), "restartApp", e);
        }

        Platform.runLater(Platform::exit);
    }

    @Override
    public void start(Stage stage) throws IOException {
        //make program keep working even when program is minimized
        Platform.setImplicitExit(false);

        logger.info("Starting application");
        primaryController = PrimaryController.getInstance();
        secondaryController = SecondaryController.getInstance();
        helpController = HelpController.getInstance();
        initializePrimaryStage(stage);
        initializeOptionStage();
        initializeHelpStage();
        
        stage.show();

        try {
            File path = new File(Parameter.LOG_DATA_PATH);
            if(!path.exists())
                path.mkdirs();
            FileHandler handler = new FileHandler(path + File.separator + "log.xml", true);
            logger.addHandler(handler);
        } catch (SecurityException | IOException e) {
            Platform.runLater(() -> InputDialogs.exceptionDialog(e, "Logger couldn't write to file!"));
            logger.log(Level.SEVERE, "Logger couldn't write to file!", e);
        }
        stage.setOnCloseRequest((event) -> {
            event.consume();
            Optional<ButtonType> optional = InputDialogs.alertDialog(AlertType.CONFIRMATION,
                    "Neutrino",
                    "Confirmation",
                    OCRTask.getInstance().getOcrIsRunning().get() ?
                            "An OCR task is running. Close anyways?" : "Exit program or minimize?",
                    new ButtonType("Minimize"),
                    new ButtonType("Exit Application"),
                    new ButtonType("Cancel"));
            optional.ifPresent((response) -> {
                if(response.getText().equals("Exit Application")) {
                    closeApp();
                }
                else if(response.getText().equals("Minimize")){
                    minimizeApp();
                }
            });
        });
        if(!Utility.checkTrainedData()){
            logger.log(Level.SEVERE, "Trained data wasn't found!!");
            Platform.runLater(() -> {
                InputDialogs.alertDialog(AlertType.ERROR, "Error", "Initialization failed", "The trained data for OCR engine wasn't found!");
                closeApp();
            });       
        }
        
        GUIMinimizer.init();
    }

    private void initializePrimaryStage(Stage stage) throws IOException{;
        primaryStage = stage;
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("default.png"))));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("primary.fxml"));
    	loader.setController(primaryController);
        scene = new Scene(loader.load());
        stage.setResizable(false);
        stage.setScene(scene);

        primaryStage.setTitle("Neutrino");
    }

    private void initializeOptionStage() throws IOException{
        secondaryStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("secondary.fxml"));
        secondaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("default.png"))));
        loader.setController(secondaryController);
        Scene scene = new Scene(loader.load());

        secondaryStage.setTitle("Neutrino: Options");
        secondaryStage.setResizable(false);
        secondaryStage.setScene(scene);
    }

    private void initializeHelpStage() throws IOException{
        helpStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("help.fxml"));
        helpStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("default.png"))));
        loader.setController(helpController);

        Scene scene = new Scene(loader.load());

        helpStage.setTitle("Neutrino: Help");
        helpStage.setResizable(false);
        helpStage.setScene(scene); 
    }

	public static PrimaryController getPrimaryController() {
        return primaryController;
    }

    public static SecondaryController getSecondaryController() {
        return secondaryController;
    }

    public static HelpController getHelpController() {
        return helpController;
    }

    public static Stage getHelpStage() {
        return helpStage;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static Stage getSecondaryStage() {
        return secondaryStage;
    }

    public static void main(String[] args) {
        Utility.deserializeSettings();
        Utility.deserializeList();
        launch();
    }

}