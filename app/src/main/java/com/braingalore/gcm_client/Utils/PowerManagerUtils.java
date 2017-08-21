package com.braingalore.gcm_client.Utils;

import android.content.Context;
import android.os.PowerManager;

/**
 * Class to handle the power manager data
 */
public class PowerManagerUtils {

    private static final String TAG = PowerManagerUtils.class.getName();

    private static PowerManager.WakeLock wakeLock;

    /**
     * Acquires the wakelock
     *
     * @param context
     */
    public static void acquireWakeLock(Context context) {
        if (wakeLock != null) wakeLock.release();
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.ON_AFTER_RELEASE, "BrainGaloreWakelock");
        wakeLock.acquire();
    }

    /**
     * Releases the acquired wakelock if any
     */
    public static void releaseWakeLock() {
        if (wakeLock != null) wakeLock.release();
        wakeLock = null;
    }
}
