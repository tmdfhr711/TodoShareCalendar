package com.example.todoshare.todosharecalendar.utils;

import android.content.Context;
import android.os.PowerManager;

/**
 * Created by OHRok on 2018-02-06.
 */

class WakeLockUtil {
    static PowerManager.WakeLock mCpuWakeLock;
    Context context;

    public WakeLockUtil() {

    }

    public WakeLockUtil(Context context) {
        this.context = context;
    }

    static void acquireCpuWakeLock(Context context) {
        if (mCpuWakeLock != null) {
            return;
        }
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mCpuWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "알람");
        mCpuWakeLock.acquire();
    }

    static void releaseCpuWakeLock() {
        if (mCpuWakeLock != null) {
            mCpuWakeLock.release();
            mCpuWakeLock = null;
        }
    }
}
