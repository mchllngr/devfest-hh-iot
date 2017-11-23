package de.devfest.iot;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String TEAM_ID = "teamXY"; // TODO 01: set this to your team id (e.g. team01)

    // For a complete list of available ports exposed by the breakout connectors see https://developer.android.com/things/hardware/imx7d-pico-io.html
    private static final String GPIO_BUTTON_PIN_NAME = "GPIO_35";
    private static final String GPIO_LED_PIN_NAME = "GPIO_10";

    // TODO 02: get a FirebaseDatabase instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFirebaseDatabaseReference();
        setupButton();
        setupLed();
        setupFirebaseValueListener();

        Log.i(TAG, "### READY ###");
    }

    @Override
    protected void onDestroy() {
        destroyFirebaseValueListener();
        destroyLed();
        destroyButton();
        destroyFirebaseDatabaseReference();
        super.onDestroy();
    }

    private void setupFirebaseDatabaseReference() {
        // TODO 03: use the FirebaseDatabase to get a DatabaseReference from the field "TEAM_ID"
    }

    private void destroyFirebaseDatabaseReference() {
        // TODO 04: remove the DatabaseReference
    }

    private void setupButton() {
        // TODO 05: create a Button (located in package com.google.android.things.contrib.driver.button) for the button-gpio pin so that it is pressed when the signal is high
        // TODO 06: set an event listener to receive the button state when changed
    }

    private void destroyButton() {
        // TODO 07: destroy and remove the button
    }

    // TODO 08: at this point you should already be able to update your team-value in Firebase with the push of the button

    private void setupLed() {
        // TODO 09: create a gpio reference for the led using the PeripheralManagerService
        // TODO 10: set the gpio direction to out and initially low
    }

    private void destroyLed() {
        // TODO 11: destroy and remove the led
    }

    private void setupFirebaseValueListener() {
        // TODO 12: add a ValueEventListener to the DatabaseReference to receive value updates
        // TODO 13: when receiving a value update set the new value to the led (hint: you may need to cast the value to boolean)
    }

    private void destroyFirebaseValueListener() {
        // TODO 14: remove the ValueEventListener
    }
}
