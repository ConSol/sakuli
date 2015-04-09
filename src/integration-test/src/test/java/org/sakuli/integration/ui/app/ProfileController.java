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

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Profile Controller.
 */
public class ProfileController extends AbstractUiTestPane {

    @FXML
    private TextField user;
    @FXML
    private TextField phone;
    @FXML
    private TextField email;
    @FXML
    private TextArea address;
    @FXML
    private CheckBox subscribed;
    @FXML
    private Hyperlink logout;
    @FXML
    private Button save;

    @FXML
    private Label success;


    @Override
    public void setApp(UiTestApplication application, Map<UiTestEvent, Map<EventType<? extends Event>, EventHandler<? super Event>>> controllerEvents) {
        super.setApp(application);
        if (controllerEvents != null) {
            for (UiTestEvent event : controllerEvents.keySet()) {
                switch (event) {
                    case PROFILE_USER_TF:
                        addEventHandlers(user, controllerEvents.get(event));
                        break;
                    case PROFILE_PHONE_TF:
                        addEventHandlers(phone, controllerEvents.get(event));
                        break;
                    case PROFILE_EMAIL_TF:
                        addEventHandlers(email, controllerEvents.get(event));
                        break;
                    case PROFILE_ADDRESS_TA:
                        addEventHandlers(address, controllerEvents.get(event));
                        break;
                    case PROFILE_SUBSCRIBED_CB:
                        addEventHandlers(subscribed, controllerEvents.get(event));
                        break;
                    case PROFILE_LOG_OUT_HL:
                        addEventHandlers(subscribed, controllerEvents.get(event));
                        break;
                    case PROFILE_SAVE_BT:
                        addEventHandlers(subscribed, controllerEvents.get(event));
                        break;
                }
            }
        }
        User loggedUser = application.getLoggedUser();
        user.setText(loggedUser.getId());
        email.setText(loggedUser.getEmail());
        phone.setText(loggedUser.getPhone());
        if (loggedUser.getAddress() != null) {
            address.setText(loggedUser.getAddress());
        }
        subscribed.setSelected(loggedUser.isSubscribed());
        success.setOpacity(0);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void processLogout(ActionEvent event) {
        if (application == null) {
            // We are running in isolated FXML, possibly in Scene Builder.
            // NO-OP.
            return;
        }

        application.userLogout();
    }

    public void saveProfile(ActionEvent event) {
        if (application == null) {
            // We are running in isolated FXML, possibly in Scene Builder.
            // NO-OP.
            animateMessage();
            return;
        }
        User loggedUser = application.getLoggedUser();
        loggedUser.setEmail(email.getText());
        loggedUser.setPhone(phone.getText());
        loggedUser.setSubscribed(subscribed.isSelected());
        loggedUser.setAddress(address.getText());
        animateMessage();
    }

    public void resetProfile(ActionEvent event) {
        if (application == null) {
            return;
        }
        email.setText("");
        phone.setText("");
        subscribed.setSelected(false);
        address.setText("");
        success.setOpacity(0.0);

    }

    private void animateMessage() {
        FadeTransition ft = new FadeTransition(Duration.millis(1000), success);
        ft.setFromValue(0.0);
        ft.setToValue(1);
        ft.play();
    }
}
