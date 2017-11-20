package de.devfest.iot;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.things.contrib.driver.button.Button;

import java.io.IOException;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String gpioButtonPinName = "GPIO_35";
    private Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupButton();
    }

    @Override
    protected void onDestroy() {
        destroyButton();
        super.onDestroy();
    }

    private void setupButton() {
        try {
            button = new Button(gpioButtonPinName,
                    // high signal indicates the button is pressed
                    // use with a pull-down resistor
                    Button.LogicState.PRESSED_WHEN_HIGH
            );
            button.setOnButtonEventListener(new Button.OnButtonEventListener() {
                @Override
                public void onButtonEvent(Button button, boolean pressed) {
                    // do something awesome
                    Log.d(TAG, "onButtonEvent: pressed");
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
