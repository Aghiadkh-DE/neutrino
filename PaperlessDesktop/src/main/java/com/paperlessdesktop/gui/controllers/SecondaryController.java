package com.paperlessdesktop.gui.controllers;

import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import com.paperlessdesktop.finder.automation.AutomationToken;
import com.paperlessdesktop.finder.automation.Language;
import com.paperlessdesktop.finder.PatternFinder;
import com.paperlessdesktop.gui.App;
import com.paperlessdesktop.ocrengine.CPUPerformance;
import com.paperlessdesktop.ocrengine.OCRTask;
import com.paperlessdesktop.util.InputDialogs;
import com.paperlessdesktop.util.Parameter;
import com.paperlessdesktop.util.Settings;
import com.paperlessdesktop.util.Utility;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import net.sourceforge.tess4j.ITessAPI.TessOcrEngineMode;

/**
 * Option GUI controller
 */
public class SecondaryController implements Initializable{
    private static SecondaryController instance;
    private final Settings settings = Settings.getInstance();

    private SecondaryController() {}

    public static SecondaryController getInstance(){
        return instance != null ? instance : (instance = new SecondaryController());
    }

    @FXML
    private Button addButton;

    @FXML
    private ListView<String> automationListView;

    @FXML
    private ListView<Language> availableLanguagesListView;

    @FXML
    private RadioButton balancedTrainedData;

    @FXML
    private RadioButton cpuBalancedRadioBox;

    @FXML
    private RadioButton cpuLowPerformanceRadioBox;

    @FXML
    private RadioButton cpuMaximumPerformanceRadioBox;

    @FXML
    private RadioButton defaultRadioBox;

    @FXML
    private RadioButton fastestTrainedData;

    @FXML
    private Button from_Avail_To_quickMenu_Btn;

    @FXML
    private Button from_quickMenu_To_availBtn;

    @FXML
    private Hyperlink hyperlink;

    @FXML
    private CheckBox imageEnhancingCheckBox;

    @FXML
    private RadioButton legacyOnlyRadioBox;

    @FXML
    private CheckBox loadDstOnStartUpCheckBox;

    @FXML
    private CheckBox loadSourceOnStartUpCheckBox;

    @FXML
    private RadioButton lstmOnlyRadioBox;

    @FXML
    private RadioButton lstm_legacy_RadioBox;

    @FXML
    private ListView<Language> quickLanguagesListView;

    @FXML
    private Button removeAutomationBtn;

    @FXML
    private Button resetApplicationButton;

    @FXML
    private TextField ruleFolderNameTextField;

    @FXML
    private TextField rulePatternTextField;

    @FXML
    private TextField ruleTitleTextField;

    @FXML
    private RadioButton slowestTrainedData;

    @FXML
    private Label automationResultLabel;

    @FXML
    private CheckBox moveCheckBox;

    @FXML
    private CheckBox shutdownCheckBox;

    @FXML
    private CheckBox startMinimizedCheckbox;

    @FXML
    private CheckBox openOnStartupCheckbox;

    @FXML
    void openOnStartupAction(ActionEvent event){
        settings.openOnStartup = openOnStartupCheckbox.isSelected();
        //TODO
    }

    @FXML
    void startMinimizedAction(ActionEvent event){
        settings.startMinimized = startMinimizedCheckbox.isSelected();
        //TODO
    }

    /**
     * Shutdown check box clicked
     * @param event Event to process.
     */
    @FXML
    void shutdownAction(ActionEvent event) {
        settings.shutdownAfterOCR.set(shutdownCheckBox.isSelected());
    }  

    /**
     * activate image enhancing check box clicked
     * @param event Event to process.
     */
    @FXML
    void activateImageEnhancingAction(ActionEvent event) {
        PrimaryController primaryController = PrimaryController.getInstance();
        if(settings.withEnhancer){
            primaryController.getPictureEnhancerCheckBox().setSelected(false);
            settings.withEnhancer = false;
        }else{
            primaryController.getPictureEnhancerCheckBox().setSelected(true);
            settings.withEnhancer = true;
        }
    }

    /**
     * Add automation button clicked
     * @param event Event to process.
     */
    @FXML
    void addAutomationAction(ActionEvent event) {
        PatternFinder patternFinder = PatternFinder.getInstance();
        if(InputDialogs.addAutomationFromTextField(patternFinder, ruleTitleTextField.getText(), 
        rulePatternTextField.getText(), ruleFolderNameTextField.getText(), automationListView)){
            ruleFolderNameTextField.setText("");
            rulePatternTextField.setText("");
            ruleTitleTextField.setText("");
        }
        addButton.setDisable(true);
    }

