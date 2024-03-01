package com.paperlessdesktop.gui.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

import com.paperlessdesktop.finder.automation.Language;
import com.paperlessdesktop.finder.PatternFinder;
import com.paperlessdesktop.gui.App;
import com.paperlessdesktop.ocrengine.CPUPerformance;
import com.paperlessdesktop.ocrengine.OCRTask;
import com.paperlessdesktop.util.InputDialogs;
import com.paperlessdesktop.util.Settings;
import com.paperlessdesktop.util.Utility;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 * Primary Controller of the main GUI window.
 */
public class PrimaryController implements Initializable {
    private final Settings settings = Settings.getInstance();
    private final OCRTask ocrTask = OCRTask.getInstance();

    private static PrimaryController instance;
    private final SecondaryController secondaryController = SecondaryController.getInstance();

    private PrimaryController() {}

    public static PrimaryController getInstance(){
        return instance != null ? instance : (instance = new PrimaryController());
    }

    @FXML
    private MenuItem quitMenuItem;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private MenuItem aboutMenuItem;

    @FXML
    private MenuItem addAutomationMenuItem;

    @FXML
    private MenuItem configPathMenuItem;

    @FXML
    private ListView<String> destListView;

    @FXML
    private TextField destPathTextField;

    @FXML
    private ChoiceBox<Language> languageChoiceBox;

    @FXML
    private CheckBox maxPerformanceChoiceBox;

    @FXML
    private Label languageLabel;

    @FXML
    private Button ocrBtn;

    @FXML
    private CheckBox pictureEnhancerCheckBox;

    @FXML
    private MenuItem preferenceMenuItem;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private MenuItem restMenuItem;

    @FXML
    private Label resultLabel;

    @FXML
    private ListView<String> sourceListView;

    @FXML
    private TextField sourcePathTextField;

    @FXML
    private Label statusLabel;

    @FXML
    private Label versionLabel;

    @FXML
    private MenuItem viewAutomationsMenuItem;

    @FXML
    private Button loadFilesBtn;

    @FXML
    private Label timerLabel;

    @FXML
    private MenuItem sourceFolderMenuItem;

    @FXML
    private MenuItem destinationFolderMenuItem;

    public ListView<String> getSourceListView() {
        return sourceListView;
    }

    public ListView<String> getDestListView() {
        return destListView;
    }

