/*
 * Copyright 2015, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.testing.uiautomator.BasicSample;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SdkSuppress;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.Until;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Basic sample for unbundled UiAutomator.
 */
@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 18)

public class CallingBehaviourTest {

    private static final int TIMEOUT = 5000;

    public static UiDevice mDevice;

    //Starts the Rebtel app at from its previous state before every test
    @Before
    public void startMainActivityFromHomeScreen() {
        // Initialize UiDevice instance
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        // Start from the home screen
        mDevice.pressHome();

        // Launch the app
        Context context = InstrumentationRegistry.getContext();
        final Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage(STRING.PACKAGE_NAME);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);    // Clear out any previous instances
        context.startActivity(intent);

        // Wait for the app to appear
        mDevice.wait(Until.hasObject(By.pkg(STRING.PACKAGE_NAME).depth(0)), TIMEOUT);
    }

    /**
     * Tests the login functionality in Rebtel
     *
     * @throws InterruptedException
     * @throws UiObjectNotFoundException
     * @throws IOException
     */
    @Test
    public void getStarted() throws InterruptedException, UiObjectNotFoundException, IOException {
        // Press the button.
        mDevice.findObject(By.res(STRING.PACKAGE_NAME, STRING.ID.GET_STARTED_BUTTON))
                .click();

        // Verify if the registration screen has appeared
        assertTrue("Phone number slot unavailable", waitUntilAppear(TIMEOUT, STRING.TEXT.VERIFY_YOUR_NUMBER));

        //Get the text of the input field
        String inputText = mDevice.findObject(By.res(STRING.PACKAGE_NAME, STRING.ID.PHONE_NUMBER_REGISTRATION))
                .getText();

        // Clear text if available
        if (null != inputText && !inputText.isEmpty()) {
            for (int i = 0; i < inputText.length(); i++) {
                mDevice.executeShellCommand("input keyevent KEYCODE_DEL");
            }
        }

        //Check if the Continue button is enabled
        assertFalse("Continue button is enabled", mDevice.hasObject(By.res(STRING.PACKAGE_NAME, inputText).enabled(true)));

        // Type phone number
        mDevice.findObject(By.res(STRING.PACKAGE_NAME, STRING.ID.PHONE_NUMBER_REGISTRATION))
                .setText(inputText);

        // Press the continue button.
        mDevice.findObject(By.res(STRING.PACKAGE_NAME, STRING.ID.CONTINUE_BUTTON))
                .click();

        //Wait for SMS Verification
        assertTrue("SMS confirmation did not finish", waitUntilAppear(10000, "successText") ||
                mDevice.wait(Until.hasObject
                        (By.res(STRING.PACKAGE_NAME, STRING.ID.PAGER_SCREEN)), TIMEOUT));
    }

    @Test
    public void dialNumber() throws InterruptedException, IOException {
        // Press the dial pad button from top bar
        mDevice.findObject(By.desc(STRING.TEXT.DIAL_PAD_ICON)).click();
        assertTrue("Dial pad is not opened", mDevice.wait(Until.hasObject
                (By.res(STRING.PACKAGE_NAME, STRING.ID.DIAL_PAD_TEXT_HOLDER)), TIMEOUT));

        //Clear the input field
        mDevice.findObject(By.res(STRING.PACKAGE_NAME, STRING.ID.DIAL_PAD_TEXT_HOLDER)).longClick();
        mDevice.executeShellCommand("input keyevent KEYCODE_DEL");

        //Dial a phone number
        mDevice.findObject(By.res(STRING.PACKAGE_NAME, STRING.ID.DIAL_PAD_TEXT_HOLDER)).setText(STRING.PHONE_NUMBER);

        //Press dial
        mDevice.findObject(By.desc(STRING.TEXT.CALL_BUTTON)).click();

        //Wait to place call
        Thread.sleep(10000);

        //Hang up call
        //mDevice.pressKeyCode(6);
        mDevice.executeShellCommand("input keyevent KEYCODE_ENDCALL");
        assertTrue("Did not return t application", mDevice.wait(Until.hasObject
                (By.res(STRING.PACKAGE_NAME, STRING.ID.PAGER_SCREEN)), TIMEOUT));

        // Press the home button from top bar
        mDevice.findObject(By.desc("recent")).click();
        assertTrue("Did not open pager", mDevice.wait(Until.hasObject
                (By.res(STRING.PACKAGE_NAME, STRING.ID.PAGER_SCREEN)), TIMEOUT));

        //Check if has an entry
        assertTrue("Entry not found correctly", mDevice.hasObject(By.text(STRING.PHONE_NUMBER)));

    }

    public Boolean waitUntilAppear(int timeout, String label) {
        return mDevice.wait(Until.hasObject(By.res(STRING.PACKAGE_NAME, label)), timeout);
    }

    public static Boolean waitUntilGone(String label) {
        return mDevice.wait(Until.gone(By.res(STRING.PACKAGE_NAME, label)), TIMEOUT);
    }
}
