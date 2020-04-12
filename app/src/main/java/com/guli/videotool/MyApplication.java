package com.guli.videotool;

import android.app.Application;
import android.os.Environment;

public class MyApplication extends Application {
    private static MyApplication instance;

    public static final String BASE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM";
    public static final String VIDEO_DIR_PATH = BASE_PATH + "/video3h";
    public static final String VIDEO_DIR_PATH_FINISH_PRODUCT = BASE_PATH + "/video3hFinish";
    public static int VIDEO_HEIGHT = 1080;
    public static int VIDEO_WIDTH = 1920;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static MyApplication getInstance() {
        return instance;
    }
}
