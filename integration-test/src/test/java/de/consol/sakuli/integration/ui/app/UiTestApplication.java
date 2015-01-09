/*
 * Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved.
 */
package de.consol.sakuli.integration.ui.app;

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

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * UiTestApplication Application. This class handles navigation and user session.
 */
public class UiTestApplication extends Application implements Runnable {

    public static final Logger LOGGER = Logger.getLogger(UiTestApplication.class.getName());
    public static Stage stage;
    protected static Map<UiTestEvent, Map<EventType<? extends Event>, EventHandler<? super Event>>> loginControllerEvents;
    protected static Map<UiTestEvent, Map<EventType<? extends Event>, EventHandler<? super Event>>> profileControllerEvents;
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

    public static void main(String[] args) {
        LOGGER.info("............................START");
        final UiTestApplication uiTestApplication = new UiTestApplication();
        new JFXPanel();
        Platform.runLater(uiTestApplication);
    }

    @Override
    public void run() {
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        //create Login scene
        Scene loginScene = gotoLogin();
        final double height = bounds.getHeight() - 100;
//        final double height = bounds.getHeight();

        Stage stage = StageBuilder.create()
                .x(0).y(0)
                .width(bounds.getWidth())
                .height(height)
                .title("Sakuli Login Sample")
                .scene(loginScene)
                .onCloseRequest(new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent windowEvent) {
                        LOGGER.info("PLATFORM exit!");
                        Platform.exit();
                    }
                }).build();
        start(stage);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            stage = primaryStage;
            primaryStage.showAndWait();
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

    private Scene gotoProfile() {
        try {
            LOGGER.info("GO TO PROFILE PAGE!");
            return getFxmlScene("Profile.fxml", profileControllerEvents);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private Scene gotoLogin() {
        try {
            LOGGER.info("GO TO LOGIN PAGE!");
            return getFxmlScene("Login.fxml", loginControllerEvents);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return null;
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
