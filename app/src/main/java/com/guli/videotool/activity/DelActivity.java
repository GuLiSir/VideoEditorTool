package com.guli.videotool.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.guli.videotool.MyApplication;
import com.guli.videotool.R;

import java.io.File;
import java.util.ArrayList;

public class DelActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_del);
    }

    public void StartDel(View view) {
        selectFile(1);
    }

    @Override
    protected void onFileSelect(int requestCode, ArrayList<String> paths) {
        super.onFileSelect(requestCode, paths);
        String path = paths.get(0);
        showInfo("123:" + path);
        if (path.contains(MyApplication.VIDEO_DIR_PATH) || path.contains(MyApplication.VIDEO_DIR_PATH_FINISH_PRODUCT)) {
            boolean delete = new File(path).delete();
            if (delete) {
                showInfo("删除文件成功:" + path);
            }else {
                showInfo("删除文件失败,请使用系统删除功能:" + path);
            }
        } else {
            showInfo("不是本工作目录下的文件,不可删除:" + path);
        }
    }
}
