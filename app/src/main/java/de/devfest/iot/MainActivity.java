package de.devfest.iot;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.things.contrib.driver.button.Button;
import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String TEAM_ID = "team00"; // TODO 01: set this to your team id

    // For a complete list of available ports exposed by the breakout connectors see https://developer.android.com/things/hardware/imx7d-pico-io.html
    private static final String GPIO_BUTTON_PIN_NAME = "GPIO_35";
    private static final String GPIO_LED_PIN_NAME = "GPIO_10";

    // TODO 02: get a FirebaseDatabase instance
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference;
    private Button button;
    private Gpio led;
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFirebaseDatabaseReference();
        setupButton();
        setupLed();
        setupFirebaseValueListener();
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
        databaseReference = firebaseDatabase.getReference(TEAM_ID);
    }

    private void destroyFirebaseDatabaseReference() {
        // TODO 04: remove the DatabaseReference
        databaseReference = null;
    }

    private void setupButton() {
        try {
            // high signal indicates the button is pressed (use with a pull-down resistor)
            button = new Button(GPIO_BUTTON_PIN_NAME, Button.LogicState.PRESSED_WHEN_HIGH);
            button.setOnButtonEventListener(new Button.OnButtonEventListener() {
                @Override
                public void onButtonEvent(Button button, boolean pressed) {
                    Log.v(TAG, "onButtonEvent: pressed=" + pressed);
                    databaseReference.setValue(pressed);
                }
            });
        } catch (IOException e) {
            Log.e(TAG, "Error configuring the button", e);
        }
    }

    private void destroyButton() {
        if (button != null) {
            try {
                button.setOnButtonEventListener(null);
                button.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing button", e);
            } finally {
                button = null;
            }
        }
    }

    private void setupLed() {
        PeripheralManagerService peripheralManagerService = new PeripheralManagerService();
        try {
            led = peripheralManagerService.openGpio(GPIO_LED_PIN_NAME);
            led.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
        } catch (IOException e) {
            Log.e(TAG, "Error configuring the led", e);
        }
    }

    private void destroyLed() {
        if (led != null) {
            try {
                led.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing led", e);
            } finally {
                led = null;
            }
        }
    }

    private void setupFirebaseValueListener() {
        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // In general we would need to be more careful before casting the value,
                // but in this example we know that this is of type boolean
                boolean value = (boolean) dataSnapshot.getValue();
                setLedValue(value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { /* ignored */ }
        });
    }

    private void setLedValue(boolean value) {
        try {
            led.setValue(value);
        } catch (IOException e) {
            Log.e(TAG, "Error setting led value", e);
        }
    }

    private void destroyFirebaseValueListener() {
        if (valueEventListener != null) {
            databaseReference.removeEventListener(valueEventListener);
            valueEventListener = null;
        }
    }
}
