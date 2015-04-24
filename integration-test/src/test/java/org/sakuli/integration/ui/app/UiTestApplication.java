/*
 * Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved.
 */
package org.sakuli.integration.ui.app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageBuilder;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * UiTestApplication Application. This class handles navigation and user session.
 */
public class UiTestApplication extends Application implements Runnable {

    public static final Logger LOGGER = LoggerFactory.getLogger(UiTestApplication.class.getName());
    public static final double HEIGHT_PERCENTAGE = 0.9;
    public static Stage stage;
    protected static Map<UiTestEvent, Map<EventType<? extends Event>, EventHandler<? super Event>>> loginControllerEvents;
    protected static Map<UiTestEvent, Map<EventType<? extends Event>, EventHandler<? super Event>>> profileControllerEvents;
    private User loggedUser;
    private double width;
    private double height;

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

    public static void main(String[] args) {
        LOGGER.info("............................START");
        final UiTestApplication uiTestApplication = new UiTestApplication();
        new JFXPanel();
        Platform.runLater(uiTestApplication);
    }

    @Override
    public void run() {
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        //create Login scene
        Scene loginScene = gotoLogin();
        width = bounds.getWidth();
        height = bounds.getHeight() * HEIGHT_PERCENTAGE;
        Stage stage = StageBuilder.create()
                .x(0).y(0)
                .width(width)
                .height(height)
                .title("Sakuli Login Sample")
                .scene(loginScene)
                .onCloseRequest(new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent event) {
                        LOGGER.info("PLATFORM exit!");
                    }
                }).build();
        LOGGER.info("set width '{}' and height '{}'", width, height);
        start(stage);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            stage = primaryStage;
            stage.show();
//            primaryStage.showAndWait();
        } catch (Exception ex) {
            LOGGER.error("start up error", ex);
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

    private Scene gotoProfile() {
        try {
            LOGGER.info("GO TO PROFILE PAGE!");
            Scene fxmlScene = getFxmlScene("Profile.fxml", profileControllerEvents);
            updateStageSize();
            return fxmlScene;
        } catch (Exception ex) {
            LOGGER.error("error in profile scene", ex);
        }
        return null;
    }

    private Scene gotoLogin() {
        try {
            LOGGER.info("GO TO LOGIN PAGE!");
            Scene fxmlScene = getFxmlScene("Login.fxml", loginControllerEvents);
            updateStageSize();
            return fxmlScene;
        } catch (Exception ex) {
            LOGGER.error("error in login page scene", ex);
        }
        return null;
    }

    private void updateStageSize() {
        if (stage != null) {
            stage.setWidth(width);
            stage.setHeight(height);
            LOGGER.info("set width '{}' and height '{}'", width, height);
        }
    }

    private Scene getFxmlScene(String fxml, Map<UiTestEvent, Map<EventType<? extends Event>, EventHandler<? super Event>>> controllerEvents) throws IOException {
        URL resourceURl = getClass().getResource(fxml);
        FXMLLoader loader = new FXMLLoader(resourceURl);
        //load defined page
        AnchorPane page = (AnchorPane) loader.load();
        //add page to stage
        Scene scene = new Scene(page, 800, 600);
        if (stage != null) {
            stage.setScene(scene);
            stage.sizeToScene();
        }

        //return the corresponding controller
        ((AbstractUiTestPane) loader.getController()).setApp(this, controllerEvents);
        return scene;
    }
}
