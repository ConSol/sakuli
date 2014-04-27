/*
 * Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved.
 */
package de.consol.sakuli.integration.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DemoApplication Application. This class handles navigation and user session.
 */
public class DemoApplication extends Application implements Runnable {

    private static final double MINIMUM_WINDOW_WIDTH = 390.0;
    private static final double MINIMUM_WINDOW_HEIGHT = 500.0;
    private Stage stage;
    private User loggedUser;

    @Override
    public void run() {
        Application.launch(DemoApplication.class, (String[]) null);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            stage = primaryStage;
            stage.setTitle("FXML Login Sample");
            stage.setMinWidth(MINIMUM_WINDOW_WIDTH);
            stage.setMinHeight(MINIMUM_WINDOW_HEIGHT);
            gotoLogin();
            primaryStage.show();
        } catch (Exception ex) {
            Logger.getLogger(DemoApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public User getLoggedUser() {
        return loggedUser;
    }

    public boolean userLogging(String userId, String password) {
        if (Authenticator.validate(userId, password)) {
            loggedUser = User.of(userId);
            gotoProfile();
            return true;
        } else {
            return false;
        }
    }

    void userLogout() {
        loggedUser = null;
        gotoLogin();
    }

    private void gotoProfile() {
        try {
            ProfileController profile = (ProfileController) getController("Profile.fxml");
            profile.setApp(this);
        } catch (Exception ex) {
            Logger.getLogger(DemoApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void gotoLogin() {
        try {
            LoginController login = (LoginController) getController("Login.fxml");
//            (LoginController) replaceSceneContent("Login.fxml");
            login.setApp(this);
        } catch (Exception ex) {
            Logger.getLogger(DemoApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Initializable getController(String fxml) throws IOException {
        URL resourceURl = getClass().getResource(fxml);
        FXMLLoader loader = new FXMLLoader(resourceURl);
        //load defined page
        AnchorPane page = (AnchorPane) loader.load();
        //add page to stage
        Scene scene = new Scene(page, 800, 600);
        stage.setScene(scene);
        stage.sizeToScene();
        //return the corresponding controller
        return loader.getController();
    }
}
