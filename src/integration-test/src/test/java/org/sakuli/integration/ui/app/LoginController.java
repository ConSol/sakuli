/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sakuli.integration.ui.app;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Login Controller.
 */
public class LoginController extends AbstractUiTestPane {

    @FXML
    TextField userId;
    @FXML
    PasswordField password;
    @FXML
    Button login;
    @FXML
    Label errorMessage;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        errorMessage.setText("");
    }


    public void processLogin(ActionEvent event) {
        if (application == null) {
            // We are running in isolated FXML, possibly in Scene Builder.
            // NO-OP.
            errorMessage.setText("Hello " + userId.getText());
        } else {
            if (!application.userLogging(userId.getText(), password.getText())) {
                errorMessage.setText("Username/Password is incorrect");
            }
        }
    }

    @Override
    public void setApp(UiTestApplication application, Map<UiTestEvent, Map<EventType<? extends Event>, EventHandler<? super Event>>> controllerEvents) {
        super.setApp(application);
        if (controllerEvents != null) {
            for (UiTestEvent event : controllerEvents.keySet()) {
                switch (event) {
                    case LOGIN_BT:
                        addEventHandlers(login, controllerEvents.get(event));
                        break;
                    case PASSWORD_TF:
                        addEventHandlers(password, controllerEvents.get(event));
                        break;
                    case USER_TF:
                        addEventHandlers(userId, controllerEvents.get(event));
                        break;
                }
            }
        }
    }

}
