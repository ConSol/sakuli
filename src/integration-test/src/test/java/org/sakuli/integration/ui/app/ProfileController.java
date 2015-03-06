/*
 * Copyright (c) 2008, 2012 Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
