package com.guli.videotool;

import android.app.Application;

public class MyApplication extends Application {
    private static MyApplication instance;
    public static final String VIDEO_DIR_PATH = "sdcard/3hVideo";
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static MyApplication getInstance() {
        return instance;
    }
}
