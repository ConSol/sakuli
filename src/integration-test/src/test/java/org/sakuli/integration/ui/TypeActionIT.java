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

package org.sakuli.integration.ui;

import javafx.stage.Stage;
import org.sakuli.actions.screenbased.Key;
import org.sakuli.actions.screenbased.Region;
import org.sakuli.integration.IntegrationTest;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * @author tschneck Date: 09.04.14
 */
@Test(groups = IntegrationTest.GROUP_UI)
public class TypeActionIT extends AbstractUiTestApplicationIT {

    @Test
    public void testLoginAction() throws Exception {
        env.setSimilarity(0.7);
        Stage stage = startUiApplication();
        new Region().waitForImage("app_logo", 10);
        new Region().find("username").click().type("demo");
        new Region().find("password").click().typeMasked("demo");
        assertNotNull(new Region().find("username_filled"));
        assertNotNull(new Region().find("password_filled"));


        new Region().find("login_bt").click();
        assertTrue(new Region().exists("profil") != null);

        new Region().find("email").right(50).click().paste("test@sakuli.de");
        env.type(Key.TAB + "089-123456");
        env.type(Key.TAB).typeMasked("capital letters", Key.SHIFT);

        new Region().find("subscribe_newsletter").find("checkbox").click();
        List<String> pics = Arrays.asList("email_filled", "phone_filled", "subscribe_newsletter_filled", "address_filled");
        for(String pic : pics){
            logger.info("check pic '{}'", pic);
            assertNotNull(new Region().exists(pic));
        }
        new Region().find("save").click();

        assertNotNull(new Region().exists("successfull_updated"));
        new Region().find("logout").click();

        assertNotNull(new Region().exists("app_logo"));
        assertNotNull(new Region().find("login_bt"));

        stopUiApplication(stage);
    }

}
