package com.metait.javafxmlfileI18convert;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;
import java.io.IOException;

// import jfxtras.styles.jmetro.Style;
// import jfxtras.styles.jmetro.JMetro;

public class JavaFxmlFileConver18Application extends Application {

    private Stage m_primaryStage;
    private JavaFxmlFileControllerI18 controller = new JavaFxmlFileControllerI18();
    public JavaFxmlFileControllerI18 getController()
    {
        return controller;
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(JavaFxmlFileConver18Application.class.getResource("javafxmlfileconverti18-view.fxml"));
        boolean bMultiLanguage = JavaFxmlFileControllerI18.bUseMultiLang;
        Locale locale = new Locale("en", "UK");
        JavaFxmlFileControllerI18.locale = locale;
        JavaFxmlFileControllerI18.setLanguageLocale();
        locale = JavaFxmlFileControllerI18.getLocale();
        if (bMultiLanguage) {
            ResourceBundle bundle = ResourceBundle.getBundle("com/metait/javafxmlfileI18convert/lang", locale);
            // System.out.println("path=" +JavaFxmlFileConver18Application.class.getPackageName().toString());
            String strPath = JavaFxmlFileConver18Application.class.getPackageName().replaceAll("\\.", "/");
            // FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("javafxmlfileconverti18-view.fxml"));
            fxmlLoader = new FXMLLoader(getClass().getResource("javafxmlfileconverti18-viewtemplate.fxml"), bundle);
        }
        // remove xml block from .fxml file: fx:controller="com.metait.javafxplayer.PlayerController"
        fxmlLoader.setController(controller);
        m_primaryStage = stage;
        controller.setPrimaryStage(m_primaryStage);
        // Parent loadedroot = fxmlLoader.load();
        Scene scene = new Scene(fxmlLoader.load(), 1000, 800);
      //  JMetro jMetro = new JMetro(scene, Style.LIGHT);
        stage.setTitle("JavaFxmlFileI18Convert");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}