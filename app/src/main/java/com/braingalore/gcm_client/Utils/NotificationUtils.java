package com.braingalore.gcm_client.Utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.braingalore.gcm_client.views.MainActivity;
import com.braingalore.gcm_client.R;

/**
 * Utility class to display the notifications
 */
public class NotificationUtils {

    public static final String CHANNEL_ID = "GCM_NOTIFICATION_CHANNEL";

    private static final String TAG = NotificationUtils.class.getName();

    /**
     * Function triggered when the GCM push notification is received.
     *
     * @param context
     * @param message
     */
    public static void generateNotification(Context context, String message) {
        int icon = R.drawable.ic_launcher;
        long when = System.currentTimeMillis();
        String title = context.getString(R.string.app_name);
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(icon)
                .setContentTitle(title);
        builder.setContentIntent(intent);
        builder.setContentText(message);
        builder.setChannelId(CHANNEL_ID);
        builder.setWhen(when);
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(0, builder.build());
    }

    /**
     * Initialises the notification channel required for Android O
     * @param context
     */
    public static void initialize(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    context.getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(channel);
        }
    }
}
