package com.guli.videotool.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;

import com.guli.videotool.R;

import java.io.File;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Locale;

import VideoHandle.EpEditor;
import VideoHandle.EpVideo;
import VideoHandle.OnEditorListener;

//视频时长裁剪
public class ClipActivity extends BaseActivity {

    private String path1;
    private String path2;
    private String outPutVideoPath1;
    private String outPutVideoPath2;
    EditText etStart;
    EditText etLength;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clip);
        etStart = findViewById(R.id.clip_et_start);
        etLength = findViewById(R.id.clip_et_length);

        showInfo("时长剪辑功能,会自动将两个视频设置为同一个宽度,宽度比较大视频宽度的会改变为宽度小的视频同宽");

    }

    public void onClickSelFile(View view) {
        selectFile(2, 2);//可以选择两个视频文件
    }

    @Override
    protected void onFileSelect(int requestCode, ArrayList<String> path) {
        super.onFileSelect(requestCode, path);
        if (path.size() != 2) {
            showInfo("请选择两个视频文件");
            return;
        }
        this.path1 = path.get(0);
        this.path2 = path.get(1);

    }

    int count = 0;

    public void onClickStart(View view) {
        if (path1 == null) {
            showInfo("没有选择文件");
            return;
        }

        clipOneFile(path1, 1);//开始剪切第一个,在第一个完成的时候就剪切第二个

    }

    /**
     * 第一个视频还是第二个视频
     * 开始剪切第一个,在第一个完成的时候就剪切第二个
     */
    private void clipOneFile(String path, final int index) {
        String s = etStart.getText().toString();
        int integer1 = Integer.parseInt(s);

        String s2 = etLength.getText().toString();
        int integer2 = Integer.parseInt(s2);

        EpVideo epVideo = new EpVideo(path);

        //一个参数为剪辑的起始时间，第二个参数为持续时间,单位：秒
        epVideo.clip(integer1, integer2);//从第一秒开始，剪辑两秒


        //输出选项，参数为输出文件路径(目前仅支持mp4格式输出)
        final String outPutVideoPath = getOutPutVideoPath();
        if (index == 1) {
            outPutVideoPath1 = outPutVideoPath;
        } else if (index == 2) {
            outPutVideoPath2 = outPutVideoPath;
        }
        EpEditor.OutputOption outputOption = getOutputOption(outPutVideoPath);
        EpEditor.exec(epVideo, outputOption, new OnEditorListener() {
            @Override
            public void onSuccess() {
                showInfo("输出视频目录是:" + outPutVideoPath);
                showInfo("成功!!!");
                scanFileToSystem(outPutVideoPath);
                count++;
                if (count == 2) {
                    //两个视频都时间剪切完了开始进行视频缩放动作
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            scale();//开始进行视频缩放
                        }
                    });
                }

                if (index == 1) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            clipOneFile(path2, 2);
                        }
                    });
                }

            }

            @Override
            public void onFailure() {
                showInfo("失败 0.0   " + index);
            }

            @Override
            public void onProgress(float progress) {
                //这里获取处理进度
                showInfo("进度" + index + "   :" + progress);
            }
        });
    }

    //剪切第二个
    private void scale() {
        int videoW1 = getVideoW(outPutVideoPath1);
        int videoW2 = getVideoW(outPutVideoPath2);
        final String scalePath;
        final int scaleForm;
        final int scaleTo;
        if (videoW1 == videoW2) {
            showInfo("视频宽度一致,无需修改");
            return;
        } else if (videoW1 < videoW2) {
            //视频一比较小,缩放视频2
            scalePath = outPutVideoPath2;
            scaleForm = videoW2;
            scaleTo = videoW1;
        } else {
            scalePath = outPutVideoPath1;
            scaleForm = videoW1;
            scaleTo = videoW2;
        }


      /*命令
       将输入的1920x1080缩小到960x540输出:
        ./ffmpeg -i input.mp4 -vf scale=960:540 output.mp4
        //ps: 如果540不写，写成-1，即scale=960:-1, 那也是可以的，ffmpeg会通知缩放滤镜在输出时保持原始的宽高比。

        作者：FlyingPenguin
        链接：https://www.jianshu.com/p/3fa0b04027ce
        来源：简书
        著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。*/


        int videoW = getVideoW(scalePath);
        int videoH = getVideoH(scalePath);
        //计算同比例宽高
        float v = (float) videoW / (float) videoH;//宽高比
        int scaleToH = (int) (scaleTo / v);
        if (scaleToH % 2 == 1) {
            //高不能为奇数
            scaleToH--;
        }

        final String outPutVideoPath = getOutPutVideoPath();
        EpEditor.OutputOption outputOption = getOutputOption(outPutVideoPath);
        String format = String.format(Locale.CHINA, "-i %s -vf scale=%d:%d %s", scalePath, scaleTo, scaleToH, outPutVideoPath);
        showInfo("执行命令:" + format);
        EpEditor.execCmd(format, 0, new OnEditorListener() {
            @Override
            public void onSuccess() {
                showInfo("输出视频目录是:" + outPutVideoPath);
                showInfo("缩放设置成功!!!");
                scanFileToSystem(outPutVideoPath);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        new File(scalePath).delete();//删除视频宽一点的视频
                    }
                });
            }

            @Override
            public void onFailure() {
                showInfo("缩放失败 0.0");
            }

            @Override
            public void onProgress(float progress) {
                //这里获取处理进度
                showInfo("缩放进度:" + progress);
            }
        });

    }

}
