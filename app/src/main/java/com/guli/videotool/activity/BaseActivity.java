package com.guli.videotool.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.guli.videotool.MyApplication;
import com.guli.videotool.R;
import com.guli.videotool.util.GlideLoader;
import com.lcw.library.imagepicker.ImagePicker;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public abstract class BaseActivity extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        verifyStoragePermissions(this);
        File file = new File(MyApplication.VIDEO_DIR_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static void verifyStoragePermissions(Activity activity) {

        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //在onCreate()方法中调用该方法即可


    public int requestSelectCodeTmp = 1;
    private ArrayList<String> mImagePaths;


    protected void selectFile(int selectRequestCode) {
        requestSelectCodeTmp = selectRequestCode;
        ImagePicker.getInstance()
                .setTitle("标题")//设置标题
                .showCamera(false)//设置是否显示拍照按钮
                .showImage(false)//设置是否展示图片
                .showVideo(true)//设置是否展示视频
//                .filterGif(false)//设置是否过滤gif图片
                .setSingleType(true)//设置图片视频不能同时选择
                .setMaxCount(1)//设置最大选择图片数目(默认为1，单选)
//                .setImagePaths(mImageList)//保存上一次选择图片的状态，如果不需要可以忽略
                .setImageLoader(new GlideLoader())//设置自定义图片加载器
                .start(this, requestSelectCodeTmp);//REQEST_SELECT_IMAGES_CODE为Intent调用的requestCode

    }

    private static final String TAG = "BaseActivity";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == requestSelectCodeTmp && resultCode == RESULT_OK) {
            mImagePaths = data.getStringArrayListExtra(ImagePicker.EXTRA_SELECT_IMAGES);
            StringBuilder stringBuffer = new StringBuilder();
            stringBuffer.append("当前选中图片路径：\n\n");
            for (int i = 0; i < mImagePaths.size(); i++) {
                stringBuffer.append(mImagePaths.get(i)).append("\n\n");
            }
            showInfo("选择了文件:" + mImagePaths.get(0));
            onFileSelect(requestSelectCodeTmp, mImagePaths.get(0));
        }
    }

    protected abstract void onFileSelect(int requestCode, String path);

    TextView tvInfo;
    boolean findInfoId = false;//避免重复查找控件

    int infoCount = 1;
    Handler handler = new Handler();

    protected void showInfo(final String info) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                showInfo2(info);
            }
        });
    }

    protected void showInfo2(String info) {
        Log.i(TAG, "showInfo: " + info);

        if (tvInfo == null) {
            if (findInfoId) {
                return;//不重复查找控件
            }
            tvInfo = findViewById(R.id.tv_info);
            findInfoId = true;
        }
        if (tvInfo != null) {
            CharSequence text = tvInfo.getText();
            String format = String.format("%s\n%d.%s", text, infoCount, info);
            tvInfo.setText(format);
            infoCount++;
        }

    }

    protected String getOutPutVideoPath() {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd-HH-mm-ss");
        String format = simpleDateFormat.format(new Date());
        return MyApplication.VIDEO_DIR_PATH + "/" + format+".mp4";
    }

}
