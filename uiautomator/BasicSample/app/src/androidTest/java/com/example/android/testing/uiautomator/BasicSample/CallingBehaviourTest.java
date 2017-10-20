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
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Basic sample for unbundled UiAutomator.
 */
@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 18)

public class CallingBehaviourTest {


    private static final int LAUNCH_TIMEOUT = 5000;

    public static UiDevice mDevice;

    @Before
    public void startMainActivityFromHomeScreen() {
        // Initialize UiDevice instance
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        // Start from the home screen
        mDevice.pressHome();

        /**Wait for launcher
         final String launcherPackage = getLauncherPackageName();
         assertThat(launcherPackage, notNullValue());
         mDevice.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT);*/

        // Launch the blueprint app
        Context context = InstrumentationRegistry.getContext();
        final Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage(Strings.BASIC_SAMPLE_PACKAGE);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);    // Clear out any previous instances
        context.startActivity(intent);

        // Wait for the app to appear
        mDevice.wait(Until.hasObject(By.pkg(Strings.BASIC_SAMPLE_PACKAGE).depth(0)), LAUNCH_TIMEOUT);
    }

    @Test
    public void getStarted() throws InterruptedException {
        // Press the button.
        mDevice.findObject(By.res(Strings.BASIC_SAMPLE_PACKAGE, "getStartedButton"))
                .click();

        // Verify if the new screen has appeared
        //hread.sleep(5000);
        assertTrue("Phone number slot unavailable", waitUntilAppear(5000, "sign_up_headline"));


        // Type phone number
        mDevice.findObject(By.res(Strings.BASIC_SAMPLE_PACKAGE, "inputPhoneNumber"))
                .setText(Strings.PHONE_NUMBER);

        //Thread.sleep(5000);
        UiObject2 inputField = mDevice.wait(Until.findObject(By.res(Strings.BASIC_SAMPLE_PACKAGE, "inputPhoneNumber")), LAUNCH_TIMEOUT);

        // Press the continue button.
        mDevice.findObject(By.res(Strings.BASIC_SAMPLE_PACKAGE, "verifyButton"))
                .click();
        assertTrue("Phone number is not typed", waitUntilGone("verifyButton"));

        //Wait for SMS Verification
        Thread.sleep(10000);
    }

    @Test
    public void dialNumber() throws InterruptedException, IOException {
        // Press the dial pad button from top bar
        mDevice.findObject(By.desc("dialpad")).click();

        // Clear text
        for(int i=0; i<3 ; i++) {
            Thread.sleep(500);
            mDevice.findObject(By.desc("Erase")).click();
        }

        //Dial a phone number
        mDevice.findObject(By.desc("Pressed numbers")).setText(Strings.PHONE_NUMBER);

        //Press dial
        mDevice.findObject(By.desc("Call")).click();

        //Wait to place call
        Thread.sleep(5000);

        //Hang up call
        mDevice.executeShellCommand("input keyevent KEYCODE_ENDCALL");
        Thread.sleep(5000);

        // Press the home button from top bar
        mDevice.findObject(By.desc("recent")).click();

        //Check if has an entry
        assertTrue("Entry not found correctly", mDevice.hasObject(By.text(Strings.PHONE_NUMBER)));

    }
    public Boolean waitUntilAppear(int timeout, String label) {
        return mDevice.wait(Until.hasObject(By.res(Strings.BASIC_SAMPLE_PACKAGE, label)), timeout);
    }

    public static Boolean waitUntilGone(String label) {
        return mDevice.wait(Until.gone(By.res(Strings.BASIC_SAMPLE_PACKAGE, label)), LAUNCH_TIMEOUT);
    }
}
