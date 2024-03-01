package com.paperlessdesktop.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.paperlessdesktop.finder.automation.AutomationToken;
import com.paperlessdesktop.finder.PatternFinder;
import com.paperlessdesktop.gui.App;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public final class InputDialogs {

    private InputDialogs(){}

    private static final PatternFinder patternFinder = PatternFinder.getInstance();

    public static void editAutomationTokenDialog(ListView<String> automationListView, AutomationToken token){
        Dialog<ArrayList<String>> dialog = new Dialog<>();
        dialog.setTitle("Edit Automation");

        dialog.setHeaderText("Edit Automation from the list");
        dialog.setWidth(500);

        // Set the button types.
        ButtonType addButton = new ButtonType("Add", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        Node addButtonNode = dialog.getDialogPane().lookupButton(addButton);
        addButtonNode.setDisable(true);
        GridPane gridPane = new GridPane();
        gridPane.getColumnConstraints().add(new ColumnConstraints(300));
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 50, 10, 10));
    
        TextField title = new TextField();
        title.setPromptText("Rule's Title (optional)");
        TextField pattern = new TextField();
        pattern.setPromptText("Rule's Pattern. e.g.: bank, Max Mustermann");
        TextField folderName = new TextField();
        folderName.setPromptText("Rule's folder name (optional)");
        gridPane.add(title, 0, 0);
        gridPane.add(pattern, 0, 1);
        gridPane.add(folderName, 0, 2);

        Label status = new Label();
        status.setAlignment(Pos.CENTER_RIGHT);
        status.setFont(Font.font(15));

        gridPane.add(status, 0, 3);
        VBox vBox = new VBox(gridPane, status);
        vBox.setPadding(new Insets(20, 0, 10, 10));
        dialog.getDialogPane().setContent(vBox);

        title.setText(token.getTitle());
        folderName.setText(token.getFolderName());
        pattern.setText(token.getPatternStr());

        folderName.textProperty().addListener((observable, oldValue, newValue) -> {
            checkTextFieldsForFolderName(patternFinder, newValue, (Button)addButtonNode, title, pattern, 
            folderName, status, true, token);
        });

        title.textProperty().addListener((observable, oldValue, newValue) -> {
            checkTextFieldForTitle(patternFinder, newValue, (Button)addButtonNode, title, pattern, 
            folderName, status,true, token);
        });

        pattern.textProperty().addListener((observable, oldValue, newValue) -> {
            checkTextFieldsForPattern(patternFinder, newValue, (Button)addButtonNode, title, pattern, 
            folderName, status,true, token);
        });

        // Request focus on the username field by default.
        Platform.runLater(title::requestFocus);
        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButton) {
                return new ArrayList<>(List.of(title.getText(), pattern.getText(), folderName.getText()));
            }
            return null;
        });
    
        Optional<ArrayList<String>> result = dialog.showAndWait();
        result.ifPresent((list) -> {
            editAutomationTokenFromTextField(patternFinder, list.get(0), list.get(1), list.get(2), automationListView, token);
        });
    }


    public static void addAutomationDialog(ListView<String> automationListView){
        Dialog<ArrayList<String>> dialog = new Dialog<>();
        dialog.setTitle("Add Automation");

        dialog.setHeaderText("Add Automation to the list");
        dialog.setWidth(500);

    
        // Set the button types.
        ButtonType addButton = new ButtonType("Add", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        Node addButtonNode = dialog.getDialogPane().lookupButton(addButton);
        addButtonNode.setDisable(true);
        GridPane gridPane = new GridPane();
        gridPane.getColumnConstraints().add(new ColumnConstraints(300));
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 50, 10, 10));
    
        TextField title = new TextField();
        title.setPromptText("Rule's Title (optional)");
        TextField pattern = new TextField();
        pattern.setPromptText("Rule's Pattern. e.g.: bank, Max Mustermann");
        TextField folderName = new TextField();
        folderName.setPromptText("Rule's folder name (optional)");
        gridPane.add(title, 0, 0);
        gridPane.add(pattern, 0, 1);
        gridPane.add(folderName, 0, 2);

        Label status = new Label();
        status.setAlignment(Pos.CENTER_RIGHT);
        status.setFont(Font.font(15));

        gridPane.add(status, 0, 3);
        VBox vBox = new VBox(gridPane, status);
        vBox.setPadding(new Insets(20, 0, 10, 10));
        dialog.getDialogPane().setContent(vBox);

        folderName.textProperty().addListener((observable, oldValue, newValue) -> {
            checkTextFieldsForFolderName(patternFinder, newValue, (Button)addButtonNode, title, pattern, 
            folderName, status, false, null);
        });

        title.textProperty().addListener((observable, oldValue, newValue) -> {
            checkTextFieldForTitle(patternFinder, newValue, (Button)addButtonNode, title, pattern, 
            folderName, status, false, null);
        });

        pattern.textProperty().addListener((observable, oldValue, newValue) -> {
            checkTextFieldsForPattern(patternFinder, newValue, (Button)addButtonNode, title, pattern, 
            folderName, status, false, null);
        });
    
        // Request focus on the username field by default.
        Platform.runLater(title::requestFocus);
        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButton) {
                return new ArrayList<>(List.of(title.getText(), pattern.getText(), folderName.getText()));
            }
            return null;
        });
    
        Optional<ArrayList<String>> result = dialog.showAndWait();
        result.ifPresent((list) -> {
            addAutomationFromTextField(patternFinder, list.get(0), list.get(1), list.get(2), automationListView);
        });
    }

    public static void checkTextFieldsForPattern(PatternFinder patternFinder,
                                                 String newValue,
                                                 Button button,
                                                 TextField titleTextField,
                                                 TextField patternTextField,
                                                 TextField folderNameTextField,
                                                 Label statusLabel,
                                                 boolean withException,
                                                 AutomationToken token){

        button.setDisable(newValue.trim().isEmpty());
        
        if(!withException){
            if(patternFinder.tokenExistsPattern(patternTextField.getText())){
                button.setDisable(true);
                statusLabel.setTextFill(Color.RED);
                statusLabel.setText("Pattern already exists!");
            }else if(!newValue.isBlank()){
                if(patternFinder.folderNameExists(folderNameTextField.getText().trim()) && !patternTextField.getText().isBlank()){
                    statusLabel.setTextFill(Color.RED);
                    statusLabel.setText("Pattern already exists!");
                }
                else if(patternFinder.titleExists(titleTextField.getText().trim()) && !titleTextField.getText().isBlank()){
                    statusLabel.setTextFill(Color.RED);
                    statusLabel.setText("Title already exists");
                }
                else{
                    statusLabel.setText("");
                    if(!patternTextField.getText().isBlank()){
                        button.setDisable(false);
                    }
                }  
            }  
        }
        else{
            if(patternFinder.patternExistsElseWhereExcept(patternTextField.getText(), token)){
                button.setDisable(true);
                statusLabel.setTextFill(Color.RED);
                statusLabel.setText("Pattern already exists!");
            }else if(!newValue.isBlank()){
                if(patternFinder.folderNameExistsExcept(folderNameTextField.getText().trim(), token) && !patternTextField.getText().isBlank()){
                    statusLabel.setTextFill(Color.RED);
                    statusLabel.setText("Pattern already exists!");
                }
                else if(patternFinder.titleExistsExcept(titleTextField.getText().trim(), token) && !titleTextField.getText().isBlank()){
                    statusLabel.setTextFill(Color.RED);
                    statusLabel.setText("Title already exists");
                }
                else{
                    statusLabel.setText("");
                    if(!patternTextField.getText().isBlank()){
                        button.setDisable(false);
                    }
                }  
            } 
        }              
    }
    
    public static void checkTextFieldsForFolderName(PatternFinder patternFinder,
                                                    String newValue,
                                                    Button button,
                                                    TextField titleTextField,
                                                    TextField patternTextField,
                                                    TextField folderNameTextField,
                                                    Label statusLabel,
                                                    boolean withException,
                                                    AutomationToken token){
        if(!withException){
            if(patternFinder.folderNameExists(newValue)){
                button.setDisable(true);
                statusLabel.setTextFill(Color.RED);
                statusLabel.setText("Folder name already exists");
            }
            else{
                if(patternFinder.tokenExistsPattern(patternTextField.getText().trim())){
                    statusLabel.setTextFill(Color.RED);
                    statusLabel.setText("Pattern already exists!");
                }
                else if(patternFinder.titleExists(titleTextField.getText().trim())){
                    statusLabel.setTextFill(Color.RED);
                    statusLabel.setText("Title already exists");
                }
                else{
                    statusLabel.setText("");
                    if(!patternTextField.getText().isBlank()){
                        button.setDisable(false);
                    }
                }         
            }
        }
        else{
            if(patternFinder.folderNameExistsExcept(newValue, token)){
                button.setDisable(true);
                statusLabel.setTextFill(Color.RED);
                statusLabel.setText("Folder name already exists");
            }
            else{
                if(patternFinder.patternExistsElseWhereExcept(patternTextField.getText().trim(), token)){
                    statusLabel.setTextFill(Color.RED);
                    statusLabel.setText("Pattern already exists!");
                }
                else if(patternFinder.titleExistsExcept(titleTextField.getText().trim(), token)){
                    statusLabel.setTextFill(Color.RED);
                    statusLabel.setText("Title already exists");
                }
                else{
                    statusLabel.setText("");
                    if(!patternTextField.getText().isBlank()){
                        button.setDisable(false);
                    }
                }         
            }
        }
        
        
    }

    public static void checkTextFieldForTitle(PatternFinder patternFinder,
                                              String newValue, Button button,
                                              TextField titleTextField,
                                              TextField patternTextField,
                                              TextField folderNameTextField,
                                              Label statusLabel,
                                              boolean withException,
                                              AutomationToken token){

        if(!withException){
            if(patternFinder.titleExists(newValue)){
                button.setDisable(true);
                statusLabel.setTextFill(Color.RED);
                statusLabel.setText("Title already exists");
            }else{
                if(patternFinder.tokenExistsPattern(patternTextField.getText().trim()) && !patternTextField.getText().isBlank()){
                    statusLabel.setTextFill(Color.RED);
                    statusLabel.setText("Pattern already exists!");
                }
                else if(patternFinder.folderNameExists(folderNameTextField.getText().trim()) && !folderNameTextField.getText().isBlank()){
                    statusLabel.setTextFill(Color.RED);
                    statusLabel.setText("Folder name already exists");
                }
                else if(newValue.isBlank() && !patternTextField.getText().isBlank() && !folderNameTextField.getText().isBlank()){
                    button.setDisable(true);
                }
                else{
                    statusLabel.setText("");
                    if(!patternTextField.getText().isBlank()){
                        button.setDisable(false);
                    }
                }  
            }
        }
        else{
            if(patternFinder.titleExistsExcept(newValue, token)){
                button.setDisable(true);
                statusLabel.setTextFill(Color.RED);
                statusLabel.setText("Title already exists");
            }else{
                if(patternFinder.patternExistsElseWhereExcept(patternTextField.getText().trim(), token) && !patternTextField.getText().isBlank()){
                    statusLabel.setTextFill(Color.RED);
                    statusLabel.setText("Pattern already exists!");
                }
                else if(patternFinder.folderNameExistsExcept(folderNameTextField.getText().trim(), token) && !folderNameTextField.getText().isBlank()){
                    statusLabel.setTextFill(Color.RED);
                    statusLabel.setText("Folder name already exists");
                }
                else if(newValue.isBlank() && !patternTextField.getText().isBlank() && !folderNameTextField.getText().isBlank()){
                    button.setDisable(true);
                }
                else{
                    statusLabel.setText("");
                    if(!patternTextField.getText().isBlank()){
                        button.setDisable(false);
                    }
                }  
            }
        }   
    }

    public static boolean editAutomationTokenFromTextField(PatternFinder patternFinder, String title, String pattern, String folderName, ListView<String> listView, AutomationToken tokenToEdit){
        if(!pattern.isBlank() && !title.isBlank() && !folderName.isBlank()){
            patternFinder.editToken(pattern.trim(), title.trim(), folderName, listView, tokenToEdit);
            return true;
        }
        else if(folderName.isBlank() && !pattern.isBlank() && !title.isBlank()){
            patternFinder.editToken(pattern.trim(), title.trim(), folderName, listView, tokenToEdit);
            return true;
        }
        else if(!pattern.isBlank() && title.isBlank() && folderName.isBlank()){
            patternFinder.editToken(pattern.trim(), title.trim(), folderName, listView, tokenToEdit);
            return true;
        }
        return false;
    }

    public static boolean addAutomationFromTextField(PatternFinder patternFinder, String title, String pattern, String folderName, ListView<String> listView){
        if(!pattern.isBlank() && !title.isBlank() && !folderName.isBlank()){
            patternFinder.addToken(pattern.trim(), title.trim(), folderName.trim(), listView);
            return true;
        }
        else if(folderName.isBlank() && !pattern.isBlank() && !title.isBlank()){
            patternFinder.addToken(pattern.trim(), title.trim(), listView);
            return true;
        }
        else if(!pattern.isBlank() && title.isBlank() && folderName.isBlank()){
            patternFinder.addToken(pattern.trim(), listView);
            return true;
        }
        return false;
    }

    public static Optional<ButtonType> alertDialog(AlertType type, String title, String header, String content){
        Alert dialog = new Alert(type);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(content);
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Objects.requireNonNull(App.class.getResourceAsStream("default.png"))));
        
        return dialog.showAndWait();
    }

    public static Optional<ButtonType> alertDialog(AlertType type, String title, String header, String content, ButtonType ... buttonTypes){
        Alert dialog = new Alert(type, content, buttonTypes);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Objects.requireNonNull(App.class.getResourceAsStream("default.png"))));

        return dialog.showAndWait();
    }

    public static void exceptionDialog(Exception e, String content){
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("An Error has occurred");
        alert.setContentText(content);

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Objects.requireNonNull(App.class.getResourceAsStream("default.png"))));

        // Create expandable Exception.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }
}
