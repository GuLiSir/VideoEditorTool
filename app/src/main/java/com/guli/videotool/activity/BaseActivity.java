package com.guli.videotool.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.TextView;

import com.guli.videotool.MyApplication;
import com.guli.videotool.R;
import com.guli.videotool.util.GlideLoader;
import com.lcw.library.imagepicker.ImagePicker;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import VideoHandle.EpEditor;

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
        File file1 = new File(MyApplication.VIDEO_DIR_PATH_FINISH_PRODUCT);
        if (!file1.exists()) {
            file1.mkdirs();
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
        selectFile(selectRequestCode, 1);
    }

    protected void selectFile(int selectRequestCode, int count) {


        requestSelectCodeTmp = selectRequestCode;
        ImagePicker.getInstance()
                .setTitle("选择视频")//设置标题
                .showCamera(false)//设置是否显示拍照按钮
                .showImage(false)//设置是否展示图片
                .showVideo(true)//设置是否展示视频
//                .filterGif(false)//设置是否过滤gif图片
                .setSingleType(true)//设置图片视频不能同时选择
                .setMaxCount(count)//设置最大选择图片数目(默认为1，单选)
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
            onFileSelect(requestSelectCodeTmp, mImagePaths);
        }
    }

    protected void onFileSelect(int requestCode, ArrayList<String> paths) {
        for (String s : paths) {
            showVideoInfo(s);
        }
    }

    TextView tvInfo;
    ScrollView scrollView;
    boolean findInfoId = false;//避免重复查找控件

    int infoCount = 1;
    protected Handler handler = new Handler();

    protected void showInfo(final String info) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                showInfo2(info);
            }
        });
    }

    private void showInfo2(String info) {
        Log.i(TAG, "showInfo: " + info);

        if (tvInfo == null) {
            if (findInfoId) {
                return;//不重复查找控件
            }
            tvInfo = findViewById(R.id.tv_info);
            scrollView = findViewById(R.id.scrollView);
            findInfoId = true;
        }
        if (tvInfo != null) {
            CharSequence text = tvInfo.getText();
            String format = String.format("%s\n%d.%s", text, infoCount, info);
            tvInfo.setText(format);
            handler.post(new Runnable() {
                @Override
                public void run() {
//            scrollView.fullScroll(ScrollView.FOCUS_UP);//滚动到顶部
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);//滚动到底部

                }
            });
            infoCount++;
        }

    }

    protected String getOutPutVideoPath() {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd-HH-mm-ss");
        String format = simpleDateFormat.format(new Date());
        return MyApplication.VIDEO_DIR_PATH + "/" + format + ".mp4";
    }


    protected EpEditor.OutputOption getOutputOption(String outPutVideoPath) {
        EpEditor.OutputOption outputOption = new EpEditor.OutputOption(outPutVideoPath);
//        outputOption.setWidth(MyApplication.VIDEO_WIDTH);  //输出视频宽，如果不设置则为原始视频宽高
//        outputOption.setHeight(MyApplication.VIDEO_HEIGHT); //输出视频高度
        outputOption.frameRate = 30;//输出视频帧率,默认30
        outputOption.bitRate = 10;//输出视频码率,默认10
        return outputOption;
    }


    protected void scanFileToSystem(String outPutVideoPath) {
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + outPutVideoPath)));
    }


    private void showVideoInfo(String path) {
        android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();
        try {
            if (path != null) {
                Uri uri = Uri.fromFile(new File(path));
                mmr.setDataSource(this, uri);
            } else {
                //mmr.setDataSource(mFD, mOffset, mLength);
            }

            String duration = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);//时长(毫秒)
            String width = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);//宽
            String height = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);//高

            Integer integer = Integer.valueOf(duration);
            showInfo(String.format(Locale.CHINA, "所选取的视频时长:%d秒,视频宽:%s,高%s", (integer / 1000), width, height));
//            Toast.makeText(this, "playtime:" + duration + "w=" + width + "h=" + height, Toast.LENGTH_SHORT).show();

        } catch (Exception ex) {
            Log.e("TAG", "MediaMetadataRetriever exception " + ex);
        } finally {
            mmr.release();
        }

    }


    protected int getVideoW(String path) {
        android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();
        try {
            if (path != null) {
                Uri uri = Uri.fromFile(new File(path));
                mmr.setDataSource(this, uri);
            }
            String width = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);//宽
            return Integer.valueOf(width);
        } catch (Exception ex) {
            Log.e("TAG", "MediaMetadataRetriever exception " + ex);
        } finally {
            mmr.release();
        }
        return 0;

    }
    protected int getVideoH(String path) {
        android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();
        try {
            if (path != null) {
                Uri uri = Uri.fromFile(new File(path));
                mmr.setDataSource(this, uri);
            }
            String height = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);//高
            return Integer.valueOf(height);
        } catch (Exception ex) {
            Log.e("TAG", "MediaMetadataRetriever exception " + ex);
        } finally {
            mmr.release();
        }
        return 0;

    }

}
