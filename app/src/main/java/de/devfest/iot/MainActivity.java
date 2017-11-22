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

    private static final String TEAM_ID = "00"; // TODO 01: set this to your team id
    private static final String GPIO_BUTTON_PIN_NAME = "GPIO_35";
    private static final String GPIO_LED_PIN_NAME = "GPIO_10";

    private DatabaseReference databaseReference;
    private Button button;
    private Gpio led;
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFirebaseDatabase();
        setupButton();
        setupLed();
    }

    private void setupFirebaseDatabase() {
        databaseReference = FirebaseDatabase.getInstance().getReference("team" + TEAM_ID);
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
            // couldn't configure the button...
        }
    }

    private void setupLed() {
        PeripheralManagerService peripheralManagerService = new PeripheralManagerService();
        try {
            led = peripheralManagerService.openGpio(GPIO_LED_PIN_NAME);
            led.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);

            valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    boolean value = (boolean) dataSnapshot.getValue(); // TODO add comment why boolean
                    setLedValue(value);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) { /* ignored */ }
            });
        } catch (IOException e) {
            // couldn't configure the led...
        }
    }

    private void setLedValue(boolean value) {
        try {
            led.setValue(value);
        } catch (IOException e) {
            Log.e(TAG, "Error setting led value", e);
        }
    }

    @Override
    protected void onDestroy() {
        destroyLed();
        destroyButton();
        destroyFirebaseDatabase();
        super.onDestroy();
    }

    private void destroyLed() {
        if (valueEventListener != null) {
            databaseReference.removeEventListener(valueEventListener);
            valueEventListener = null;
        }
        if (led != null) {
            Log.i(TAG, "Closing led");
            try {
                led.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing led", e);
            } finally {
                led = null;
            }
        }
    }

    private void destroyButton() {
        if (button != null) {
            Log.i(TAG, "Closing button");
            try {
                button.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing button", e);
            } finally {
                button = null;
            }
        }
    }

    private void destroyFirebaseDatabase() {
        databaseReference = null;
    }
}
