package com.metait.javafxmlfileI18convert;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.scene.control.DialogEvent;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.WindowEvent;
import javafx.stage.Modality;
import javafx.scene.layout.Region;
import javafx.scene.input.MouseButton;
import javafx.scene.Node;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.metait.javafxmlfileI18convert.data.convert.JavaFxmlFileConvetI18;

public class JavaFxmlFileControllerI18 {

    @FXML
    private Button buttonOpenFile;
    @FXML
    private Button buttonConvert;
    @FXML
    private CheckBox checkBoxShowInput;
    @FXML
    private Button buttonWriteFxml;
    @FXML
    private Button buttonWriteProperties;
    @FXML
    private Button buttonWriteProperties2;
    @FXML
    private TextArea textAreaFxml;
    @FXML
    private TextArea textAreaProperties;
    @FXML
    private Label labelMsg;
    @FXML
    private Button buttonOldVariables;
    @FXML
    private Button buttonOpenProp;
    @FXML
    private CheckBox checkBoxEditPropFiles;
    @FXML
    private Button buttonOpenProp2;
    @FXML
    private Button buttonClear;
    @FXML
    private ToggleButton toggleButtonSearch;
    @FXML
    private TextField textFieldSearch;
    @FXML
    private Button buttonSearch;
    @FXML
    private Button buttonPrevious;
    @FXML
    private Button buttonNext;
    @FXML
    private CheckBox checkBoxInCase;
    @FXML
    private MenuItem menuItemHelp;

    private class SearchPosition {
        public int iStart = -1;
        public int iEnd = -1;
    }

    private WebView webViewHelp;
    private int iIndSearchPostion = -1;
    private SearchPosition[] arrfounded = null;
    private TextArea searcTextArea = null;
    private Stage primaryStage;
    private boolean bConvertingFiles = false;
    private FileChooser fileFxmlChooser = new FileChooser();
    private FileChooser filePropertiesChooser = new FileChooser();
    private final String [] arrFileExtenstions = new String[]
            {".fxml"};
    private final String [] arrFileExtenstionsProperties = new String[]
            {".properties"};
    private File fFxmlChooser = null;
    private File fPropertiesChooser = null;

    private File selectedFxmlFile = null;
    private File propertiesFile = null;
    private File propertiesFile2 = null;

    private JavaFxmlFileConvetI18 converter = null;
    private boolean bConvertedData = false;
    private String strModifiedFXml = null;
    private String strFXml = null;
    private List<String> oldLangVariables = null;
    private boolean bExistingOldVariables = false;
    private String cnstCRLines = "\n\n\n\n\n\n\n";

    private boolean buttonOpenFile_disabled = false;
    private boolean buttonOpenProp_disabled = false;
    private boolean buttonConvert_disabled = false;
    private boolean buttonOldVariables_disabled = false;
    private boolean checkBoxEditPropFiles_disabled = false;
    private boolean buttonOpenProp2_disabled = false;
    private boolean buttonWriteFxml_disabled = false;
    private boolean buttonWriteProperties_disabled = false;
    private boolean buttonWriteProperties2_disabled = false;
    private boolean checkBoxShowInput_disabled = false;
    private boolean toggleButtntonSearch_disabled = false;
    private boolean buttonSearch_disabled = false;
    private boolean textFieldSearch_disabled = false;
    private String strCheckReport = null;
    private boolean bReportDialogShowing = false;
    private boolean bOldVariablesDialogShowing = false;
    private Dialog dialogReport = null;
    private Dialog dialogOldVariables = null;
    private boolean bTextAreaFxmlChanged = false;
    private boolean bTextAreaPropertiesChanged = false;
    private String strPropertyFxml = null;
    private String strProperty = null;
    private String strPropertyEditPropFiles = null;
    private boolean bFxmlFileWrote = false;
    private boolean bPropertyFileWrote = false;
    private boolean checkBoxEditPropFiles_under_program_change = false;
    private boolean bCheckBoxShowInput_under_program_change = false;
    private boolean buttonSearch_disabled_init = false;
    private boolean textFieldSearch_disabled_init = false;
    private boolean checkBoxShowInput_disabled_init = false;
    private boolean togleButtonSearch_disabled_init = false;
    private boolean buttonOpenFile_disabled_init = false;
    private boolean buttonOpenProp_disabled_init = false;
    private boolean buttonConvert_disabled_init = false;
    private boolean buttonOldVariables_disabled_init = false;
    private boolean checkBoxEditPropFiles_disabled_init = false;
    private boolean buttonOpenProp2_disabled_init = false;
    private boolean buttonWriteFxml_disabled_init = false;
    private boolean buttonWriteProperties_disabled_init = false;
    private boolean buttonWriteProperties2_disabled_init = false;