    public Label getStatusLabel() {
        return statusLabel;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public ProgressIndicator getProgressIndicator() {
        return progressIndicator;
    }

    public Label getResultLabel() {
        return resultLabel;
    }

    public ChoiceBox<Language> getLanguageChoiceBox() {
        return languageChoiceBox;
    
    }

    public CheckBox getMaxPerformanceChoiceBox() {
        return maxPerformanceChoiceBox;
    }

    public CheckBox getPictureEnhancerCheckBox() {
        return pictureEnhancerCheckBox;
    }
    
    /**
     * Open destination directory menu item pressed
     * @param event Event to process
     */
    @FXML
    void openDestinationFolderAction(ActionEvent event) {
        //No destination path
        if(ocrTask.getDstPath() == null){
            InputDialogs.alertDialog(AlertType.INFORMATION, "Info", "Directory cannot be opened", "Destination Directory cannot be " 
           + "opened because it's not set yet!");
        }else if(ocrTask.getDstPath().isBlank()){
            InputDialogs.alertDialog(AlertType.INFORMATION, "Info", "Directory cannot be opened", "Destination Directory cannot be " 
           + "opened because it's not set yet!");
        }else{
            try {
                Runtime.getRuntime().exec("explorer /open, " + new File(ocrTask.getDstPath()).toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Open source directory menu item pressed
     * @param event Event to process.
     */
    @FXML
    void openSourceFolderAction(ActionEvent event) {
        //if source path is not specified
        if(ocrTask.getSourcePath() == null){
            InputDialogs.alertDialog(AlertType.INFORMATION, "Info", "Directory cannot be opened", "Source Directory cannot be " 
           + "opened because it's not set yet!");
        }else if(ocrTask.getSourcePath().isBlank()){
            InputDialogs.alertDialog(AlertType.INFORMATION, "Info", "Directory cannot be opened", "Source Directory cannot be " 
           + "opened because it's not set yet!");
        }else{
            try {
                Runtime.getRuntime().exec("explorer /open, " + new File(ocrTask.getSourcePath()).toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Click on destination text field
     * @param event Event to process.
     */
    @FXML
    void destTextFieldMouseClicked(MouseEvent event) {
        //If left mouse button clicked
        if(event.getButton().equals(MouseButton.PRIMARY)){
            //if button clicked twice
            if(event.getClickCount() == 2){
                if(ocrTask.getOcrIsRunning().get()){
                    statusLabel.setTextFill(Color.RED);
                    statusLabel.setText("A OCR task is already running!");
                    return;
                }
                //open Directory chooser
                DirectoryChooser directoryChooser = new DirectoryChooser();
                File dir = directoryChooser.showDialog(App.getPrimaryStage());
                //if Directory was chosen
                if(dir != null){
                    //change text-field text
                    destPathTextField.setText(dir.toPath().toString());
                    //if path was valid, then save it to settings
                    if(Utility.pathAllowed(dir.getPath())){
                        destListView.getItems().clear();
                        settings.setDestinationDir(destPathTextField.getText().trim());
                    }   
                }
            }
        } 
    }

    /**
     * Click on source text field
     * @param event Event to process.
     */
    @FXML
    void sourceTextFieldMouseClick(MouseEvent event) {
        if(event.getButton().equals(MouseButton.PRIMARY)){
            if(event.getClickCount() == 2){
                if(ocrTask.getOcrIsRunning().get()){
                    statusLabel.setTextFill(Color.RED);
                    statusLabel.setText("A OCR task is already running!");
                    return;
                }
                DirectoryChooser directoryChooser = new DirectoryChooser();
                File dir = directoryChooser.showDialog(App.getPrimaryStage());
                if(dir != null){
                    sourcePathTextField.setText(dir.toPath().toString());
                    if(Utility.pathAllowed(dir.getPath())){
                        settings.setSourceDir(sourcePathTextField.getText().trim());
                        if(ocrTask.loadFiles(sourceListView, resultLabel, progressBar).get()){
                            statusLabel.setTextFill(Color.RED);
                            statusLabel.setText("A loading task is already running!");
                        }
                        else{
                            statusLabel.setTextFill(Color.BLACK);
                            statusLabel.setText("");
                        }
                    }   
                }
            }
        }  
    }

    /**
     * Click on load files button
     * @param event Event to process.
     */
    @FXML
    void loadFilesBtn(ActionEvent event) {
        if(ocrTask.getOcrIsRunning().get()){
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("A OCR task is already running!");
            return;
        }
        if(Utility.pathAllowed(sourcePathTextField.getText().trim())){
            settings.setSourceDir(sourcePathTextField.getText().trim());
            
            if(ocrTask.loadFiles(sourceListView, resultLabel, progressBar).get()){
                statusLabel.setTextFill(Color.RED);
                statusLabel.setText("A loading task is already running!");
            }
            else{
                statusLabel.setTextFill(Color.BLACK);
                statusLabel.setText("");
            }
        }else{
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("Source format is invalid or doesn't exist!");
        }
    }

    /**
     * Item in source list selected by mouse.
     * @param event Event to process.
     */
    @FXML
    void sourceListItemSelectedByMouse(MouseEvent event) {
        if(event.getButton().equals(MouseButton.PRIMARY)){
            int index = sourceListView.getSelectionModel().getSelectedIndex();
            if(event.getClickCount() == 2 && index != -1){
                openFileFromList(ocrTask.getSourceFileList(), index, sourceListView);
            }
        }
        else if(event.getButton().equals(MouseButton.SECONDARY)){
            int index = sourceListView.getSelectionModel().getSelectedIndex();
            if(event.getClickCount() == 2 && index != -1){
                selectFileFromList(ocrTask.getSourceFileList(), index, sourceListView);
            }
        }
    }

    /**
     * Item in destination list selected by mouse
     * @param event Event to process.
     */
    @FXML
    void destListItemSelectedByMouse(MouseEvent event) {
        if(event.getButton().equals(MouseButton.PRIMARY)){
            int index = destListView.getSelectionModel().getSelectedIndex();
            if(event.getClickCount() == 2 && index != -1){
                openFileFromList(ocrTask.getDstFileList(), index, destListView);
            }
        }
        else if(event.getButton().equals(MouseButton.SECONDARY)){
            int index = destListView.getSelectionModel().getSelectedIndex();
            if(event.getClickCount() == 2 && index != -1){
                selectFileFromList(ocrTask.getDstFileList(), index, destListView);
            }
        }
    }

    /**
     * Opens a file in Windows explorer based on index.
     * @param listFiles List of files.
     * @param index Index chosen.
     * @param listView ListView that's been selected.
     */
    private void openFileFromList(List<File> listFiles, int index, ListView<String> listView){
        String fileName = listView.getItems().get(index);
        for (File file : listFiles) {
            if(file.getName().equals(fileName)){
                try {
                    Runtime.getRuntime().exec("explorer.exe /open," + file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }  
    }

    /**
     * Select a file in Windows explorer based on an index.
     * @param listFiles List of files.
     * @param index Index chosen.
     * @param listView ListView that's been selected.
     */
    private void selectFileFromList(List<File> listFiles, int index, ListView<String> listView){
        String fileName = listView.getItems().get(index);
        for (File file : listFiles) {
            if(file.getName().equals(fileName)){
                try {
                    Runtime.getRuntime().exec("explorer.exe /select," + file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        } 
    }

    /**
     * Click on the image enhancer check box
     * @param event Event to process.
     */
    @FXML
    void imageEnhancerAction(ActionEvent event) {
        SecondaryController secController = SecondaryController.getInstance();
        if(settings.withEnhancer){
            secController.getImageEnhancingCheckBox().setSelected(false);
            settings.withEnhancer = false;
        }else{
            secController.getImageEnhancingCheckBox().setSelected(true);
            settings.withEnhancer = true;
        }
    }

    /**
     * Click on max performance check box
     * @param event Event to process.
     */
    @FXML
    void maxPerformanceAction(ActionEvent event) {
        if(!settings.cpuPerformance.equals(CPUPerformance.MAX)){
            settings.cpuPerformance = CPUPerformance.MAX;
            SecondaryController.getInstance().getCpuMaximumPerformanceRadioBox().setSelected(true);
        }else if(settings.cpuPerformance.equals(CPUPerformance.MAX)){
            settings.cpuPerformance = CPUPerformance.BALANCED;
            SecondaryController.getInstance().getCpuBalancedRadioBox().setSelected(true);
        }
    }
    

    /**
     * Click on OCR button
     * @param event Event to process.
     */
    @FXML
    void ocrAction(ActionEvent event) {
        if(PatternFinder.getInstance().getTokenList().isEmpty()){
            InputDialogs.alertDialog(AlertType.INFORMATION, "OCR", "OCR is useless to start", "There are no automations tokens added!");
            return;
        }
        if(ocrTask.getOcrIsRunning().get()){
            InputDialogs.alertDialog(AlertType.INFORMATION, "OCR", "OCR cannot be started", "Another OCR task is already running");
            return;
        }
        if(ocrTask.getLoadingIsRunning().get()){
            InputDialogs.alertDialog(AlertType.INFORMATION, "OCR", "OCR cannot be started", "Loading task is running. You can start OCR after loading!");
            return;
        }
        if(ocrTask.getSourceFileList().isEmpty()){
            InputDialogs.alertDialog(AlertType.INFORMATION, "OCR", "OCR cannot be started", "No Files are loaded to be processed!");
            return;
        }
        if(ocrTask.getDstPath() == null || ocrTask.getSourcePath() == null){
            InputDialogs.alertDialog(AlertType.INFORMATION, "OCR", "OCR cannot be started", "Source or Destination Directory is not specified yet!");
            return;
        }
        if(ocrTask.getDstPath().isBlank() || ocrTask.getSourcePath().isBlank()){
            InputDialogs.alertDialog(AlertType.INFORMATION, "OCR", "OCR cannot be started", "Source or Destination Directory is not specified yet!");
            return;
        }
        ocrTask.startOCRTask(sourceListView, destListView, resultLabel, progressBar, ocrBtn, progressIndicator, timerLabel);
    }

    /**
     * Click on open config directory menu item
     * @param event Event to process.
     */
    @FXML
    void openConfigDirAction(ActionEvent event) {
        Utility.openConfigDir();
    }

    /**
     * Click on open preferences' menu item.
     * @param event
     */
    @FXML
    void openPrefrencesAction(ActionEvent event) {
        App.getSecondaryStage().show();
    }

    /**
     * Click on quick menu item
     * @param event Event to process.
     */
    @FXML
    void quitAction(ActionEvent event) {
        if(OCRTask.getInstance().getOcrIsRunning().get()){
            Alert alert = new Alert(AlertType.CONFIRMATION);
            Stage stageAlert = (Stage) alert.getDialogPane().getScene().getWindow();
            stageAlert.getIcons().add(new Image(Objects.requireNonNull(App.class.getResourceAsStream("default.png"))));
            alert.setTitle("Neutrino");
            alert.setContentText("An OCR task is running. Close anyways?");
            alert.setHeaderText("Confirmation");
            alert.showAndWait().ifPresent((response) -> {
                if(response == ButtonType.OK) {
                    App.closeApp();
                }
            });
        }
        else App.closeApp();
    }

    /**
     * Click on add automation menu item
     * @param event Event to process.
     */
    @FXML
    void addAutomationAction(ActionEvent event){
        InputDialogs.addAutomationDialog(secondaryController.getAutomationListView());
    }

    @FXML
    void openHelpAction(ActionEvent event) {
        App.getHelpStage().show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        progressIndicator.setOpacity(0);
        statusLabel.setText("");

        //load on startup
        if(settings.destinationDir != null && settings.loadDestinationPathOnStartUp){
            destPathTextField.setText(settings.destinationDir);
        }else{
            settings.setDestinationDir(null);
        }       
        if(settings.sourceDir != null && settings.loadSourcePathOnStartUp){
            sourcePathTextField.setText(settings.sourceDir);
            ocrTask.loadFiles(sourceListView, resultLabel, progressBar);
        }else{
            settings.setSourceDir(null);
        }

        if(Utility.pathAllowed(sourcePathTextField.getText().trim())){
            loadFilesBtn.setDisable(false);
        }else{
            loadFilesBtn.setDisable(true);
        }
        
        //Image enhancing CheckBox
        pictureEnhancerCheckBox.setSelected(settings.withEnhancer);

        //Max performance Checkbox
        maxPerformanceChoiceBox.setSelected(settings.cpuPerformance.equals(CPUPerformance.MAX));

        //OCR language label
        languageLabel.setText("OCR Language: " + settings.ocrLanguage);
        resultLabel.setText("");


        final AtomicBoolean sourceFormat = new AtomicBoolean(true);
        final AtomicBoolean sourceValidity = new AtomicBoolean(true);
        final AtomicBoolean dstFormat = new AtomicBoolean(true);
        final AtomicBoolean dstValidity = new AtomicBoolean(true);

        sourcePathTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            newValue = newValue.trim();
            if(sourcePathTextField.getText().isBlank()){
                ocrBtn.setDisable(true);
                loadFilesBtn.setDisable(true);
                settings.setSourceDir(null);
                if(!dstFormat.get()){
                    statusLabel.setText("Destination directory's format is bad!");
                }
                else if(!dstValidity.get()){
                    statusLabel.setText("Destination directory doesn't exist!");
                }
                else{
                    ocrBtn.setDisable(false);
                    statusLabel.setTextFill(Color.BLACK);
                    statusLabel.setText("");
                }
                sourceFormat.set(true);
                sourceValidity.set(true);
            }
            else if(!Utility.isValidPathFormat(newValue)){
                ocrBtn.setDisable(true);
                loadFilesBtn.setDisable(true);
                statusLabel.setTextFill(Color.RED);
                if(!dstFormat.get()){
                    statusLabel.setText("Source\\Destination directory's format is bad!");
                }
                else if(!dstValidity.get()){
                    statusLabel.setText("Source directory's format is bad!\n"
                    +   "Destination directory doesn't exist!");
                }
                else {
                    statusLabel.setText("Source directory's format is bad!");
                }
                sourceFormat.set(false);
            }
            else if(!Utility.isValidPath(newValue)){
                ocrBtn.setDisable(true);
                loadFilesBtn.setDisable(true);
                statusLabel.setTextFill(Color.RED);
                if(!dstValidity.get()){
                    statusLabel.setText("Source\\Directory directory doesn't exist!");
                }
                else if(!dstFormat.get()){
                    statusLabel.setText("Destination directory's format is bad!\n"
                    +   "Source directory doesn't exist!");
                }
                else{
                    statusLabel.setText("Source directory doesn't exist!");
                }
                sourceValidity.set(false);
            }else{
                settings.setSourceDir(newValue);
                ocrBtn.setDisable(false);
                loadFilesBtn.setDisable(false);
                if(!dstFormat.get()){
                    statusLabel.setText("Destination directory's format is bad!");
                }
                else if(!dstValidity.get()){
                    statusLabel.setText("Destination directory doesn't exist!");
                }
                else{
                    statusLabel.setTextFill(Color.BLACK);
                    statusLabel.setText("");
                }
                sourceFormat.set(true);
                sourceValidity.set(true);
            }    
        });
        destPathTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            newValue = newValue.trim();
            if(destPathTextField.getText().isBlank()){
                settings.setDestinationDir(null);
                if(!sourceFormat.get()){
                    statusLabel.setText("Source directory's format is bad!");
                }
                else if(!sourceValidity.get()){
                    statusLabel.setText("Source directory doesn't exist!");
                }
                else{
                    statusLabel.setTextFill(Color.BLACK);
                    statusLabel.setText("");
                }
                dstFormat.set(true);
                dstValidity.set(true);
            }
            else if(!Utility.isValidPathFormat(newValue)){
                settings.setDestinationDir(null);
                statusLabel.setTextFill(Color.RED);
                if(!sourceFormat.get()){
                    statusLabel.setText("Source\\Destination directory's format is bad!");
                }
                else if(!sourceValidity.get()){
                    statusLabel.setText("Destination directory's format is bad!\n"
                    +   "    Source directory doesn't exist!");
                }
                else {
                    statusLabel.setText("Source directory's format is bad!");
                }
                dstFormat.set(false);
            }
            else if(!Utility.isValidPath(newValue)){
                settings.setDestinationDir(null);
                statusLabel.setTextFill(Color.RED);
                if(!sourceValidity.get()){
                    statusLabel.setText("Source\\Directory directory doesn't exist!");
                }
                else if(!sourceFormat.get()){
                    statusLabel.setText(" Source directory's format is bad!\n"
                    +   "Destination directory doesn't exist!");
                }
                else{
                    statusLabel.setText("Directory directory doesn't exist!");
                }
                dstValidity.set(false);
            }else{
                settings.setDestinationDir(newValue);
                ocrBtn.setDisable(false);
                if(!sourceFormat.get()){
                    statusLabel.setText("Source directory's format is bad!");
                }
                else if(!sourceValidity.get()){
                    statusLabel.setText("Source directory doesn't exist!");
                }
                else{
                    statusLabel.setTextFill(Color.BLACK);
                    statusLabel.setText("");
                }
                dstFormat.set(true);
                dstValidity.set(true);
            }    
        });

        //load quick menu items
        settings.quickmenuLanguagesList.forEach(e -> languageChoiceBox.getItems().add(e));

        languageChoiceBox.setOnAction(e -> {
            if(languageChoiceBox.getSelectionModel().getSelectedIndex() == -1)
                return;
            Language language = languageChoiceBox.getValue();
            settings.ocrLanguage = language;
            languageLabel.setText("OCR Language: " + language);
        });
    }
}