package com.guli.videotool.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.guli.videotool.MyApplication;
import com.guli.videotool.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import VideoHandle.EpEditor;
import VideoHandle.OnEditorListener;

public class MergeActivity extends BaseActivity {

    public static final int sel_code_file1 = 1;
    public static final int sel_code_file2 = 2;
    String file1;
    String file2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actiivty_merge);

    }

    @Override
    protected void onFileSelect(int requestCode, ArrayList<String> path) {
        super.onFileSelect(requestCode,path);
        if (requestCode == sel_code_file1) {
            file1 = path.get(0);
        } else if (requestCode == sel_code_file2) {
            file2 = path.get(0);
        }
    }

    public void onClickSelFile1(View view) {
        selectFile(sel_code_file1);
//        view.setEnabled(false);
    }

    public void onClickSelFile2(View view) {
        selectFile(sel_code_file2);
//        view.setEnabled(false);
    }

    public void onClickStart(View view) {
        view.setEnabled(false);
        if (file1 == null) {
            showInfo("文件1未选择");
            return;
        }
        if (file2 == null) {
            showInfo("文件2未选择");
            return;
        }

        final String outPutVideoPath = getOutPutVideoPathFinish();
        String cmd = getCmd(file1, file2, outPutVideoPath);
//        EpEditor.OutputOption outputOption = getOutputOption(outPutVideoPath);
        showInfo("执行命令:"+cmd);
        EpEditor.execCmd(cmd, 0, new OnEditorListener() {
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

    private String getCmd(String file1, String file2, String outFile) {
        //windows使用的命令:ffmpeg -i 1.mp4 -i 33.mp4 -filter_complex "[0:v]pad=iw*0:ih*0[a];[a][1:v]overlay=0:0" out2.mp4
        String format = String.format("-i %s -i %s -filter_complex [0:v]pad=iw*0:ih*0[a];[a][1:v]overlay=0:0 %s", file1, file2, outFile);
        return format;
    }

    protected String getOutPutVideoPathFinish() {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd-HH-mm-ss");
        String format = simpleDateFormat.format(new Date());
        return MyApplication.VIDEO_DIR_PATH_FINISH_PRODUCT + "/" + format + ".mp4";
    }

}
