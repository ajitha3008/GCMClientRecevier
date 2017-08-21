package com.braingalore.gcm_client.views;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.braingalore.gcm_client.Core.GCMController;
import com.braingalore.gcm_client.R;
import com.braingalore.gcm_client.Utils.Constants;
import com.braingalore.gcm_client.Utils.DialogUtils;
import com.braingalore.gcm_client.Utils.NetworkUtils;
import com.braingalore.gcm_client.Utils.PowerManagerUtils;
import com.google.android.gcm.GCMRegistrar;

public class MainActivity extends Activity {
    // label to display gcm messages
    TextView incoming_gcm_data;
    private static final String TAG = MainActivity.class.getName();

    // Asyntask
    AsyncTask<Void, Void, Void> mRegisterTask;

    public static String name;
    public static String email;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!NetworkUtils.isNetworkAvailable(this)) {
            DialogUtils.showAlertDialog(MainActivity.this,
                    "Internet Connection Error",
                    "Please connect to Internet connection");
            return;
        }

        // Getting name, email from intent
        Intent i = getIntent();
        name = i.getStringExtra(Constants.PARAM_NAME);
        email = i.getStringExtra(Constants.PARAM_EMAIL);

        // Make sure the device has the proper dependencies.
        GCMRegistrar.checkDevice(this);

        // Make sure the manifest permissions was properly set
        GCMRegistrar.checkManifest(this);

        incoming_gcm_data = findViewById(R.id.incoming_gcm_data);

        // Register custom Broadcast receiver to show messages on activity
        registerReceiver(mHandleMessageReceiver, new IntentFilter(
                Constants.DISPLAY_MESSAGE_ACTION));

        // Get GCM registration id
        final String regId = GCMRegistrar.getRegistrationId(this);

        // Check if regid already presents
        if (TextUtils.isEmpty(regId)) {

            // Register with GCM
            GCMRegistrar.register(this, Constants.GOOGLE_SENDER_ID);

        } else {

            // Device is already registered on GCM Server
            if (GCMRegistrar.isRegisteredOnServer(this)) {

                // Skips registration.
                Toast.makeText(getApplicationContext(), "Already registered with GCM Server", Toast.LENGTH_LONG).show();

            } else {

                // Try to register again, but not in the UI thread.
                // It's also necessary to cancel the thread onDestroy(),
                // hence the use of AsyncTask instead of a raw thread.

                final Context context = this;
                mRegisterTask = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {

                        // Register on our server
                        // On server creates a new user
                        GCMController.getInstance().register(context, name, email, regId);

                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        mRegisterTask = null;
                    }

                };

                // execute AsyncTask
                mRegisterTask.execute(null, null, null);
            }
        }
    }

    // Create a broadcast receiver to get message and show on screen
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String newMessage = intent.getExtras().getString(Constants.INTENT_EXTRA_MESSAGE);

            // Waking up mobile if it is sleeping
            PowerManagerUtils.acquireWakeLock(getApplicationContext());

            // Display message on the screen
            incoming_gcm_data.append(newMessage + "\n");

            Toast.makeText(getApplicationContext(), "Got Message: " + newMessage, Toast.LENGTH_LONG).show();

            // Releasing wake lock
            PowerManagerUtils.releaseWakeLock();
        }
    };

    @Override
    protected void onDestroy() {
        // Cancel AsyncTask
        if (mRegisterTask != null) {
            mRegisterTask.cancel(true);
        }
        try {
            // Unregister Broadcast Receiver
            unregisterReceiver(mHandleMessageReceiver);

            //Clear internal resources.
            GCMRegistrar.onDestroy(this);

        } catch (Exception e) {
            Log.e(TAG, "UnRegister Receiver Error> " + e.getMessage());
        }
        super.onDestroy();
    }

}
