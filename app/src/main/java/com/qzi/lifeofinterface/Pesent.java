package com.qzi.lifeofinterface;

import android.util.Log;

public class Pesent {

    public void start(final Callback callback){
        new Thread(){
            @Override
            public void run() {
                while (true){
                    try {
                        Log.e("Thread", "111111");
                        callback.onBegin();
                        Thread.sleep(2000);
                        callback.onEnd();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
}
