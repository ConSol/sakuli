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

package org.sakuli.integration.ui;

import javafx.stage.Stage;
import org.sakuli.integration.IntegrationTest;
import org.sakuli.javaDSL.TestCaseInitParameter;
import org.sakuli.javaDSL.actions.Key;
import org.sakuli.javaDSL.actions.Region;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * @author tschneck
 *         Date: 09.04.14
 */
@Test(groups = IntegrationTest.GROUP_UI)
public class TypeActionIT extends AbstractUiTestApplicationIT {

    @Override
    protected String getTestCaseId() {
        return "Type_Action_Test";
    }

    @Override
    protected TestCaseInitParameter getTestCaseInitParameter() throws Throwable {
        return new TestCaseInitParameter(getUniqueTestCaseId(), IMAGE_LIB_FOLDER_NAME);
    }

    @Test
    public void testLoginAction() throws Exception {
        env.setSimilarity(0.7);
        Stage stage = startUiApplication();
        new Region("app_logo").waitFor(10);
        new Region("username").click().type("demo");
        new Region("password").click().typeMasked("demo");
        assertNotNull(new Region("username_filled").find());
        assertNotNull(new Region("password_filled").find());


        new Region("login_bt").click();
        assertTrue(new Region("profil").exists() != null);

        new Region("email").right(50).click().paste("test@sakuli.de");
        env.type(Key.TAB + "089-123456");
        env.type(Key.TAB).typeMasked("capital letters", Key.SHIFT);

        new Region("subscribe_newsletter").find("checkbox").click();
        Arrays.asList("email_filled", "phone_filled", "subscribe_newsletter_filled", "address_filled")
                .forEach(pic -> {
                    logger.info("check pic '{}'", pic);
                    assertNotNull(new Region().exists(pic));
                });
        new Region("save").click();

        assertNotNull(new Region().exists("successfull_updated"));
        new Region("logout").click();

        assertNotNull(new Region().exists("app_logo"));
        assertNotNull(new Region("login_bt").find());

        stopUiApplication(stage);
    }

}
