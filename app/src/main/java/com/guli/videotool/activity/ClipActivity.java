package com.guli.videotool.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;

import com.guli.videotool.R;

import VideoHandle.EpEditor;
import VideoHandle.EpVideo;
import VideoHandle.OnEditorListener;

public class ClipActivity extends BaseActivity {

    private String path;
    EditText etStart;
    EditText etLength;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clip);
        etStart = findViewById(R.id.clip_et_start);
        etLength = findViewById(R.id.clip_et_length);


    }

    public void onClickSelFile(View view) {
        selectFile(2);
    }

    @Override
    protected void onFileSelect(int requestCode, String path) {
        this.path = path;
    }

    public void onClickStart(View view) {
        if (path == null) {
            showInfo("没有选择文件");
            return;
        }

        String s = etStart.getText().toString();
        Integer integer1 = Integer.valueOf(s);

        String s2 = etLength.getText().toString();
        Integer integer2 = Integer.valueOf(s2);

        EpVideo epVideo = new EpVideo(path);

        //一个参数为剪辑的起始时间，第二个参数为持续时间,单位：秒
        epVideo.clip(integer1, integer2);//从第一秒开始，剪辑两秒


        //输出选项，参数为输出文件路径(目前仅支持mp4格式输出)
        final String outPutVideoPath = getOutPutVideoPath();
        EpEditor.OutputOption outputOption = new EpEditor.OutputOption(outPutVideoPath);
        outputOption.setWidth(480);  //输出视频宽，如果不设置则为原始视频宽高
        outputOption.setHeight(360); //输出视频高度
        outputOption.frameRate = 30;//输出视频帧率,默认30
        outputOption.bitRate = 10;//输出视频码率,默认10
        EpEditor.exec(epVideo, outputOption, new OnEditorListener() {
            @Override
            public void onSuccess() {
                showInfo("输出视频目录是:"+outPutVideoPath);
                showInfo("成功!!!");
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
}
