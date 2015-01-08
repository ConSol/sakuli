/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2014 the original author or authors.
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

package de.consol.sakuli.integration.ui;

import de.consol.sakuli.integration.IntegrationTest;
import de.consol.sakuli.integration.ui.app.UiTestApplication;
import de.consol.sakuli.javaDSL.TestCaseInitParameter;
import de.consol.sakuli.javaDSL.actions.Region;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.testng.annotations.Test;

import java.util.concurrent.ConcurrentHashMap;

import static de.consol.sakuli.integration.ui.app.UiTestEvent.LOGIN_BT;
import static org.testng.Assert.assertEquals;

/**
 * @author tschneck
 *         Date: 09.04.14
 */
@Test(groups = IntegrationTest.GROUP_UI)
public class ClickActionIT extends AbstractUiTestApplicationIT {

    @Override
    protected String getTestCaseId() {
        return "Click_Action_Test";
    }

    @Override
    protected TestCaseInitParameter getTestCaseInitParameter() throws Throwable {
        return new TestCaseInitParameter(getUniqueTestCaseId(), IMAGE_LIB_FOLDER_NAME);
    }

    public void testWaitForClickAction() throws Exception {
        final int expectedClickCount = 2;
        Stage stage = startUiApplication();
        /**
         * SAKULI ACTIONS
         */
//        env.sleep(2);

        //opt 1
        Region region = new Region();
        region.waitForImage("login_bt.png", 3).doubleClick();

        //assert the count of button clicks
        stopUiApplication(stage);
        assertEquals(getEventCount(LOGIN_BT), expectedClickCount);
    }

    @Test
    public void testClickAction() throws Exception {
        final int expectedClickCount = 3;
//        env.setSimilarity(0.85);
        Stage stage = startUiApplication();
        /**
         * SAKULI ACTIONS
         */

        //opt 1
//        env.sleep(9999);
        de.consol.sakuli.actions.screenbased.Region region = new Region().exists("login_bt.png", 5);
        region.find().click();
//        assertEquals(getEventCount(LOGIN_BT), 1);
        //opt 2
        new Region().find("username").doubleClick().type("demo");
        new Region().find("login_bt").click();
//        new Region().find("password").doubleClick().type("demo");
        new Region("password").doubleClick().type("demo");
        new Region("login_bt").click().sleep(2);
//        new Region().find("login_bt").click().sleep(2);
//        assertEquals(getEventCount(LOGIN_BT), 2);
        assertEquals(getEventCount(LOGIN_BT), 3);
//        env.sleep(2);

//        new Region("login_bt.png").find().click();
//        env.sleep(2);
        //assert the count of button clicks
        stopUiApplication(stage);
    }

    protected Stage startUiApplication() {
        UiTestApplication.cleanAllEvents();
        eventCounter = new ConcurrentHashMap<>();
        UiTestApplication.addLoginControllEvent(LOGIN_BT, MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                logger.info("---> MOUSE EVENT triggered");
                countEvent(LOGIN_BT);
            }
        });
        return super.startUiApplication();
    }

}