    /**
     * Balanced trained data radio button
     * @param event Event to process.
     */
    @FXML
    void balancedTrainedDataAction(ActionEvent event) {
        settings.trainedDataPath = Parameter.DEFAULT_TRAINED_DATA_PATH;
        lstmOnlyRadioBox.setDisable(false);
        legacyOnlyRadioBox.setDisable(false);
        lstm_legacy_RadioBox.setDisable(false);
        defaultRadioBox.setDisable(false);
    }

    /**
     * Config file hyperlink
     * @param event Event to process.
     */
    @FXML
    void configFileAction(ActionEvent event) {
        Utility.openConfigDir();
    }

    /**
     * cpu balanced radio button
     * @param event Event to process.
     */
    @FXML
    void cpuBalancedPerformanceAction(ActionEvent event) {
        settings.cpuPerformance = CPUPerformance.BALANCED;
    }

    /**
     * cpu low performance radio button
     * @param event Event to process.
     */
    @FXML
    void cpuLowPerformanceAction(ActionEvent event) {
        settings.cpuPerformance = CPUPerformance.LOW;
    }   

    /**
     * cpu max performance radio button
     * @param event Event to process.
     */
    @FXML
    void cpuMaxPerformanceAction(ActionEvent event) {
        settings.cpuPerformance = CPUPerformance.MAX;
        PrimaryController.getInstance().getMaxPerformanceChoiceBox().setSelected(true);
    }

    /**
     * default ocr engine mode
     * @param event Event to process.
     */
    @FXML
    void defaultOEMAction(ActionEvent event) {
        settings.ocrEngineMode = TessOcrEngineMode.OEM_DEFAULT;
    }

    /**
     * Reset application button 
     * @param event Event to process.
     */
    @FXML
    void resetApplicationAction(ActionEvent event) {
        Optional<ButtonType> optional = InputDialogs.alertDialog(AlertType.CONFIRMATION, "Neutrino", "Confirmation", """
                Resetting application will erase all data!

                Proceed?
                """);
        
        optional.ifPresent((response) -> {
            if(response == ButtonType.OK){
                if(OCRTask.getInstance().getOcrIsRunning().get()){
                    InputDialogs.alertDialog(AlertType.CONFIRMATION, "Neutrino", "Confirmation", "An OCR task is running. Reset and close anyways?")
                        .ifPresent((responseToSecondAlert) -> {
                            if(responseToSecondAlert == ButtonType.OK){
                                File file = new File(Parameter.NEUTRINO_DATA_PATH);
                                Utility.deleteDir(file.toPath());
                                App.restartApp();
                            }
                        });
                }else{
                    File file = new File(Parameter.NEUTRINO_DATA_PATH);
                    Utility.deleteDir(file.toPath());
                    App.restartApp();
                } 
            }    
        });    
    }

    /**
     * Fastest trained data radio button
     * @param event Event to process.
     */
    @FXML
    void fastestTrainedDataAction(ActionEvent event) {
        settings.trainedDataPath = Parameter.FASTEST_TRAINED_DATA_PATH;
        lstmOnlyRadioBox.setDisable(false);
        legacyOnlyRadioBox.setDisable(true);
        lstm_legacy_RadioBox.setDisable(true);
        defaultRadioBox.setDisable(false);
        if(legacyOnlyRadioBox.isSelected() || lstm_legacy_RadioBox.isSelected()){
            defaultRadioBox.setSelected(true);
        }
    }

    /**
     * Legacy only radio button
     * @param event Event to process.
     */
    @FXML
    void legacyOnlyAction(ActionEvent event) {
        settings.ocrEngineMode = TessOcrEngineMode.OEM_TESSERACT_ONLY;
    }

    /**
     * lstm and legacy radio button
     * @param event Event to process.
     */
    @FXML
    void lstm_LegacyAction(ActionEvent event) {
        settings.ocrEngineMode = TessOcrEngineMode.OEM_TESSERACT_LSTM_COMBINED;
    }

    /**
     * remove automation button 
     * @param event Event to process.
     *
     */
    @FXML
    void removeAutomationAction(ActionEvent event) {
        ObservableList<Integer> indices = automationListView.getSelectionModel().getSelectedIndices();
        PatternFinder.getInstance().removeAutomation(automationListView, indices);
    }

    /**
     * Slowest trained data radio button
     * @param event Event to process.
     */
    @FXML
    void slowestTrainedDataAction(ActionEvent event) {
        settings.trainedDataPath = Parameter.BEST_TRAINED_DATA_PATH;
        lstmOnlyRadioBox.setDisable(false);
        legacyOnlyRadioBox.setDisable(true);
        lstm_legacy_RadioBox.setDisable(true);
        defaultRadioBox.setDisable(false);
        if(legacyOnlyRadioBox.isSelected() || lstm_legacy_RadioBox.isSelected()){
            defaultRadioBox.setSelected(true);
        }
    }

