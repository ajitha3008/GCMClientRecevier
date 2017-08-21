package com.braingalore.gcm_client;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.braingalore.gcm_client.Utils.Constants;
import com.braingalore.gcm_client.Core.GCMController;
import com.braingalore.gcm_client.views.MainActivity;
import com.braingalore.gcm_client.R;
import com.braingalore.gcm_client.Utils.NotificationUtils;
import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {

    private static final String TAG = "GCMIntentService";

    public GCMIntentService() {
        super(Constants.GOOGLE_SENDER_ID);
    }

    /**
     * Method called on device registered
     **/
    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "Device registered: regId = " + registrationId);
        GCMController.getInstance().displayMessageOnScreen(context, "Your device registred with GCM");
        Log.d("NAME", MainActivity.name);
        GCMController.getInstance().register(context, MainActivity.name, MainActivity.email, registrationId);
    }

    /**
     * Method called on device unregistred
     */
    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");
        GCMController.getInstance().displayMessageOnScreen(context, getString(R.string.gcm_unregistered));
        GCMController.getInstance().unregister(context, registrationId);
    }

    /**
     * Method called on Receiving a new message from GCM server
     */
    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.i(TAG, "Received message");
        String message = intent.getExtras().getString("price");
        GCMController.getInstance().displayMessageOnScreen(context, message);
        NotificationUtils.generateNotification(context, message);
    }

    /**
     * Method called on receiving a deleted message
     */
    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        GCMController.getInstance().displayMessageOnScreen(context, message);
        // notifies user
        NotificationUtils.generateNotification(context, message);
    }

    /**
     * Method called on Error
     */
    @Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "Received error: " + errorId);
        GCMController.getInstance().displayMessageOnScreen(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        Log.i(TAG, "Received recoverable error: " + errorId);
        GCMController.getInstance().displayMessageOnScreen(context, getString(R.string.gcm_recoverable_error,
                errorId));
        return super.onRecoverableError(context, errorId);
    }
}