    public void setPrimaryStage(Stage stage)
    {
        primaryStage = stage;
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                if (appIsClosing()) {
                    Platform.exit();
                    System.exit(0);
                }
            }
        });
    }

    private boolean appIsClosing()
    {
        System.out.println("appIsClosing()");
        if (bConvertingFiles)
        {
            System.out.println("ask to cloase appIsClosing()??");
        }
        if (!saveConfigData())
            return false;
        Platform.exit();
        System.exit(0);
        return true;
    }

    private boolean isErrorInOldPropertyText(String strTextAreaProperties, String strPropData)
    {
        boolean ret = false;
        if (strTextAreaProperties != null && strTextAreaProperties.trim().length() != 0
                && strPropData != null && strPropData.trim().length() != 0 ) {
            StringBuffer sbError = new StringBuffer();
            String patternString = "^(\\w+)=";
            Pattern pattern = Pattern.compile(patternString, Pattern.MULTILINE);
            Matcher matcher = pattern.matcher(strTextAreaProperties);
            Matcher matcher2;
            String strVariable;
            int iStart, iEnd, iMax = strTextAreaProperties.length(), ind;

            while (matcher.find()) {
                iStart = matcher.start();
                iEnd = matcher.end();
                strVariable = matcher.group(1);
                ind = strPropData.indexOf(strVariable);
                if (ind > -1)
                    sbError.append("Variable " +strVariable +" exists all ready in textarea property data!\n");
            }

            ret = sbError.toString().trim().length() != 0;
            if (ret)
                strCheckReport = sbError.toString();
        }
        return ret;
    }

    @FXML
    protected  void pressedButtonOpenProp()
    {
        System.out.println("pressedButtonOpenProp");
        if (this.filePropertiesChooser != null)
        {
            if (fPropertiesChooser != null)
                filePropertiesChooser.setInitialDirectory(fPropertiesChooser);
            else
            if (fFxmlChooser != null)
                filePropertiesChooser.setInitialDirectory(fFxmlChooser);
            else
                filePropertiesChooser.setInitialDirectory(new File("."));
        }
        filePropertiesChooser.setTitle("Lang properties file to read");
        File selectedFile = filePropertiesChooser.showOpenDialog(this.primaryStage);
        if (selectedFile != null && selectedFile.exists()) {
            if (propertiesFile2 != null && propertiesFile2.getAbsolutePath().equals(selectedFile.getAbsolutePath()))
            {
                String strTitle = "Same files";
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle(strTitle);
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                String strHeader = "You had try to open same file twice!";
                alert.setHeaderText(strHeader);
                String strContent ="File: " +selectedFile.getAbsolutePath() +" Not to open this file?";
                alert.setContentText(strContent);

                String strOK = "Cancel";
                ((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setText(strOK);
                alert.show();
                return;
            }
            fPropertiesChooser = selectedFile.getParentFile();
            propertiesFile = selectedFile;

            try {
                    String strPropData = converter.getStringOfFile(selectedFile);
                    if (bExistingOldVariables) {
                        buttonConvert.setDisable(true);
                        boolean bPropTextExist = textAreaProperties.getText().trim().length() != 0;
                        if (bPropTextExist)
                        {
                            if (isErrorInOldPropertyText(textAreaProperties.getText().trim(), strPropData)) {
                                showReportDialog();
                                return;
                            }
                        }
                        buttonSearch.setDisable(false);
                        textAreaProperties.setText(textAreaProperties.getText().trim()
                                .replaceAll("\n{2,}", "\n")+"\n" +strPropData +cnstCRLines);
                        buttonConvert.setDisable(true);
                        if (bPropTextExist)
                            buttonOpenProp.setDisable(true);
                        strCheckReport = converter.createCheckReport(textAreaFxml.getText(), textAreaProperties.getText());
                        if (strCheckReport == null || strCheckReport.trim().length() != 0)
                        {
                            // System.out.println("kissa")
                            if (bReportDialogShowing && dialogReport != null)
                                dialogReport.close();
                            if (bOldVariablesDialogShowing && dialogOldVariables != null)
                                dialogOldVariables.close();
                            showReportDialog();
                        }
                    }
                    else
                    {
                        textAreaProperties.setText(strPropData.trim().replaceAll("\n{2,}","\n") +cnstCRLines);
                    }

                if (checkBoxEditPropFiles.isSelected())
                {
                    bTextAreaPropertiesChanged = false;
                    buttonWriteProperties.setDisable(true);
                    strProperty = null;
                    textAreaProperties.textProperty().addListener(new ChangeListener<String>() {
                        @Override
                        public void changed(ObservableValue<? extends String> observableValue, String oldStr, String newStr) {
                            bTextAreaPropertiesChanged = true;
                            if (buttonWriteProperties.isDisable())
                                buttonWriteProperties.setDisable(false);
                            if (!buttonOpenProp.isDisable())
                                buttonOpenProp.setDisable(true);
                            if (!checkBoxEditPropFiles.isDisable())
                            {
                                underAppProgramChange();
                                checkBoxEditPropFiles.setDisable(true);
                                noneAppProgramChange();
                            }
                            strProperty = newStr;
                        }
                    });

                    textAreaProperties.setOnMouseClicked(evt -> {
                        if (evt.getButton() == MouseButton.PRIMARY) {
                            // check, if click was inside the content area
                            Node n = evt.getPickResult().getIntersectedNode();
                            while (n != textAreaProperties) {
                                if (n.getStyleClass().contains("content")) {
                                    // find previous/next line break
                                    int caretPosition = textAreaProperties.getCaretPosition();
                                    String text = textAreaProperties.getText();
                                    int lineBreak1 = text.lastIndexOf('\n', caretPosition - 1);
                                    if (lineBreak1 == -1)
                                        lineBreak1 = 0;
                                    int lineBreak2 = text.indexOf('\n', caretPosition);
                                    if (lineBreak2 < 0) {
                                        // if no more line breaks are found, select to end of text
                                        lineBreak2 = text.length();
                                    }

                                    textAreaProperties.selectRange(lineBreak1, lineBreak2);
                                    String selectedText = textAreaProperties.getSelectedText();
                                    if (selectedText != null && textAreaFxml != null)
                                    {
                                        String textFxml = textAreaFxml.getText();
                                        if (textFxml != null)
                                        {
                                            int indVar = selectedText.indexOf("=");
                                            if (indVar > -1) {
                                                String strVariable = selectedText.substring(0, indVar);
                                                strVariable = strVariable.replaceAll("\n","");
                                                int ind = textFxml.indexOf(strVariable);
                                                if (ind > -1) {
                                                    textAreaFxml.selectRange(ind, ind + strVariable.length());
                                                }
                                                else {
                                                    deselect(textAreaFxml);
                                                /*
                                                double scrollPosition = textAreaFxml.getScrollTop();
                                                textAreaFxml.setScrollTop(Double.MAX_VALUE);
                                                 */
                                                }
                                            }
                                            else
                                            {
                                                deselect(textAreaFxml);
                                            }
                                        }
                                    }
                                    evt.consume();
                                    break;
                                }
                                n = n.getParent();
                            }
                        }
                    });
                }
                else
                {
                    addFxmlForPropertyTextAreaListener();
                }
                buttonSearch.setDisable(false);
            }catch (Exception e){
                    e.printStackTrace();
                    labelMsg.setText(e.getMessage());
            }
        }
    }

    private void readButtonRowEnabledOrDisabled()
    {
        toggleButtntonSearch_disabled = toggleButtonSearch.isDisable();
        buttonSearch_disabled = buttonSearch.isDisable();
        textFieldSearch_disabled = textFieldSearch.isDisable();

        checkBoxShowInput_disabled = checkBoxShowInput.isDisable();
        buttonOpenFile_disabled = buttonOpenFile.isDisabled();
        buttonOpenProp_disabled = buttonOpenProp.isDisabled();
        buttonConvert_disabled = buttonConvert.isDisabled();
        buttonOldVariables_disabled = buttonOldVariables.isDisabled();
        checkBoxEditPropFiles_disabled = checkBoxEditPropFiles.isDisabled();
        buttonOpenProp2_disabled = buttonOpenProp2.isDisabled();
        buttonWriteFxml_disabled = buttonWriteFxml.isDisabled();
        buttonWriteProperties_disabled = buttonWriteProperties.isDisabled();
        buttonWriteProperties2_disabled = buttonWriteProperties2.isDisabled();
    }

    private void writeButtonRowEnabledOrDisabled() {
        buttonSearch.setDisable(buttonSearch_disabled);
        textFieldSearch.setDisable(textFieldSearch_disabled);
        toggleButtonSearch.setDisable(toggleButtntonSearch_disabled);
        buttonOpenFile.setDisable(buttonOpenFile_disabled);
        buttonOpenProp.setDisable(buttonOpenProp_disabled);
        buttonConvert.setDisable(buttonConvert_disabled);
        buttonOldVariables.setDisable(buttonOldVariables_disabled);
        //checkBoxEditPropFiles.setDisable(checkBoxEditPropFiles_disabled);
        buttonOpenProp2.setDisable(buttonOpenProp2_disabled);
        buttonWriteFxml.setDisable(buttonWriteFxml_disabled);
        buttonWriteProperties.setDisable(buttonWriteProperties_disabled);
        buttonWriteProperties2.setDisable(buttonWriteProperties2_disabled);
        underAppProgramChange();
        checkBoxShowInput.setDisable(checkBoxShowInput_disabled);
        noneAppProgramChange();
    }

    @FXML
    protected  void pressedCheckBoxEditPropFiles()
    {
        System.out.println("pressedCheckBoxEditPropFiles");
        if (checkBoxEditPropFiles_under_program_change)
            return;
        if (bCheckBoxShowInput_under_program_change)
            return;

        underAppProgramChange();
        if (checkBoxEditPropFiles.isSelected())
        {
            readButtonRowEnabledOrDisabled();

            buttonOpenFile.setDisable(true);
            buttonConvert.setDisable(true);
            buttonOldVariables.setDisable(true);
            // checkBoxEditPropFiles.setDisable(true);
           // buttonOpenProp2.setDisable(true);
            buttonWriteFxml.setDisable(true);
            buttonOpenProp2.setDisable(false);
            /*
            if (textAreaProperties.getText().trim().length()>0)
                buttonOpenProp.setDisable(true);
            else
             */
            buttonOpenProp.setDisable(false);
            if (textAreaFxml.getText().trim().length() > 0)
                strModifiedFXml = textAreaFxml.getText();
            if (textAreaProperties.getText().trim().length() > 0)
                strPropertyEditPropFiles = textAreaProperties.getText();

            textAreaFxml.setText("");
            textAreaProperties.setText("");
            textAreaFxml.setEditable(true);
            textAreaProperties.setEditable(true);
            buttonWriteProperties.setDisable(true);
            buttonWriteProperties2.setDisable(true);
            checkBoxShowInput.setDisable(true);

            readButtonRowEnabledOrDisabled();
        }
        else
        {
            buttonOpenFile.setDisable(false);
            textAreaProperties.setOnMouseClicked(null);
            textAreaFxml.setOnMouseClicked(null);
            if (strModifiedFXml != null && strModifiedFXml.trim().length()>0)
                textAreaFxml.setText(strModifiedFXml);
            else
                textAreaFxml.setText("");

            if (strPropertyEditPropFiles != null && strPropertyEditPropFiles.trim().length()>0)
                textAreaProperties.setText(strPropertyEditPropFiles);
            else
                textAreaProperties.setText("");
            textAreaFxml.setEditable(false);
            textAreaProperties.setEditable(false);
            readButtonRowEnabledOrDisabled();
            writeButtonRowEnabledOrDisabled();
        }
        noneAppProgramChange();
    }

    private void showReportDialog()
    {
        bReportDialogShowing = true;
        dialogReport = new Dialog<>();
        dialogReport.setTitle("Check report");
        dialogReport.initOwner(this.primaryStage);
        dialogReport.initModality(Modality.NONE);
        // Set the button types.
        ButtonType loginButtonType = new ButtonType("OK", ButtonData.CANCEL_CLOSE);
        dialogReport.getDialogPane().getButtonTypes().addAll(loginButtonType  /*, ButtonType.CANCEL */);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        gridPane.setPadding(new Insets(20, 20, 10, 10));

        TextArea skippedVariables = new TextArea();
        skippedVariables.setEditable(false);
        skippedVariables.setPromptText("Report");

        skippedVariables.setText(strCheckReport);
        gridPane.add(new Label("Report contains:"), 1, 0);
        gridPane.add(skippedVariables, 1, 1);

        dialogReport.getDialogPane().setContent(gridPane);

        // Request focus on the username field by default.
        Platform.runLater(() -> skippedVariables.requestFocus());
        dialogReport.setOnCloseRequest(new EventHandler<DialogEvent> () {
            @Override
            public void handle(DialogEvent t) {
                bReportDialogShowing = false;
            }
        });
        dialogReport.show();
    }

    @FXML
    protected void pressedButtonOldVariables()
    {
        System.out.println("pressedButtonOldVariables()");
        if ((strCheckReport == null || strCheckReport.trim().length() == 0)
                && (this.oldLangVariables == null || this.oldLangVariables.size() == 0 ))
        {
            labelMsg.setText("No skipped old variables and no missing language properties report to show.");
            return;
        }

        if (strCheckReport != null && strCheckReport.trim().length() != 0 )
        {
            if (!bReportDialogShowing) {
                showReportDialog();
                return;
            }
        }

        if (bOldVariablesDialogShowing)
            return;
        /*
        if (oldLangVariables != null && oldLangVariables.size() >0 )
        {
         */
            // Create the custom dialog.
            bOldVariablesDialogShowing = true;
            dialogOldVariables = new Dialog<>();
            dialogOldVariables.initModality(Modality.NONE);
            dialogOldVariables.initOwner(this.primaryStage);
            dialogOldVariables.setTitle("Founded old variables");

            // test data:
            /*
            if (oldLangVariables == null)
                oldLangVariables = new ArrayList<>();
            for(int i = 0; i < 10; i++)
                oldLangVariables.add("Test" +i);
             */

            // Set the button types.
            ButtonType loginButtonType = new ButtonType("OK", ButtonData.OK_DONE);
            dialogOldVariables.getDialogPane().getButtonTypes().addAll(loginButtonType  /*, ButtonType.CANCEL */);

            GridPane gridPane = new GridPane();
            gridPane.setHgap(5);
            gridPane.setVgap(5);
            gridPane.setPadding(new Insets(20, 20, 10, 10));

            TextArea skippedVariables = new TextArea();
            skippedVariables.setEditable(false);
            skippedVariables.setPromptText("Skipped variables");

            StringBuffer sp = new StringBuffer();
            for(String var : this.oldLangVariables)
            {
                sp.append(var +"\n");
            }

            skippedVariables.setText(sp.toString());
            gridPane.add(new Label("Founded fxml text variables, which were all ready in the read file"), 1, 0);
            gridPane.add(skippedVariables, 1, 1);

            dialogOldVariables.getDialogPane().setContent(gridPane);

            // Request focus on the username field by default.
            Platform.runLater(() -> skippedVariables.requestFocus());

            // Convert the result to a username-password-pair when the login button is clicked.
            /*
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == loginButtonType) {
                    return new Pair<>(from.getText(), to.getText());
                }
                return null;
            });

            Optional<Pair<String, String>> result = dialog.showAndWait();

            result.ifPresent(pair -> {
                System.out.println("From=" + pair.getKey() + ", To=" + pair.getValue());
            });
             */

            dialogOldVariables.setOnCloseRequest(new EventHandler<DialogEvent> () {
                    @Override
                    public void handle(DialogEvent t) {
                        bOldVariablesDialogShowing = false;
                    }
                });
            dialogOldVariables.show();

            /*
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    propertiesFile = selectedFile;
                    fPropertiesChooser = selectedFile.getParentFile();
                    JavaFxmlFileConvetI18.writeIntoFile(selectedFile, textAreaProperties.getText().trim());
                    labelMsg.setText("Lang properties has been write into file: " +selectedFile.getAbsolutePath());
                }catch (Exception e){
                    e.printStackTrace();
                    labelMsg.setText("Error in writing: " +e.getMessage());
                }
            }
             */
        /*
        }
        else
        {
         */
            /*
            try {
                propertiesFile = selectedFile;
                fPropertiesChooser = selectedFile.getParentFile();
                JavaFxmlFileConvetI18.writeIntoFile(selectedFile, textAreaProperties.getText());
                labelMsg.setText("Writen properties into file: " +selectedFile.getAbsolutePath());
            }catch (Exception e){
                e.printStackTrace();
                labelMsg.setText("Error in writing: " +e.getMessage());
            }
             */
     //  }

    }

    @FXML
    public void initialize() {
        System.out.println("initialize()");

        if (JavaFxmlFileControllerI18.static_properties_file2 != null)
            propertiesFile2 = JavaFxmlFileControllerI18.static_properties_file2;
        if (JavaFxmlFileControllerI18.static_properties_file != null)
            propertiesFile = JavaFxmlFileControllerI18.static_properties_file;
        if (JavaFxmlFileControllerI18.static_selectedFxmlFile != null)
            selectedFxmlFile = JavaFxmlFileControllerI18.static_selectedFxmlFile;

        toggleButtonSearch.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue,
                                Boolean oldValue, Boolean newValue) {
                enAbleToggleButtonSearch();
            }
        });

        // toggleButtonSearch.setDisable(true);
        toggleButtonSearch.setSelected(true);
        buttonNext.setDisable(true);
        buttonPrevious.setDisable(true);
        textAreaFxml.setEditable(false);

        buttonOpenProp2.setDisable(true);
        buttonOpenProp.setDisable(true);
        buttonOldVariables.setDisable(true);
        buttonConvert.setDisable(true);
        buttonWriteFxml.setDisable(true);
        checkBoxShowInput.setDisable(true);
        buttonWriteProperties.setDisable(true);
        buttonWriteProperties2.setDisable(true);

        fileFxmlChooser.setTitle("Open fxml file to read");
        fileFxmlChooser.setInitialDirectory(new File("."));
        // fileChooser.selectedExtensionFilterProperty()
        FileChooser.ExtensionFilter [] extFilters = getFileExtenstionFilters();
        for(FileChooser.ExtensionFilter ef : extFilters)
            fileFxmlChooser.getExtensionFilters().add(ef);

        filePropertiesChooser.setTitle("Fxml file to read");
        filePropertiesChooser.setInitialDirectory(new File("."));
        // fileChooser.selectedExtensionFilterProperty()
        extFilters = getFileExtenstionsOfProppertiesFilters();
        for(FileChooser.ExtensionFilter ef : extFilters)
            filePropertiesChooser.getExtensionFilters().add(ef);

        // test call: pressedButtonOldVariables();
        readButtonRowEnabledOrDisabled();
        setInitButtons(false);
        readLastTextAreaValues();
    }

    private void readLastTextAreaValues()
    {
        try {
            if (selectedFxmlFile == null)
            {
                boolean bSomeLanPropsLoaded = false;
                if (propertiesFile2 != null) {
                    String strContent = converter.getStringOfFile(propertiesFile2);
                    if (strContent != null) {
                        textAreaFxml.setText(strContent);
                        buttonWriteProperties2.setDisable(false);
                        bSomeLanPropsLoaded = true;
                    }
                }
                if (propertiesFile != null) {
                    String strContent = converter.getStringOfFile(propertiesFile);
                    if (strContent != null) {
                        textAreaProperties.setText(strContent);
                        buttonWriteProperties.setDisable(false);
                        bSomeLanPropsLoaded = true;
                    }
                }
                if (bSomeLanPropsLoaded)
                {
                    checkBoxEditPropFiles.setSelected(true);
                    buttonOpenFile.setDisable(true);
                }
            }
            else
            {
                if (selectedFxmlFile != null) {
                    String strContent = converter.getStringOfFile(selectedFxmlFile);
                    if (strContent != null) {
                        textAreaFxml.setText(strContent);
                        buttonConvert.setDisable(false);
                        fFxmlChooser = selectedFxmlFile.getParentFile();
                    }
                }
                if (propertiesFile != null) {
                    String strContent = converter.getStringOfFile(propertiesFile);
                    if (strContent != null) {
                        textAreaProperties.setText(strContent);
                        buttonConvert.setDisable(true);
                        buttonWriteProperties.setDisable(false);
                        fPropertiesChooser = propertiesFile.getParentFile();
                    }
                }
            }
            readButtonRowEnabledOrDisabled();
        }catch (IOException ioe){
            ioe.printStackTrace();
            labelMsg.setText(ioe.getMessage());
        }
    }

    private void setInitButtons(boolean bValue)
    {
        if (!bValue)
        {
            buttonSearch_disabled_init = buttonSearch.isDisabled();
            textFieldSearch_disabled_init = textFieldSearch.isDisabled();
            togleButtonSearch_disabled_init = toggleButtonSearch.isDisabled();
            checkBoxShowInput_disabled_init = checkBoxShowInput_disabled;
            buttonOpenFile_disabled_init = buttonOpenFile_disabled;
            buttonOpenProp_disabled_init = buttonOpenProp_disabled;
            buttonConvert_disabled_init = buttonConvert_disabled;
            buttonOldVariables_disabled_init = buttonOldVariables_disabled;
            checkBoxEditPropFiles_disabled_init = checkBoxEditPropFiles_disabled;
            buttonOpenProp2_disabled_init = buttonOpenProp2_disabled;
            buttonWriteFxml_disabled_init = buttonWriteFxml_disabled;
            buttonWriteProperties_disabled_init = buttonWriteProperties_disabled;
            buttonWriteProperties2_disabled_init = buttonWriteProperties2_disabled;
        }
        else
        {
            buttonSearch.setDisable(buttonSearch_disabled_init);
            textFieldSearch.setDisable(textFieldSearch_disabled_init);
            toggleButtonSearch.setDisable(togleButtonSearch_disabled_init);
            checkBoxShowInput.setDisable(checkBoxShowInput_disabled_init);
            buttonOpenFile.setDisable(buttonOpenFile_disabled_init);
            buttonOpenProp.setDisable(buttonOpenProp_disabled_init);
            buttonConvert.setDisable(buttonConvert_disabled_init);
            buttonOldVariables.setDisable(buttonOldVariables_disabled_init);
            checkBoxEditPropFiles.setDisable(checkBoxEditPropFiles_disabled_init);
            buttonOpenProp2.setDisable(buttonOpenProp2_disabled_init);
            buttonWriteFxml.setDisable(buttonWriteFxml_disabled_init);
            buttonWriteProperties.setDisable(buttonWriteProperties_disabled_init);
            buttonWriteProperties2.setDisable(buttonWriteProperties2_disabled_init);
        }
    }

    @FXML
    protected void pressedButtonConvert()
    {
        System.out.println("pressedButtonConvert");
        if (textAreaFxml.getText().trim().length()>0) {
            buttonWriteFxml.setDisable(true);
            underAppProgramChange();
            checkBoxShowInput.setDisable(true);
            noneAppProgramChange();
            buttonWriteProperties.setDisable(true);
            buttonWriteProperties2.setDisable(true);

            converter = new JavaFxmlFileConvetI18();
            converter.setInputFileFxml(selectedFxmlFile);
            boolean bWriteIntoOputFiles = false;
            String strStatus = "";
            try {
                labelMsg.setText("");
                oldLangVariables = null;
                strStatus = converter.convert(bWriteIntoOputFiles);
                if (bReportDialogShowing && dialogReport != null)
                    dialogReport.close();
                if (bOldVariablesDialogShowing && dialogOldVariables != null)
                    dialogOldVariables.close();

                strModifiedFXml = null;
                textAreaFxml.setText("");
                textAreaFxml.setDisable(true);
                textAreaProperties.setText("");
                textAreaProperties.setDisable(true);
                buttonOldVariables.setDisable(true);
              //  buttonOpenProp.setDisable(false);
                bConvertedData = converter.getConverter().isbConvertedData();
                if (strStatus != null && strStatus.isEmpty())
                {
                    String strProperties = converter.getConverter().getStrOutputFileProperties();
                    if (strProperties != null) {
                        textAreaProperties.setText(strProperties +cnstCRLines);
                        addFxmlForPropertyTextAreaListener();
                        textAreaProperties.setDisable(false);
                        if (strProperties.trim().length()== 0) {
                            if (bExistingOldVariables)
                                labelMsg.setText("No converted data found, because fxml file contains only old variables!");
                            else
                                labelMsg.setText("No properties data found!");
                        }
                        else
                            labelMsg.setText("Converted");
                        oldLangVariables = converter.getConverter().getOldLangVariables();
                        buttonOldVariables.setDisable(oldLangVariables == null || oldLangVariables.size()==0);
                    }
                    String strConvertFxml = converter.getConverter().getStrOutputFileFxml();
                    if (strConvertFxml != null) {
                        textAreaFxml.setText(strConvertFxml); // .replaceAll("\n+","\n"))
                        textAreaFxml.setDisable(false);
                        strModifiedFXml = strConvertFxml;
                        addFxmlForFxmlTextAreaListener();
                    }
                    boolean bSetDiaAbled = false;
                    underAppProgramChange();
                    checkBoxShowInput.setDisable(bSetDiaAbled);
                    noneAppProgramChange();
                    buttonWriteFxml.setDisable(bSetDiaAbled);
                    buttonWriteProperties.setDisable(bSetDiaAbled);
                   // buttonWriteProperties2.setDisable(bSetDiaAbled);
                    if (bExistingOldVariables)
                        buttonOpenProp.setDisable(false);
                }
                buttonConvert.setDisable(true);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @FXML
    protected void pressedButtonWriteProperties2()
    {
        System.out.println("pressedButtonWriteProperties2");
        if (this.filePropertiesChooser != null)
        {
            if (fPropertiesChooser != null)
                filePropertiesChooser.setInitialDirectory(fPropertiesChooser);
            else
                filePropertiesChooser.setInitialDirectory(fFxmlChooser);
            if (propertiesFile2 != null) {
                filePropertiesChooser.setInitialDirectory(propertiesFile2.getParentFile());
                filePropertiesChooser.setInitialFileName(propertiesFile2.getName());
            }
        }
        filePropertiesChooser.setTitle("Properties 2 file to write");
        File selectedFile = filePropertiesChooser.showSaveDialog(this.primaryStage);
        if (selectedFile != null && selectedFile.exists()) {
            if (selectedFile.exists() && selectedFile.isFile() && selectedFile.length() != 0)
            {
                String strTitle = "Warning about existing file";
                String strHeader = "File is about to over write!";

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle(strTitle);
                alert.setHeaderText(strHeader);
                String strContent ="File: " +selectedFile.getAbsolutePath() +" existing all ready. Should it wll be over write?";
                alert.setContentText(strContent);

                String strOK = "Save into 2 .properties file";
                ((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setText(strOK);
                // ((Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("Not Cancel Anymore");

                String strCancel = "Cancel";
                Optional<ButtonType> result = showAndWaitDialog(strTitle, strHeader, strContent,
                        strOK, strCancel);
                // ObservableList<ButtonType> buttons = alert.getDialogPane().getButtonTypes();
                // Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get().getButtonData() == ButtonData.OK_DONE) {
                    try {
                        String strPropData = textAreaFxml.getText().trim();
                        if (this.oldLangVariables != null && this.oldLangVariables.size() > 0)
                            strPropData = getCombinedAndCheckDataAndFromFile(strPropData, selectedFile);
                        propertiesFile2 = selectedFile;
                        fPropertiesChooser = selectedFile.getParentFile();
                        JavaFxmlFileConvetI18.writeIntoFile(selectedFile, strPropData +"\n");
                        labelMsg.setText("Lang properties was write into file: " +selectedFile.getAbsolutePath());
                        bTextAreaFxmlChanged = false;
                        buttonWriteProperties2.setDisable(true);
                    }catch (Exception e){
                        e.printStackTrace();
                        labelMsg.setText("Error in writing: " +e.getMessage());
                    }
                }
            }
            else
            {
                try {
                    propertiesFile2 = selectedFile;
                    fPropertiesChooser = selectedFile.getParentFile();
                    JavaFxmlFileConvetI18.writeIntoFile(selectedFile, textAreaProperties.getText());
                    labelMsg.setText("Lang properties was write into file: " +selectedFile.getAbsolutePath());
                    bTextAreaFxmlChanged = false;
                    buttonWriteProperties2.setDisable(true);
                }catch (Exception e){
                    e.printStackTrace();
                    labelMsg.setText("Error in writing: " +e.getMessage());
                }
            }
        }
    }

    @FXML
    protected void pressedButtonWriteProperties() {
        System.out.println("pressedButtonWriteProperties");
        if (this.filePropertiesChooser != null)
        {
            if (fPropertiesChooser != null)
                filePropertiesChooser.setInitialDirectory(fPropertiesChooser);
            else
                filePropertiesChooser.setInitialDirectory(fFxmlChooser);
            if (propertiesFile != null)
            {
                filePropertiesChooser.setInitialDirectory(propertiesFile.getParentFile());
                filePropertiesChooser.setInitialFileName(propertiesFile.getName());
            }
        }
        filePropertiesChooser.setTitle("Fxml file to write");
        File selectedFile = filePropertiesChooser.showSaveDialog(this.primaryStage);
        if (selectedFile != null && selectedFile.exists()) {
            if (selectedFile.exists() && selectedFile.isFile() && selectedFile.length() != 0)
            {
                String strTitle = "Warning about existing file";
                String strHeader = "File is about to over write!";

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle(strTitle);
                alert.setHeaderText(strHeader);
                String strContent ="File: " +selectedFile.getAbsolutePath() +" existing all ready. Should it wll be over write?";
                alert.setContentText(strContent);

                String strOK = "Save into .properties file";
                ((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setText(strOK);
                // ((Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("Not Cancel Anymore");

                String strCancel = "Cancel";
                Optional<ButtonType> result = showAndWaitDialog(strTitle, strHeader, strContent,
                        strOK, strCancel);
                // ObservableList<ButtonType> buttons = alert.getDialogPane().getButtonTypes();
                // Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get().getButtonData() == ButtonData.OK_DONE) {
                    try {
                        String strPropData = textAreaProperties.getText().trim();
                        if (this.oldLangVariables.size() > 0)
                            strPropData = getCombinedAndCheckDataAndFromFile(strPropData, selectedFile);
                        propertiesFile = selectedFile;
                        fPropertiesChooser = selectedFile.getParentFile();
                        JavaFxmlFileConvetI18.writeIntoFile(selectedFile, strPropData +"\n");
                        labelMsg.setText("Lang properties was write into file: " +selectedFile.getAbsolutePath());
                        bTextAreaPropertiesChanged = false;
                        bPropertyFileWrote = true;
                        checkCanEditLangFilesControl();
                        buttonWriteProperties.setDisable(true);
                    }catch (Exception e){
                        e.printStackTrace();
                        labelMsg.setText("Error in writing: " +e.getMessage());
                    }
                }
            }
            else
            {
                try {
                    propertiesFile = selectedFile;
                    fPropertiesChooser = selectedFile.getParentFile();
                    JavaFxmlFileConvetI18.writeIntoFile(selectedFile, textAreaProperties.getText());
                    labelMsg.setText("Lang properties was write into file: " +selectedFile.getAbsolutePath());
                    bTextAreaPropertiesChanged = false;
                    buttonWriteProperties.setDisable(true);
                }catch (Exception e){
                    e.printStackTrace();
                    labelMsg.setText("Error in writing: " +e.getMessage());
                }
            }
        }
    }

    private String getCombinedAndCheckDataAndFromFile(String strPropData, File propertiesFile)
    {
        String ret = "";
    //    String
        return strPropData;
    }

    private void checkCanEditLangFilesControl()
    {
        if (bPropertyFileWrote && bFxmlFileWrote)
        {
            underAppProgramChange();
            checkBoxEditPropFiles.setDisable(false);
            noneAppProgramChange();
        }
    }

    @FXML
    protected void pressedButtonWriteFxml()
    {
        System.out.println("pressedButtonWriteFxml");
        if (fFxmlChooser != null)
            fileFxmlChooser.setInitialDirectory(fFxmlChooser);
        if (selectedFxmlFile != null)
        {
            fileFxmlChooser.setInitialDirectory(selectedFxmlFile.getParentFile());
           // fileFxmlChooser.setInitialFileName(selectedFxmlFile.getName());
        }

        File selectedFile = fileFxmlChooser.showSaveDialog(this.primaryStage);
        if (selectedFile != null && selectedFile.exists()) {
            if (selectedFile.getAbsolutePath().equals(selectedFxmlFile.getAbsolutePath()))
            {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning about existing file");
                alert.setHeaderText("The input file and output file are the same!");
                String s ="File: " +selectedFile.getAbsolutePath() +". The save is stopped now!";
                alert.setContentText(s);
                // ObservableList<ButtonType> buttons = alert.getDialogPane().getButtonTypes();
                alert.show();
                return;
            }
            if (selectedFile.exists() && selectedFile.isFile() && selectedFile.length() != 0)
            {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Warning about existing file");
                alert.setHeaderText("About JavaFx Player v 1.0");
                String s ="File: " +selectedFile.getAbsolutePath() +" existing all ready. Should it wll be over write?";
                alert.setContentText(s);
                ((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setText("Save into fxml");

                // ObservableList<ButtonType> buttons = alert.getDialogPane().getButtonTypes();
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get().getButtonData() == ButtonData.OK_DONE) {
                    try {
                        JavaFxmlFileConvetI18.writeIntoFile(selectedFile, textAreaFxml.getText().trim());
                        labelMsg.setText("Fxml variables was write into file: " +selectedFile.getAbsolutePath());
                        selectedFxmlFile = selectedFile;
                        bFxmlFileWrote = true;
                        checkCanEditLangFilesControl();
                    }catch (Exception e){
                        e.printStackTrace();
                        labelMsg.setText("Error in writing: " +e.getMessage());
                    }
                }
            }
            else
            {
                try {
                    JavaFxmlFileConvetI18.writeIntoFile(selectedFile, textAreaFxml.getText());
                    labelMsg.setText("Fxml variables was write into file: " +selectedFile.getAbsolutePath());
                    selectedFxmlFile = selectedFile;
                    bFxmlFileWrote = true;
                    checkCanEditLangFilesControl();
                }catch (Exception e){
                    e.printStackTrace();
                    labelMsg.setText("Error in writing: " +e.getMessage());
                }
            }
        }
    }

    @FXML
    protected void pressedCheckBoxShowInput()
    {
        if (checkBoxEditPropFiles_under_program_change)
            return;
        if (bCheckBoxShowInput_under_program_change)
            return;

        underAppProgramChange();
        System.out.println("pressedCheckBoxShowInput");
        if (checkBoxShowInput.isSelected())
        {
            readButtonRowEnabledOrDisabled();
            buttonConvert.setDisable(true);
            textAreaFxml.setText(strFXml);
            buttonWriteFxml.setDisable(true);
            buttonWriteProperties.setDisable(true);
            buttonWriteProperties2.setDisable(true);
            checkBoxEditPropFiles.setDisable(true);
        }
        else
        {
            writeButtonRowEnabledOrDisabled();
            checkBoxEditPropFiles.setDisable(false);
            // buttonConvert.setDisable(false);
            // buttonWriteFxml.setDisable(false);
            // buttonWriteProperties.setDisable(false);
            if (strModifiedFXml != null)
                textAreaFxml.setText(strModifiedFXml);
        }
        noneAppProgramChange();
    }

    @FXML
    protected void pressedButtonOpenFile()
    {
        System.out.println("pressedButtonOpenFile");
        if (this.fFxmlChooser != null)
            fileFxmlChooser.setInitialDirectory(fFxmlChooser);
        File selectedFile = fileFxmlChooser.showOpenDialog(this.primaryStage);
        if (selectedFile != null && selectedFile.exists()) {
            buttonConvert.setDisable(true);
            fFxmlChooser = selectedFile.getParentFile();
            selectedFxmlFile = selectedFile;
            Tooltip t = new Tooltip(selectedFile.getAbsolutePath());
            t.setStyle("-fx-font-weight: bold");
            t.setFont(Font.font(12));
            Tooltip.install(buttonOpenFile, t);
            converter = new JavaFxmlFileConvetI18();
            try {
                String strInputData = converter.getStringOfFile(selectedFxmlFile);
                if (strInputData == null)
                    strInputData = "";
                textAreaFxml.setText(strInputData);
                bFxmlFileWrote = false;
                bPropertyFileWrote = false;
                textAreaProperties.setText("");
                if (bReportDialogShowing && dialogReport != null)
                    dialogReport.close();
                if (bOldVariablesDialogShowing && dialogOldVariables != null)
                    dialogOldVariables.close();
                strFXml = strInputData;
                bExistingOldVariables = checkFxmlTextVariables(strInputData);
                if (bExistingOldVariables)
                {
                    buttonOpenProp.setDisable(true);
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Warning about existing text variables");
                    alert.setHeaderText("There is earlier text variables inside of by example: text=\"%Menu_text\"\nOpen correspond properties file for checking later under converting!");
                    String s ="File: " +selectedFxmlFile.getAbsolutePath() +" existing all ready. Should it wll be over write?";
                    alert.setContentText(s);
                    alert.show();
                    buttonConvert.setDisable(false);
                }
                else
                    buttonConvert.setDisable(false);
                buttonWriteProperties.setDisable(true);
                buttonWriteProperties2.setDisable(true);
                buttonWriteFxml.setDisable(true);
                buttonSearch.setDisable(false);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private boolean checkFxmlTextVariables(String strInputData)
    {
        boolean ret = false;
        if (strInputData != null && strInputData.trim().length()>0) {
            String patternString = "(?s)(\"|')%.*?_.*?(\"|')";
            // String patternString = "<(\\w+)\\s+(.*?)(/*>)";
            Pattern pattern = Pattern.compile(patternString);
            Matcher matcher = pattern.matcher(strInputData);
            if (matcher.find()) {
                ret = true;
            }
        }
        return ret;
    }
//    @FXML
//    protected void onHelloButtonClick() {
//        welcomeText.setText("Welcome to JavaFX Application!");
//    }

    private FileChooser.ExtensionFilter [] getFileExtenstionsOfProppertiesFilters()
    {
        String strExts =  String.join(",", arrFileExtenstionsProperties);
        strExts = strExts.replaceAll("\\."," *.");
        strExts = "*.mp3";
        FileChooser.ExtensionFilter  [] ret = new FileChooser.ExtensionFilter[arrFileExtenstionsProperties.length];
        FileChooser.ExtensionFilter cur = null;
        int i = 0;
        for(String strExt : arrFileExtenstionsProperties)
        {
            cur = new FileChooser.ExtensionFilter("Properties files (*" +strExt +")", "*" +strExt);;
            ret[i++] = cur;
        }

        return ret;
    }

    private FileChooser.ExtensionFilter [] getFileExtenstionFilters()
    {
        String strExts =  String.join(",", arrFileExtenstions);
        strExts = strExts.replaceAll("\\."," *.");
        strExts = "*.mp3";
        FileChooser.ExtensionFilter  [] ret = new FileChooser.ExtensionFilter[arrFileExtenstions.length];
        FileChooser.ExtensionFilter cur = null;
        int i = 0;
        for(String strExt : arrFileExtenstions)
        {
            cur = new FileChooser.ExtensionFilter("Audio files (*" +strExt +")", "*" +strExt);;
            ret[i++] = cur;
        }

        return ret;
    }

    @FXML
    protected void pressedButtonOpenProp2()
    {
        System.out.println("pressedButtonOpenProp2");
        if (this.filePropertiesChooser != null)
        {
            if (fPropertiesChooser != null)
                filePropertiesChooser.setInitialDirectory(fPropertiesChooser);
            else
            if (fFxmlChooser != null)
                filePropertiesChooser.setInitialDirectory(fFxmlChooser);
            else
                filePropertiesChooser.setInitialDirectory(new File("."));
        }
        filePropertiesChooser.setTitle("Lang properties file 2 to read");
        File selectedFile = filePropertiesChooser.showOpenDialog(this.primaryStage);
        if (selectedFile != null && selectedFile.exists()) {
            if (propertiesFile != null && propertiesFile.getAbsolutePath().equals(selectedFile.getAbsolutePath()))
            {
                String strTitle = "Same files";
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle(strTitle);
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                String strHeader = "You had try to open same file twice!";
                alert.setHeaderText(strHeader);
                String strContent ="File: " +selectedFile.getAbsolutePath() +" Not to open this file?";
                alert.setContentText(strContent);

                String strOK = "Cancel";
               // ((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setText(strOK);
                alert.show();
                return;
            }
            try {
                String strPropData = converter.getStringOfFile(selectedFile);
                textAreaFxml.setText(strPropData);
                propertiesFile2 = selectedFile;
                if (checkBoxEditPropFiles.isSelected())
                {
                    bTextAreaFxmlChanged = false;
                    buttonWriteProperties2.setDisable(true);
                    strPropertyFxml = null;
                    textAreaFxml.textProperty().addListener(new ChangeListener<String>() {
                        @Override
                        public void changed(ObservableValue<? extends String> observableValue, String oldStr, String newStr) {
                            bTextAreaFxmlChanged = true;
                            if (buttonWriteProperties2.isDisable())
                                buttonWriteProperties2.setDisable(false);
                            if (!buttonOpenProp2.isDisable())
                                buttonOpenProp2.setDisable(true);
                            if (!checkBoxEditPropFiles.isDisable()) {
                                underAppProgramChange();
                                checkBoxEditPropFiles.setDisable(true);
                                noneAppProgramChange();
                            }
                            strPropertyFxml = newStr;
                        }
                    });
                    textAreaFxml.setOnMouseClicked(evt -> {
                    if (evt.getButton() == MouseButton.PRIMARY) {
                        // check, if click was inside the content area
                        Node n = evt.getPickResult().getIntersectedNode();
                        while (n != textAreaFxml) {
                            if (n.getStyleClass().contains("content")) {
                                // find previous/next line break
                                int caretPosition = textAreaFxml.getCaretPosition();
                                String text = textAreaFxml.getText();
                                int lineBreak1 = text.lastIndexOf('\n', caretPosition - 1);
                                if (lineBreak1 == -1)
                                    lineBreak1 = 0;
                                int lineBreak2 = text.indexOf('\n', caretPosition);
                                if (lineBreak2 < 0) {
                                    // if no more line breaks are found, select to end of text
                                    lineBreak2 = text.length();
                                }

                                textAreaFxml.selectRange(lineBreak1, lineBreak2);
                                String selectedText = textAreaFxml.getSelectedText();
                                if (selectedText != null && textAreaProperties != null)
                                {
                                    String textProp = textAreaProperties.getText();
                                    if (textProp != null)
                                    {
                                        int indVar = selectedText.indexOf("=");
                                        if (indVar > -1) {
                                            String strVariable = selectedText.substring(0, indVar);
                                            strVariable = strVariable.replaceAll("\n","");
                                            int ind = textProp.indexOf(strVariable);
                                            if (ind > -1) {
                                                textAreaProperties.selectRange(ind, ind + strVariable.length());
                                            }
                                            else {
                                                deselect(textAreaProperties);
                                                /*
                                                double scrollPosition = textAreaProperties.getScrollTop();
                                                textAreaProperties.setScrollTop(Double.MAX_VALUE);
                                                 */
                                            }
                                        }
                                        else
                                        {
                                            deselect(textAreaProperties);
                                        }
                                    }
                                }
                                evt.consume();
                                break;
                            }
                            n = n.getParent();
                        }
                    }
                });
                }
                buttonSearch.setDisable(false);
            }catch (Exception e){
                e.printStackTrace();
                labelMsg.setText(e.getMessage());
            }
        }

    }

    private void deselect(TextArea textField) {
        Platform.runLater(() -> {
            if (textField.getText().length() > 0 &&
                    textField.selectionProperty().get().getEnd() == 0) {
                deselect(textField);
            }else{
                textField.selectEnd();
                textField.deselect();
            }
        });
    }

    private Optional<ButtonType> showAndWaitDialog(String title, String headerText, String contentText,
                                                   String strOKText, String strCancelText)
    {
        Dialog dialog = new Dialog<>();
        dialog.setTitle(title);

        // Set the button types.
        ButtonType okButtonType = new ButtonType(strOKText, ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType(strCancelText, ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType  /*, ButtonType.CANCEL */);
        dialog.getDialogPane().getButtonTypes().addAll(cancelButtonType  /*, ButtonType.CANCEL */);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        gridPane.setPadding(new Insets(20, 20, 10, 10));

        Label firstText = new Label();
        firstText.setText(headerText);
        if (headerText != null)
            gridPane.add(firstText, 1, 0);

        Label secondText = new Label();
        secondText.setText(contentText);
        if (contentText != null)
            gridPane.add(secondText, 1, 1);

        dialog.getDialogPane().setContent(gridPane);
        // Request focus on the username field by default.
        Platform.runLater(() -> firstText.requestFocus());
        Optional<ButtonType> response = dialog.showAndWait();
        return response;
    }

    @FXML
    protected void pressedButtonClear()
    {
        String strTitle = "Warining about clear";
        String strHeader = "Clearing all data and state!";
        String strContent = "Are you sure to clear all data etc?";
        String strOK = "OK", strCancel = "No";
        Optional<ButtonType> result = showAndWaitDialog(strTitle, strHeader, strContent,
                strOK, strCancel);
        if (result.isPresent() && result.get().getButtonData() == ButtonData.OK_DONE) {
            System.out.println("pressedButtonClear");
            setInitButtons(true);
            textAreaProperties.setText("");
            textAreaProperties.setEditable(false);
            textAreaFxml.setEditable(false);
            textAreaFxml.setText("");
            checkBoxEditPropFiles.setDisable(false);
            checkBoxEditPropFiles.setSelected(false);
            checkBoxShowInput.setSelected(false);
            setInitData();
        }
    }

    private void setInitData()
    {
        bConvertingFiles = false;
        selectedFxmlFile = null;
        propertiesFile = null;
        propertiesFile2 = null;

        bConvertedData = false;
        strModifiedFXml = null;
        strFXml = null;
        oldLangVariables = null;
        bExistingOldVariables = false;

        /*
        buttonOpenFile_disabled = false;
        buttonOpenProp_disabled = false;
        buttonConvert_disabled = false;
        buttonOldVariables_disabled = false;
        checkBoxEditPropFiles_disabled = false;
        buttonOpenProp2_disabled = false;
        buttonWriteFxml_disabled = false;
        buttonWriteProperties_disabled = false;
        buttonWriteProperties2_disabled = false;
        checkBoxShowInput_disabled = false;
        */

        strCheckReport = null;
        bReportDialogShowing = false;
        bOldVariablesDialogShowing = false;
        dialogReport = null;
        dialogOldVariables = null;
        bTextAreaFxmlChanged = false;
        bTextAreaPropertiesChanged = false;
        strPropertyFxml = null;
        strProperty = null;
        strPropertyEditPropFiles = null;
        bFxmlFileWrote = false;
        bPropertyFileWrote = false;

        checkBoxEditPropFiles_under_program_change = false;
        bCheckBoxShowInput_under_program_change = false;

    }

    private void underAppProgramChange()
    {
        checkBoxEditPropFiles_under_program_change = true;
        bCheckBoxShowInput_under_program_change = true;
    }

    private void noneAppProgramChange()
    {
        checkBoxEditPropFiles_under_program_change = false;
        bCheckBoxShowInput_under_program_change = false;
    }

    @FXML
    protected void pressedToggleButtonSearch()
    {
      //  System.out.println("pressedToggleButtonSearch");
        enAbleToggleButtonSearch();
    }

    private void enAbleToggleButtonSearch()
    {
        buttonSearch.setDisable(true);
        buttonNext.setDisable(true);
        buttonPrevious.setDisable(true);
        if (toggleButtonSearch.isSelected())
        {
            toggleButtonSearch.setText("Search from upper textarea");
            if (textAreaFxml.getText().trim().length() > 0)
            {
                toggleButtonSearch.setDisable(false);
                buttonSearch.setDisable(false);
                textFieldSearch.setDisable(false);
            }
        }
        else
        {
            if (textAreaProperties.getText().trim().length() > 0)
            {
                toggleButtonSearch.setDisable(false);
                buttonSearch.setDisable(false);
                textFieldSearch.setDisable(false);
            }
            toggleButtonSearch.setText("Search from lang properties");
        }
    }

    @FXML
    protected void pressedButtonSearch() {
      //  System.out.println("pressedButtonSearch");
        if (textFieldSearch.getText().trim().length() == 0) {
            labelMsg.setText("None search text!");
            return;
        }
        if (toggleButtonSearch.isSelected()) {
            if (textAreaFxml.getText().trim().length() == 0) {
                labelMsg.setText("No fxml or property 1 text to a search!");
                return;
            }
            iIndSearchPostion = -1;
            buttonNext.setDisable(true);
            buttonPrevious.setDisable(true);
            arrfounded = searchFoundedRangesIn(textAreaFxml, textFieldSearch.getText(), checkBoxInCase.isSelected());
            if (arrfounded == null || arrfounded.length < 1 || iIndSearchPostion >= arrfounded.length) {
                deselect(textAreaFxml);
                textAreaFxml.positionCaret(0);
                return;
            }
            SearchPosition nextPos = arrfounded[iIndSearchPostion];
            textAreaFxml.positionCaret(0);
            textAreaFxml.selectRange(nextPos.iStart, nextPos.iEnd);
            searcTextArea = textAreaFxml;
            buttonNext.setDisable(false);
            buttonPrevious.setDisable(false);
        } else {
            if (textAreaProperties.getText().trim().length() == 0) {
                labelMsg.setText("No property 2 text to a search!");
                return;
            }
            iIndSearchPostion = -1;
            buttonNext.setDisable(true);
            buttonPrevious.setDisable(true);
            arrfounded = searchFoundedRangesIn(textAreaProperties, textFieldSearch.getText(), checkBoxInCase.isSelected());
            if (arrfounded == null || arrfounded.length < 1 || iIndSearchPostion >= arrfounded.length) {
                deselect(textAreaProperties);
                textAreaProperties.positionCaret(0);
                return;
            }
            SearchPosition nextPos = arrfounded[iIndSearchPostion];
            textAreaProperties.positionCaret(0);
            textAreaProperties.selectRange(nextPos.iStart, nextPos.iEnd);
            searcTextArea = textAreaProperties;
            buttonNext.setDisable(false);
            buttonPrevious.setDisable(false);
        }
    } // end of method

    private SearchPosition [] searchText(final String strSearch, final String strText, final boolean bInCaseSearch)
    {
        SearchPosition [] ret = null;
        if (strSearch != null && strSearch.trim().length() != 0
            && strSearch != null && strSearch.trim().length() != 0)
        {
            String strText2 = (bInCaseSearch ? strText.toLowerCase() : strText);
            String strSearch2 = (bInCaseSearch ? strSearch.toLowerCase() : strSearch);
            int ind = strText2.indexOf(strSearch2);
            List<SearchPosition> founded = new ArrayList<>();
            SearchPosition foundPos = null;
            while (ind > -1)
            {
                foundPos = new SearchPosition();
                foundPos.iStart = ind;
                foundPos.iEnd = ind +strSearch2.length();
                founded.add(foundPos);
                ind = strText2.indexOf(strSearch2, ind+1);
            }
            ret = new SearchPosition[founded.size()];
            founded.toArray(ret);
        }
        return ret;
    }

    /*
    private void markFoundedRangesIn(final TextArea textArea, final SearchPosition [] arrfounded, int iIndSearchPostion)
    {
        if (textArea == null || arrfounded == null || arrfounded.length == 0)
            return;
        if (iIndSearchPostion < 0 || iIndSearchPostion >= arrfounded.length)
            return;

        final SearchPosition nextPos = arrfounded[iIndSearchPostion];
        if (nextPos == null)
            return;
        Platform.runLater(() -> {
            textArea.selectRange(*nextPos.iStart, nextPos.iEnd);
        });
        // textArea.setStyle(sp.iStart, sp.iEnd, "-fx-font-weight: bold;");
    }
    */

    private SearchPosition [] searchFoundedRangesIn(final TextArea textArea,
                                                    final String strSearch,
                                                    final boolean bInCaseSearch)
    {
        String strText = textArea.getText();
        SearchPosition [] ret = searchText(strSearch, strText, bInCaseSearch);
        if (ret == null) {
            labelMsg.setText("None founded in this search!");
            return null;
        }
        /* this next call deselect will cause that setSelectRangeg will not work!
        deselect(textArea);
        try {
            Thread.sleep(500);
        } catch (Exception e){;
        }
         */
        iIndSearchPostion = 0;
        // markFoundedRangesIn(textArea, ret, iIndSearchPostion);
        return ret;
    }

    @FXML
    protected void pressedButtonPrevious()
    {
    //    System.out.println("pressedButtonPrevisous");
        if (searcTextArea == null)
            return;
        if (arrfounded == null || arrfounded.length == 0)
            return;
        if (iIndSearchPostion < 1 || iIndSearchPostion >= (arrfounded.length))
            return;
        iIndSearchPostion--;
        SearchPosition prevPos = arrfounded[iIndSearchPostion];
        searcTextArea.selectRange(prevPos.iStart, prevPos.iEnd);
    }

    @FXML
    protected void pressedButtonNext()
    {
      //  System.out.println("pressedButtonNext");
        if (searcTextArea == null)
            return;
        if (arrfounded == null || arrfounded.length == 0)
            return;
        if (iIndSearchPostion < 0 || iIndSearchPostion >= (arrfounded.length-1))
            return;
        iIndSearchPostion++;
        SearchPosition nextPos = arrfounded[iIndSearchPostion];
        searcTextArea.selectRange(nextPos.iStart, nextPos.iEnd);
    }

    private void addFxmlForPropertyTextAreaListener()
    {
        textAreaProperties.setOnMouseClicked(evt -> {
            if (evt.getButton() == MouseButton.PRIMARY) {
                // check, if click was inside the content area
                Node n = evt.getPickResult().getIntersectedNode();
                while (n != textAreaProperties) {
                    if (n.getStyleClass().contains("content")) {
                        // find previous/next line break
                        int caretPosition = textAreaProperties.getCaretPosition();
                        String text = textAreaProperties.getText();
                        int lineBreak1 = text.lastIndexOf('\n', caretPosition - 1);
                        if (lineBreak1 == -1)
                            lineBreak1 = 0;
                        int lineBreak2 = text.indexOf('\n', caretPosition);
                        if (lineBreak2 < 0) {
                            // if no more line breaks are found, select to end of text
                            lineBreak2 = text.length();
                        }

                        textAreaProperties.selectRange(lineBreak1, lineBreak2);
                        String selectedText = textAreaProperties.getSelectedText();
                        if (selectedText != null && textAreaFxml != null)
                        {
                            String textFxml = textAreaFxml.getText();
                            if (textFxml != null)
                            {
                                int indVar = selectedText.indexOf("=");
                                if (indVar > -1) {
                                    String strVariable = selectedText.substring(0, indVar);
                                    strVariable = strVariable.replaceAll("\n","");
                                    int ind = textFxml.indexOf("%" +strVariable);
                                    if (ind > -1) {
                                        textAreaFxml.selectRange(ind, ind +1 + strVariable.length());
                                    }
                                    else {
                                        deselect(textAreaFxml);
                                                /*
                                                double scrollPosition = textAreaFxml.getScrollTop();
                                                textAreaFxml.setScrollTop(Double.MAX_VALUE);
                                                 */
                                    }
                                }
                                else
                                {
                                    deselect(textAreaFxml);
                                }
                            }
                        }
                        evt.consume();
                        break;
                    }
                    n = n.getParent();
                }
            }
        });
    }

    private void addFxmlForFxmlTextAreaListener()
    {
        textAreaFxml.setOnMouseClicked(evt -> {
            if (evt.getButton() == MouseButton.PRIMARY) {
                // check, if click was inside the content area
                Node n = evt.getPickResult().getIntersectedNode();
                while (n != textAreaFxml) {
                    if (n.getStyleClass().contains("content")) {
                        // find previous/next line break
                        int caretPosition = textAreaFxml.getCaretPosition();
                        String text = textAreaFxml.getText();
                        int lineBreak1 = text.lastIndexOf('\n', caretPosition - 1);
                        if (lineBreak1 == -1)
                            lineBreak1 = 0;
                        int lineBreak2 = text.indexOf('\n', caretPosition);
                        if (lineBreak2 < 0) {
                            // if no more line breaks are found, select to end of text
                            lineBreak2 = text.length();
                        }

                        textAreaFxml.selectRange(lineBreak1, lineBreak2);
                        String selectedText = textAreaFxml.getSelectedText();
                        if (selectedText != null && textAreaProperties != null)
                        {
                            String textProp = textAreaProperties.getText();
                            if (textProp != null)
                            {
                                int indVar = selectedText.indexOf("%");
                                if (indVar > -1) {
                                    String strVariable = selectedText.substring(indVar);
                                    int indVar2 = strVariable.indexOf('"');
                                    if (indVar2 > -1)
                                        strVariable = strVariable.substring(1, indVar2);
                                    strVariable = strVariable.replaceAll("\n","");
                                    int ind = textProp.indexOf(strVariable);
                                    if (ind > -1) {
                                        textAreaProperties.selectRange(ind, ind + strVariable.length());
                                    }
                                    else {
                                        deselect(textAreaProperties);
                                                /*
                                                double scrollPosition = textAreaProperties.getScrollTop();
                                                textAreaProperties.setScrollTop(Double.MAX_VALUE);
                                                 */
                                    }
                                }
                                else
                                {
                                    deselect(textAreaProperties);
                                }
                            }
                        }
                        evt.consume();
                        break;
                    }
                    n = n.getParent();
                }
            }
        });
    }

    @FXML
    private void pressedHelp(ActionEvent event) {
   //     System.out.println("pressedHelp");
        try {
            webViewHelp = new WebView();
            if (converter == null)
                converter = new JavaFxmlFileConvetI18();
            String href = "Help.html";
            // File helpFile =   );
            // String strHelp = converter.getStringOfFile(selectedFile);
            String strHelp = getClass().getResource(href).toString();
            webViewHelp.getEngine().load(strHelp);

            Dialog dialog = new Dialog<>();
            String title = "Help";
            dialog.setTitle(title);

            // Set the button types.
            ButtonType okButtonType = new ButtonType("Ok", ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(okButtonType  /*, ButtonType.CANCEL */);
            // dialog.getDialogPane().setExpandableContent(webViewHelp  /*, ButtonType.CANCEL */);
            // dialog.getDialogPane().ad

            GridPane gridPane = new GridPane();
            gridPane.setHgap(5);
            gridPane.setVgap(5);
            gridPane.setPadding(new Insets(20, 20, 10, 10));

            Label firstText = new Label();
            firstText.setText("Content");
            // gridPane.add(firstText, 1, 0);
            gridPane.add(webViewHelp, 1, 1);

            dialog.getDialogPane().setContent(gridPane);
            // Request focus on the username field by default.
            Platform.runLater(() -> firstText.requestFocus());
            Optional<ButtonType> response = dialog.showAndWait();

            /*
            FXMLLoader fxmlLoader = new FXMLLoader(HelpController.class.getResource("javafxplayerhelp.fxml"));
            helpController = new HelpController();
            fxmlLoader.setController(helpController);
            Parent parent = fxmlLoader.load();

            labelLevel.setFocusTraversable(true);

            Scene scene = new Scene(parent, 700, 400);
            stageHelp = new Stage();
            // stageHelp.setIconified(true);
            stageHelp.initModality(Modality.WINDOW_MODAL);
            stageHelp.setScene(scene);
            */
        }catch (Exception ioe){
            ioe.printStackTrace();
        }
    }

    @FXML
    private void pressedAbout(ActionEvent event)
    {
      //  System.out.println("pressedAbout");
        String strTitle = "Help";
        String strHeader = "How to use this application?";
        String strContent = "Case 1: open, convert and save normal Fxml file into JavaFx % variables\n"
          +"and generated language properties file.\n"
                +"Case 2: edit and save 1 or 2 languae properties files together.";
        String strOK = "OK", strCancel = null;
        Optional<ButtonType> result = showAndWaitDialog(strTitle, strHeader, strContent,
                strOK, strCancel);
        // if (result.isPresent() && result.get().getButtonData() == ButtonData.OK_DONE) {

        }

    @FXML
    protected void pressedChangeLanguage()
    {
        System.out.println("pressedChangeLanguage");

        Dialog dialog = new Dialog<>();
        String title = "Change language";
        dialog.setTitle(title);

        // Set the button types.
        ButtonType okButtonType = new ButtonType("Ok", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);
        // dialog.getDialogPane().setExpandableContent(webViewHelp  /*, ButtonType.CANCEL */);
        // dialog.getDialogPane().ad

        GridPane gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        gridPane.setPadding(new Insets(20, 20, 10, 10));

        Label warningText = new Label("If you will change a gui language, the application will destroy all unsaved data!");
        Label firstText = new Label("Select language");
        // ListCell<String>> cellFactory =
        List<String> listLang = getExistingLangPropertieFilesAsStringList();
        ObservableList<String> observableList = FXCollections.observableList(listLang);
        ComboBox comboBox = new ComboBox();
        comboBox.setItems(observableList);
     //   comboBox.setButtonCell(cellFactory.call(null));
     //   comboBox.setCellFactory(cellFactory);
        int indSelected = 0;
        String selectedLangString = getSelectedLangString();
        if (selectedLangString != null)
            indSelected = getSelectedLangIndex(selectedLangString);
        comboBox.getSelectionModel().select(indSelected);
        String strSelectedLang = null;
        if (comboBox.getSelectionModel().getSelectedIndex()>-1)
            strSelectedLang = (String)comboBox.getSelectionModel().getSelectedItem();

        warningText.setStyle("-fx-font-weight: bold");
        gridPane.add(warningText, 1, 0);
        gridPane.add(firstText, 1, 1);
        gridPane.add(comboBox, 1, 2);

        dialog.getDialogPane().setContent(gridPane);
        // Request focus on the username field by default.
        Platform.runLater(() -> comboBox.requestFocus());
        Optional<ButtonType> response = dialog.showAndWait();
        if (response.isPresent() && response.get().getButtonData() == ButtonData.OK_DONE) {
            if (comboBox.getSelectionModel().getSelectedIndex()>-1) {
                String newSelected = (String)comboBox.getSelectionModel().getSelectedItem();
                if (newSelected != null && !newSelected.equals(strSelectedLang)) {
                    System.out.println("newSelected=" + newSelected);
                    String [] langparts = newSelected.split("_");
                    if (langparts != null && langparts.length == 2)
                    {
                        Locale locale = new Locale(langparts[0], langparts[1]);
                        if (locale != null) {
                            JavaFxmlFileControllerI18.locale = locale;
                            saveConfigData();
                            System.out.println( "Restarting app!" );
                            primaryStage.close();
                            JavaFxmlFileConver18Application app = new JavaFxmlFileConver18Application();
                            Platform.runLater( () -> {
                            try {
                                app.start(primaryStage);
                            }catch (IOException e){
                                e.printStackTrace();
                            }});
                        }
                    }
                }
            }
        }
    }

    private String getSelectedLangString()
    {
        String strLang = locale.getLanguage() +"_" +locale.getCountry();
        return strLang;
    }

    private int getSelectedLangIndex(String strSelectedLang)
    {
        int index = 0;
        if (strSelectedLang != null && strSelectedLang.trim().length()>0)
        {
            List<String> langList = getExistingLangPropertieFilesAsStringList();
            int i = 0;
            for(String lang : langList)
            {
                if (lang == null) {
                    i++;
                    continue;
                }
                if (lang.equals(strSelectedLang))
                    return i;
                i++;
            }
        }
        return index;
    }

    private List<String> getExistingLangPropertieFilesAsStringList()
    {
        List<String> ret = new ArrayList<>();
        ret.add("en_UK");
      //  ret.add("fi_FI");
       //  ddddd
        return ret;
    }

    public static Locale getLocale()
    {
        return locale;
    }

    public static void setLanguageLocale()
    {
        String strUserHome = System.getProperty("user.home");
        if (strUserHome != null && strUserHome.trim().length()>0)
        {
            File userDir = new File(strUserHome);
            if (userDir.exists())
            {
                File appDir = new File(userDir.getAbsolutePath() +File.separator
                        +"javafxmli18app");
                if (!appDir.exists())
                    if (!appDir.mkdir())
                    {
                        System.err.println("Cannot create app dir in: ");
                        return;
                    }
                File appFile = new File(appDir.getAbsolutePath() +File.separator
                        +"config.properties");
                if (!appFile.exists())
                    return;
                try {
                   FileReader myReader = new FileReader(appFile);
                   BufferedReader bufferedReader = new BufferedReader(myReader);
                   String line = null;
                   while((line = bufferedReader.readLine()) != null)
                   {
                       if (line != null && line.trim().length()>0)
                       {
                           String search = "gui_language=";
                           int ind = line.indexOf(search);
                           if (ind > -1) {
                               String langValue = line.substring(ind +search.length());
                               if (langValue != null && langValue.trim().length()>0)
                               {
                                   String [] langparts = langValue.split("_");
                                   if (langparts != null && langparts.length ==2)
                                   {
                                       Locale locale = new Locale(langparts[0], langparts[1]);
                                       if (locale != null)
                                           JavaFxmlFileControllerI18.locale = locale;
                                   }
                               }
                           }
                           else {
                               search = cnst_last_fxml_file;
                               ind = line.indexOf(search);
                               if (ind > -1) {
                                   String strValue = line.substring(ind +search.length());
                                   if (strValue != null && strValue.trim().length()>0) {
                                       static_selectedFxmlFile = new File(strValue);
                                   }
                               }
                               else {
                                   search = cnst_last_lang_properties_file;
                                   ind = line.indexOf(search);
                                   if (ind > -1) {
                                       String strValue = line.substring(ind +search.length());
                                       if (strValue != null && strValue.trim().length()>0) {
                                           static_properties_file = new File(strValue);
                                       }
                                   }
                                   else {
                                       search = cnst_last_lang2_properties_file;
                                       ind = line.indexOf(search);
                                       if (ind > -1) {
                                           String strValue = line.substring(ind +search.length());
                                           if (strValue != null && strValue.trim().length()>0) {
                                               static_properties_file2 = new File(strValue);
                                           }
                                       }
                                   }
                               }
                           }
                           /*
                           if (ind > -1) {
                               if (selectedFxmlFile != null)
                               sb.append(cnst_last_fxml_file +selectedFxmlFile.getAbsolutePath() +"\n");
                           if (propertiesFile != null)
                               sb.append(cnst_last_lang_properties_file +propertiesFile.getAbsolutePath() +"\n");
                           if (propertiesFile2 != null)
                               sb.append(cnst_last_lang2_properties_file +propertiesFile2.getAbsolutePath() +"\n");
                           */
                       }
                   }
                   bufferedReader.close();
                   System.out.println("Successfully read from the file: " +appFile.getAbsolutePath());
                } catch (IOException e) {
                   System.out.println("An error occurred.");
                   e.printStackTrace();
                }
            }
        }
    }

    public static Locale locale = new Locale("en", "UK");
    public static boolean bUseMultiLang = true;
    private static final String cnst_last_fxml_file = "last_fxml_file=";
    private static final String cnst_last_lang_properties_file = "last_lang_properties_file=";
    private static final String cnst_last_lang2_properties_file = "last_lang2_properties_file=";
    private static File static_selectedFxmlFile;
    private static File static_properties_file;
    private static File static_properties_file2;

    private boolean saveConfigData()
    {
        String strUserHome = System.getProperty("user.home");
        if (strUserHome != null && strUserHome.trim().length()>0)
        {
            File userDir = new File(strUserHome);
            if (userDir.exists())
            {
                File appDir = new File(userDir.getAbsolutePath() +File.separator
                   +"javafxmli18app");
                if (!appDir.exists())
                    if (!appDir.mkdir())
                    {
                        labelMsg.setText("Cannot create app dir in: ");
                        return false;
                    }
                File appFile = new File(appDir.getAbsolutePath() +File.separator
                        +"config.properties");

                StringBuffer sb = new StringBuffer();
                Locale locale = JavaFxmlFileControllerI18.locale;
                if (locale != null) {
                    sb.append("gui_language=" +locale.getLanguage()  + "_" +locale.getCountry() +"\n");
                    // private File fPropertiesChooser = null;
                    if (selectedFxmlFile != null && !checkBoxEditPropFiles.isSelected()
                        && textAreaFxml.getText().length()>0)
                        sb.append(cnst_last_fxml_file +selectedFxmlFile.getAbsolutePath() +"\n");
                    if (propertiesFile != null && textAreaProperties.getText().length()>0)
                        sb.append(cnst_last_lang_properties_file +propertiesFile.getAbsolutePath() +"\n");
                    if (propertiesFile2 != null && checkBoxEditPropFiles.isSelected()
                            && textAreaFxml.getText().length()>0)
                        sb.append(cnst_last_lang2_properties_file +propertiesFile2.getAbsolutePath() +"\n");
                    try {
                        FileWriter myWriter = new FileWriter(appFile);
                        myWriter.write(sb.toString());
                        myWriter.close();
                        System.out.println("Successfully wrote to the file: " +appFile.getAbsolutePath());
                    } catch (IOException e) {
                        System.out.println("An error occurred.");
                        e.printStackTrace();
                    }
                }
            }
        }
        return true;
    }

}