/*
 * Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved.
 */
package de.consol.sakuli.integration.ui.app;

import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * UiTestApplication Application. This class handles navigation and user session.
 */
public class UiTestApplication extends Application implements Callable<Long> {

    private static final double MINIMUM_WINDOW_WIDTH = 390.0;
    private static final double MINIMUM_WINDOW_HEIGHT = 500.0;
    protected static Map<UiTestEvent, Map<EventType<? extends Event>, EventHandler<? super Event>>> loginControllerEvents;
    protected static Map<UiTestEvent, Map<EventType<? extends Event>, EventHandler<? super Event>>> profileControllerEvents;
    private Stage stage;
    private User loggedUser;

    public static void cleanAllEvents() {
        loginControllerEvents = null;
        profileControllerEvents = null;
    }

    public static void addLoginControllEvent(UiTestEvent testEvent, EventType<? extends Event> eventType, EventHandler<? extends Event> eventHandler) {
        loginControllerEvents = updateEventMap(loginControllerEvents, testEvent, eventType, eventHandler);
    }

    private static Map<UiTestEvent, Map<EventType<? extends Event>, EventHandler<? super Event>>> updateEventMap(Map<UiTestEvent, Map<EventType<? extends Event>, EventHandler<? super Event>>> eventMap, UiTestEvent testEvent, EventType<? extends Event> eventType, EventHandler<? extends Event> eventHandler) {
        if (eventMap == null) {
            eventMap = new ConcurrentHashMap<>();
        }
        Map<EventType<? extends Event>, EventHandler<? super Event>> entryMap;
        if (eventMap.containsKey(testEvent)) {
            entryMap = eventMap.get(testEvent);
        } else {
            entryMap = new ConcurrentHashMap<>();
        }
        entryMap.put(eventType, (EventHandler<? super Event>) eventHandler);
        eventMap.put(testEvent, entryMap);
        return eventMap;
    }

    public static void addProfileControllEvent(UiTestEvent testEvent, EventType<? extends Event> eventType, EventHandler<? extends Event> eventHandler) {
        profileControllerEvents = updateEventMap(loginControllerEvents, testEvent, eventType, eventHandler);
    }

    @Override
    public Long call() throws Exception {
        Date startDate = new Date();
        Application.launch(UiTestApplication.class, (String[]) null);
        return (new Date().getTime() - startDate.getTime()) / 1000;
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            stage = primaryStage;
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();

            stage.setX(bounds.getMinX());
            stage.setY(bounds.getMinY());
            stage.setWidth(bounds.getWidth());
            stage.setHeight(bounds.getHeight());
            stage.setTitle("Sakuli Login Sample");

//            stage.setMinWidth(MINIMUM_WINDOW_WIDTH);
//            stage.setMinHeight(MINIMUM_WINDOW_HEIGHT);
            gotoLogin();
            primaryStage.show();
        } catch (Exception ex) {
            Logger.getLogger(UiTestApplication.class.getName()).log(Level.SEVERE, null, ex);
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
            ProfileController profileController = (ProfileController) getController("Profile.fxml");
            profileController.setApp(this, profileControllerEvents);
        } catch (Exception ex) {
            Logger.getLogger(UiTestApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void gotoLogin() {
        try {
            LoginController loginController = (LoginController) getController("Login.fxml");
            loginController.setApp(this, loginControllerEvents);
        } catch (Exception ex) {
            Logger.getLogger(UiTestApplication.class.getName()).log(Level.SEVERE, null, ex);
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
