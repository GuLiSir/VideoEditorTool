package com.guli.videotool.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.guli.videotool.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




//        finish();

    }


    public void clickClip(View view) {
        startActivity(new Intent(this,ClipActivity.class));
    }

    public void clickCrop(View view) {
        startActivity(new Intent(this,CropActivity.class));
    }

    public void clickMerge(View view) {
        startActivity(new Intent(this,MergeActivity.class));
    }

    public void clickDel(View view) {
        startActivity(new Intent(this,DelActivity.class));
    }
}