    /**
     * TLSM radio button
     * @param event Event to process.
     */
    @FXML
    void lstmOnlyAction(ActionEvent event) {
        settings.ocrEngineMode = TessOcrEngineMode.OEM_LSTM_ONLY;
    }

    /**
     * avail -> Quick button
     * @param event Event to process.
     */
    @FXML
    void fromAvailableToQuickAction(ActionEvent event) {
        ObservableList<Language> languagesToRemove = availableLanguagesListView.getSelectionModel().getSelectedItems();

        OCRTask.getInstance().fromAvailableToQuick(languagesToRemove,
                availableLanguagesListView,
                quickLanguagesListView,
                PrimaryController.getInstance().getLanguageChoiceBox());
    }

    /**
     * Quick -> avail button
     * @param event Event to process.
     */
    @FXML
    void fromQuickToAvailableAction(ActionEvent event) {
        ObservableList<Language> languagesToRemove = quickLanguagesListView.getSelectionModel().getSelectedItems();

        OCRTask.getInstance().fromQuickToAvailable(languagesToRemove,
                availableLanguagesListView,
                quickLanguagesListView,
                PrimaryController.getInstance().getLanguageChoiceBox());
    }

    /**
     * move instead of copy check box
     * @param event Event to process.
     */
    @FXML
    void moveCheckBoxAction(ActionEvent event) {
        settings.cut_paste = moveCheckBox.isSelected();
    }

    /**
     * load dest path check box
     * @param event Event to process.
     */
    @FXML
    void loadDestPathOnStartUpAction(ActionEvent event) {
        settings.loadDestinationPathOnStartUp = loadDstOnStartUpCheckBox.isSelected();
    }

    /**
     * Load source path on startup check box
     * @param event Event to process.
     */
    @FXML
    void loadSourcePathOnStartUp(ActionEvent event) {
        settings.loadSourcePathOnStartUp = loadSourceOnStartUpCheckBox.isSelected();
    }

    /**
     * Mouse listener when an item in the automation list 
     * has been selected.
     * @param event Event to process.
     */
    @FXML
    void automationListMouseClickedAction(MouseEvent event) {
        PatternFinder finder = PatternFinder.getInstance();

        if(event.getButton().equals(MouseButton.PRIMARY)){
            if(event.getClickCount() == 2){
                AutomationToken token = finder.getAutomationToken(automationListView.getSelectionModel().getSelectedIndex());
                InputDialogs.editAutomationTokenDialog(automationListView, token);
            }
        }
    }
    
    public ListView<String> getAutomationListView() {
        return automationListView;
    }

    public Button getAddButton() {
        return addButton;
    }

    public CheckBox getImageEnhancingCheckBox() {
        return imageEnhancingCheckBox; 
    }

    public RadioButton getCpuMaximumPerformanceRadioBox() {
        return cpuMaximumPerformanceRadioBox;
    }

