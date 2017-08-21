package com.braingalore.gcm_client.Core;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.braingalore.gcm_client.BuildConfig;
import com.braingalore.gcm_client.Utils.NotificationUtils;

/**
 * Application class
 */
public class GCMClientApplication extends Application implements Application.ActivityLifecycleCallbacks {
    private static final String TAG = GCMClientApplication.class.getName();
    private Activity mCurrentActivity = null;

    @Override
    public void onCreate() {
        super.onCreate();
        NotificationUtils.initialize(this);
        /*Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable e) {
                handleUncaughtException(thread, e);
            }
        });*/
    }

    public void handleUncaughtException(Thread thread, Throwable e) {
        // kill off the crashed app
        System.exit(1);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        if (BuildConfig.REPORT_DEBUG_LOGS) {
            Log.d(TAG, "onActivityCreated(): Task ID: "
                    + activity.getTaskId()
                    + ": "
                    + activity.getClass().getSimpleName()
                    + ", currentActivity: "
                    + (mCurrentActivity == null ? " none" :
                    mCurrentActivity.getClass().getSimpleName()));
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (BuildConfig.REPORT_DEBUG_LOGS) {
            Log.d(TAG, "onActivityStarted(): Task ID: "
                    + activity.getTaskId()
                    + ": "
                    + activity.getClass().getSimpleName()
                    + ", currentActivity: "
                    + (mCurrentActivity == null ? " none" :
                    mCurrentActivity.getClass().getSimpleName()));
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
        mCurrentActivity = activity;
        if (BuildConfig.REPORT_DEBUG_LOGS) {
            Log.d(TAG, "onActivityResumed(): Task ID: "
                    + activity.getTaskId()
                    + ": "
                    + activity.getClass().getSimpleName()
                    + ", currentActivity: "
                    + (mCurrentActivity == null ? " none" : mCurrentActivity.getClass().getSimpleName()));
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (activity == mCurrentActivity) {
            // if it is, then that means, no activities are in foreground anymore
            mCurrentActivity = null;
        }
        if (BuildConfig.REPORT_DEBUG_LOGS) {
            Log.d(TAG, ("onActivityPaused(): Task ID: "
                    + activity.getTaskId()
                    + ": "
                    + activity.getClass().getSimpleName()
                    + ", currentActivity: "
                    + (mCurrentActivity == null ? " none" : mCurrentActivity.getClass().getSimpleName())));
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (BuildConfig.REPORT_DEBUG_LOGS) {
            Log.d(TAG, ("onActivityStopped(): Task ID: "
                    + activity.getTaskId() + ": "
                    + activity.getClass().getSimpleName()
                    + ", currentActivity: "
                    + (mCurrentActivity == null ? " none" : mCurrentActivity.getClass().getSimpleName())));
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        if (BuildConfig.REPORT_DEBUG_LOGS) {
            Log.d(TAG, "onActivitySaveInstanceState(): Task ID: "
                    + activity.getTaskId()
                    + ": "
                    + activity.getClass().getSimpleName()
                    + ", currentActivity: "
                    + (mCurrentActivity == null ? " none" : mCurrentActivity.getClass().getSimpleName()));
        }
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if (BuildConfig.REPORT_DEBUG_LOGS) {
            Log.d(TAG, "onActivityDestroyed(): Task ID: "
                    + activity.getTaskId()
                    + ": "
                    + activity.getClass().getSimpleName()
                    + ", currentActivity: "
                    + (mCurrentActivity == null ? " none" : mCurrentActivity.getClass().getSimpleName()));
        }
    }
}
