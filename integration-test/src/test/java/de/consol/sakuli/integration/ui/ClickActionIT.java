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

import de.consol.sakuli.actions.screenbased.Region;
import de.consol.sakuli.integration.IntegrationTest;
import de.consol.sakuli.integration.ui.app.UiTestApplication;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import org.testng.annotations.Test;

import java.util.concurrent.Future;

import static de.consol.sakuli.integration.ui.app.UiTestEvent.LOGIN_BT;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author tschneck
 *         Date: 09.04.14
 */
@Test(groups = IntegrationTest.GROUP)
public class ClickActionIT extends AbstractUiTestApplicationIT {

    @Override
    protected String getTestCaseId() {
        return "Click_Action_Test";
    }

    @Test
    public void testClickAction() throws Exception {
        final int expectedCount = 2;
        //set click event handler
        UiTestApplication.addLoginControllEvent(LOGIN_BT, MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                logger.info("mouse event triggered");
                countEvent(LOGIN_BT);
                if (getEventCount(LOGIN_BT) >= expectedCount) {
                    Platform.exit();
                }
            }
        });
        Future<Long> future = startUiTestApplication();

        /**
         * SAKULI ACTIONS
         */
        env.sleep(2);

        //opt 1
        new Region("login_bt", false, getScreenActionLoader()).find().click();
        Region region = new Region(false, getScreenActionLoader());
        region.find("login_bt.png").click().highlight();
        //opt 2
        new Region("login_bt", false, getScreenActionLoader()).click();
        //opt3

        //assert that the runtime of the application fits
        assertTrue(future.get() > 0);
        //assert the count of button clicks
        assertEquals(getEventCount(LOGIN_BT), expectedCount);

    }
}
