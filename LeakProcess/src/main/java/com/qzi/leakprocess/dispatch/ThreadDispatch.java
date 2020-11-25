package com.qzi.leakprocess.dispatch;

import android.os.Handler;
import android.os.Looper;

/**
 * 线程调度器
 */
public class ThreadDispatch {

    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    public static Handler getMainHandler() {
        return mainHandler;
    }
}
