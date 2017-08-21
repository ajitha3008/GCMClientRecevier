package com.braingalore.gcm_client.Core;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.braingalore.gcm_client.Utils.Constants;
import com.braingalore.gcm_client.R;
import com.google.android.gcm.GCMRegistrar;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Controller class to register/unregister device with php server
 */
public class GCMController {
    private static final String TAG = GCMController.class.getName();
    private final int MAX_ATTEMPTS = 5;
    private final int BACKOFF_MILLI_SECONDS = 2000;
    private final Random random = new Random();
    private static GCMController gcmControllerInstance;

    private GCMController() {
    }

    public static GCMController getInstance() {
        if (gcmControllerInstance != null) {
            return gcmControllerInstance;
        }
        gcmControllerInstance = new GCMController();
        return gcmControllerInstance;
    }

    public void register(final Context context, String name, String email, final String regId) {
        Log.i(TAG, "Registering device (regId = " + regId + ")");
        String serverUrl = Constants.SERVER_URL;
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constants.PARAM_REGID, regId);
        params.put(Constants.PARAM_NAME, name);
        params.put(Constants.PARAM_EMAIL, email);
        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);

        // Once GCM returns a registration id, we need to register on our server
        // As the server might be down, we will retry it a couple
        // times.
        for (int i = 1; i <= MAX_ATTEMPTS; i++) {

            Log.d(TAG, "Attempt #" + i + " to register");

            try {
                //Send Broadcast to Show message on screen
                displayMessageOnScreen(context, context.getString(
                        R.string.server_registering, i, MAX_ATTEMPTS));

                // Post registration values to web server
                HttpRequestSender.post(serverUrl, params);

                GCMRegistrar.setRegisteredOnServer(context, true);

                //Send Broadcast to Show message on screen
                String message = context.getString(R.string.server_registered);
                displayMessageOnScreen(context, message);

                return;
            } catch (IOException e) {

                // Here we are simplifying and retrying on any error; in a real
                // application, it should retry only on unrecoverable errors
                // (like HTTP error code 503).

                Log.e(TAG, "Failed to register on attempt " + i + ":" + e);

                if (i == MAX_ATTEMPTS) {
                    break;
                }
                try {

                    Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
                    Thread.sleep(backoff);

                } catch (InterruptedException e1) {
                    // Activity finished before we complete - exit.
                    Log.d(TAG, "Thread interrupted: abort remaining retries!");
                    Thread.currentThread().interrupt();
                    return;
                }

                // increase backoff exponentially
                backoff *= 2;
            }
        }

        String message = context.getString(R.string.server_register_error,
                MAX_ATTEMPTS);

        //Send Broadcast to Show message on screen
        displayMessageOnScreen(context, message);
    }

    // Unregister this account/device pair within the server.
    public void unregister(final Context context, final String regId) {

        Log.i(TAG, "unregistering device (regId = " + regId + ")");

        String serverUrl = Constants.SERVER_URL + "/unregister";
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constants.PARAM_REGID, regId);

        try {
            HttpRequestSender.post(serverUrl, params);
            GCMRegistrar.setRegisteredOnServer(context, false);
            String message = context.getString(R.string.server_unregistered);
            displayMessageOnScreen(context, message);
        } catch (IOException e) {
            // At this point the device is unregistered from GCM, but still
            // registered in the our server.
            // We could try to unregister again, but it is not necessary:
            // if the server tries to send a message to the device, it will get
            // a "NotRegistered" error message and should unregister the device.

            String message = context.getString(R.string.server_unregister_error,
                    e.getMessage());
            displayMessageOnScreen(context, message);
        }
    }

    // Notifies UI to display a message.
    public void displayMessageOnScreen(Context context, String message) {
        Intent intent = new Intent(Constants.DISPLAY_MESSAGE_ACTION);
        intent.putExtra(Constants.INTENT_EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
}
