/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2017 the original author or authors.
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

package org.sakuli.aop;

import org.sakuli.actions.environment.Environment;
import org.sakuli.actions.screenbased.TypingUtil;
import org.sakuli.actions.screenbased.UserInterfaceInputActionCallback;
import org.sakuli.datamodel.actions.LogLevel;
import org.sakuli.loader.BeanLoader;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.Test;

import java.util.Collection;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

/**
 * @author tschneck
 * Date: 4/24/17
 */
public class UserInterfaceInputActionExecutedAspectTest extends AopBaseTest {

    @Test
    public void testDoEnvironmentLog() throws Exception {
        TypingUtil typingUtil = mock(TypingUtil.class);
        Environment testAction = new Environment(false);
        ReflectionTestUtils.setField(testAction, "typingUtil", typingUtil);
        testAction.type("nothing");

        verify(typingUtil).type(eq("nothing"), isNull());
        assertLastLine(logFile, testAction.getClass().getSimpleName(), LogLevel.INFO,
                "Environment.type() - type over system keyboard with arg(s) [nothing]");
        final Collection<UserInterfaceInputActionCallback> callbacks = BeanLoader.loadMultipleBeans(UserInterfaceInputActionCallback.class).values();
        assertEquals(callbacks.size(), 1);
        final UserInterfaceInputActionCallback cb = callbacks.stream().findFirst().get();
        verify(cb).afterUserInterfaceInput(any());
        verify(cb).beforeUserInterfaceInput(any());
    }
}