    public RadioButton getCpuBalancedRadioBox() {
        return cpuBalancedRadioBox;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        automationListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        availableLanguagesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        quickLanguagesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


        addButton.setDisable(true);
        removeAutomationBtn.setDisable(true);
        ruleTitleTextField.requestFocus();

        from_Avail_To_quickMenu_Btn.setDisable(true);
        from_quickMenu_To_availBtn.setDisable(true);

        //Image enhancer
        imageEnhancingCheckBox.setSelected(settings.withEnhancer);

        //CPU components
        if(settings.cpuPerformance.equals(CPUPerformance.MAX)){
            cpuMaximumPerformanceRadioBox.setSelected(true);
        }else if(settings.cpuPerformance.equals(CPUPerformance.BALANCED)){
            cpuBalancedRadioBox.setSelected(true);
        }else if(settings.cpuPerformance.equals(CPUPerformance.LOW)){
            cpuLowPerformanceRadioBox.setSelected(true);
        }

        //Trained data components
        if(settings.trainedDataPath.equals(Parameter.DEFAULT_TRAINED_DATA_PATH)){
            balancedTrainedData.setSelected(true);
            lstmOnlyRadioBox.setDisable(false);
            legacyOnlyRadioBox.setDisable(false);
            lstm_legacy_RadioBox.setDisable(false);
            defaultRadioBox.setDisable(false);
        }else if(settings.trainedDataPath.equals(Parameter.FASTEST_TRAINED_DATA_PATH)){
            fastestTrainedData.setSelected(true);
            lstmOnlyRadioBox.setDisable(false);
            legacyOnlyRadioBox.setDisable(true);
            lstm_legacy_RadioBox.setDisable(true);
            defaultRadioBox.setDisable(false);
        }else if(settings.trainedDataPath.equals(Parameter.BEST_TRAINED_DATA_PATH)){
            slowestTrainedData.setSelected(true);
            lstmOnlyRadioBox.setDisable(false);
            legacyOnlyRadioBox.setDisable(true);
            lstm_legacy_RadioBox.setDisable(true);
            defaultRadioBox.setDisable(false);
        }

        //Engine mode components
        if(settings.ocrEngineMode == TessOcrEngineMode.OEM_DEFAULT){
            defaultRadioBox.setSelected(true);
        }else if(settings.ocrEngineMode == TessOcrEngineMode.OEM_LSTM_ONLY){
            lstmOnlyRadioBox.setSelected(true);
        }else if(settings.ocrEngineMode == TessOcrEngineMode.OEM_TESSERACT_LSTM_COMBINED){
            lstm_legacy_RadioBox.setSelected(true);
        }else if(settings.ocrEngineMode == TessOcrEngineMode.OEM_TESSERACT_ONLY){
            legacyOnlyRadioBox.setSelected(true);
        }

        //Cut paste checkbox
        moveCheckBox.setSelected(settings.cut_paste);

        //Automation listview
        PatternFinder.getInstance().updateAutomationsListAndGUI(automationListView);

        ToggleGroup cpuGroupRadio = new ToggleGroup();
        cpuMaximumPerformanceRadioBox.setToggleGroup(cpuGroupRadio);
        cpuBalancedRadioBox.setToggleGroup(cpuGroupRadio);
        cpuLowPerformanceRadioBox.setToggleGroup(cpuGroupRadio);

        ToggleGroup ocrAlgorithmGroupRadio = new ToggleGroup();
        lstmOnlyRadioBox.setToggleGroup(ocrAlgorithmGroupRadio);
        legacyOnlyRadioBox.setToggleGroup(ocrAlgorithmGroupRadio);
        lstm_legacy_RadioBox.setToggleGroup(ocrAlgorithmGroupRadio);
        defaultRadioBox.setToggleGroup(ocrAlgorithmGroupRadio);

        ToggleGroup trainedDataGroupRadio = new ToggleGroup();
        fastestTrainedData.setToggleGroup(trainedDataGroupRadio);
        balancedTrainedData.setToggleGroup(trainedDataGroupRadio);
        slowestTrainedData.setToggleGroup(trainedDataGroupRadio);

        PatternFinder patternFinder = PatternFinder.getInstance();
        ruleTitleTextField.textProperty().addListener((observable, oldValue, newValue) ->
            InputDialogs.checkTextFieldForTitle(patternFinder, newValue, addButton, ruleTitleTextField, rulePatternTextField, 
            ruleFolderNameTextField, automationResultLabel, false, null)
        );
        rulePatternTextField.textProperty().addListener((observable, oldValue, newValue) ->
            InputDialogs.checkTextFieldsForPattern(patternFinder, newValue, addButton, ruleTitleTextField, rulePatternTextField, 
            ruleFolderNameTextField, automationResultLabel, false, null)
        );
        ruleFolderNameTextField.textProperty().addListener((observable, oldValue, newValue) ->
            InputDialogs.checkTextFieldsForFolderName(patternFinder, newValue, addButton, ruleTitleTextField, rulePatternTextField, 
            ruleFolderNameTextField, automationResultLabel, false, null)
        );

        automationListView.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) ->{
            ObservableList<Integer> selectedIndices = automationListView.getSelectionModel().getSelectedIndices();
            removeAutomationBtn.setDisable(automationListView.getSelectionModel().getSelectedIndex() == -1);
            if(selectedIndices.size() > 1)
                removeAutomationBtn.setText("Remove Selection");
            else
                removeAutomationBtn.setText("Remove");
        });


        availableLanguagesListView.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) ->{
            from_Avail_To_quickMenu_Btn.setDisable(availableLanguagesListView.getSelectionModel().getSelectedIndex() == -1);
        });

        quickLanguagesListView.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) ->
            from_quickMenu_To_availBtn.setDisable(quickLanguagesListView.getSelectionModel().getSelectedIndex() == -1)
        );

        Settings.getInstance().availableLanguagesList.forEach(e -> availableLanguagesListView.getItems().add(e));
        Settings.getInstance().quickmenuLanguagesList.forEach(e -> quickLanguagesListView.getItems().add(e));

        //load startup option
        loadDstOnStartUpCheckBox.setSelected(settings.loadDestinationPathOnStartUp);
        loadSourceOnStartUpCheckBox.setSelected(settings.loadSourcePathOnStartUp);

        //shutdown component
        shutdownCheckBox.setSelected(settings.shutdownAfterOCR.get());

        //Start minimized component
        startMinimizedCheckbox.setSelected(settings.startMinimized);

        //Open on startup component
        openOnStartupCheckbox.setSelected(settings.openOnStartup);
    }
}