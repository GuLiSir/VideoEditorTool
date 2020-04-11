package com.guli.videotool.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.guli.videotool.R;


public class CropActivity extends BaseActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        selectFile(1);

    }


    @Override
    protected void onFileSelect(int requestCode, String path) {

    }
}
