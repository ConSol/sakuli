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
import de.consol.sakuli.javaDSL.TestCaseInitParameter;
import de.consol.sakuli.javaDSL.actions.Key;
import de.consol.sakuli.javaDSL.actions.Region;
import javafx.stage.Stage;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.testng.Assert.*;

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
        Stage stage = startUiApplication();
        new Region("username").click().type("demo");
        new Region("password").click().typeMasked("demo");
        assertNotNull(new Region("username_filled").find());
        assertNotNull(new Region("password_filled").find());


        new Region("login_bt").click();
        assertTrue(new Region("profil").exists() != null);

        env.type(Key.TAB).paste("test@sakuli.de");
        env.type(Key.TAB + "089-123456");
        env.type(Key.TAB).typeMasked("capital letters", Key.SHIFT);

        new Region("subscribe_newsletter").find("checkbox").click();
        for (String filledRegionPattern : Arrays.asList("email_filled", "phone_filled", "subscribe_newsletter_filled")) {
            assertNotNull(new Region().exists(filledRegionPattern));
        }
        new Region("save").click();

        assertNotNull(new Region().exists("successfull_updated"));
        new Region("logout").click();

        assertNull(new Region().exists("profil"));
        assertNotNull(new Region("login_bt").find());

        //
        //TODO assert click event SAVE
        stopUiApplication(stage);
    }

}
