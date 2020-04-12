package com.guli.videotool.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.VideoView;

import com.guli.videotool.R;
import com.guli.videotool.view.MView;


import java.util.ArrayList;

import VideoHandle.EpEditor;
import VideoHandle.EpVideo;
import VideoHandle.OnEditorListener;

//视频画面裁剪
public class CropActivity extends BaseActivity {

    EditText etH;
    EditText etW;
    EditText etX;
    EditText etY;

    VideoView videoView;
    MView mView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        etH = findViewById(R.id.crop_et_h);
        etW = findViewById(R.id.crop_et_w);
        etX = findViewById(R.id.crop_et_start_x);
        etY = findViewById(R.id.crop_et_start_y);

        videoView = findViewById(R.id.crop_vv);
        mView = findViewById(R.id.crop_mv);

    }


    @Override
    protected void onFileSelect(int requestCode, ArrayList<String> path) {
        super.onFileSelect(requestCode, path);

        this.path = path.get(0);
        etW.setText(getVideoW(this.path) + "");
        videoView.setVideoPath(this.path);
    }

    private String path;

    public void onClickSelFile(View view) {
        selectFile(2);
    }

    public void onClickStart(View view) {
        if (path == null) {
            showInfo("没有选择文件");
            return;
        }

        String s = etH.getText().toString();
        int integer1 = Integer.parseInt(s);

        String s2 = etW.getText().toString();
        int integer2 = Integer.parseInt(s2);
        int x = Integer.parseInt(etX.getText().toString());
        int y = Integer.parseInt(etY.getText().toString());

        EpVideo epVideo = new EpVideo(path);


        //参数分别是裁剪的宽，高，起始位置X，起始位置Y
        epVideo.crop(integer2, integer1, x, y);


        //输出选项，参数为输出文件路径(目前仅支持mp4格式输出)
        final String outPutVideoPath = getOutPutVideoPath();
        EpEditor.OutputOption outputOption = getOutputOption(outPutVideoPath);
        EpEditor.exec(epVideo, outputOption, new OnEditorListener() {
            @Override
            public void onSuccess() {
                showInfo("输出视频目录是:" + outPutVideoPath);
                showInfo("成功!!!");
                scanFileToSystem(outPutVideoPath);
            }

            @Override
            public void onFailure() {
                showInfo("失败 0.0");
            }

            @Override
            public void onProgress(float progress) {
                //这里获取处理进度
                showInfo("进度:" + progress);
            }
        });


    }


    public void onClickPreview(View view) {
        if (this.path == null) {
            showInfo("请选择视频文件");
            return;
        }

        String s = etH.getText().toString();
        int integer1 = 0;
        try {
            integer1 = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            showInfo("请输入正确参数1");
            return;
        }

        String s2 = etW.getText().toString();
        int integer2 = 0;
        try {
            integer2 = Integer.parseInt(s2);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            showInfo("请输入正确参数2");
            return;
        }


        int x = 0;
        try {
            x = Integer.parseInt(etX.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            showInfo("请输入正确参数3");
            return;
        }

        int y = 0;
        try {
            y = Integer.parseInt(etY.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            showInfo("请输入正确参数4");
            return;
        }

        videoView.start();

        mView.preview(videoView.getMeasuredWidth(), videoView.getMeasuredHeight(), getVideoW(path), getVideoH(path),
                integer2, integer1, x, y
        );
    }
}
