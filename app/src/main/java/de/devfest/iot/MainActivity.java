package de.devfest.iot;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.things.contrib.driver.button.Button;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String GPIO_BUTTON_PIN_NAME = "GPIO_35";
    private static final String TEAM_NUMBER = "01";

    private DatabaseReference databaseReference;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFirebaseDatabase();
        setupButton();
    }

    @Override
    protected void onDestroy() {
        destroyButton();
        destroyFirebaseDatabase();
        super.onDestroy();
    }

    private void setupFirebaseDatabase() {
        databaseReference = FirebaseDatabase.getInstance().getReference("team" + TEAM_NUMBER);
    }

    private void destroyFirebaseDatabase() {
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
            // couldn't configure the button...
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
}
