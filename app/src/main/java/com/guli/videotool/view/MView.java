package com.guli.videotool.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.guli.videotool.R;

public class MView extends View {
    public MView(Context context) {
        super(context);
    }

    public MView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    //中间截取区域框
    private RectF rectFCenter = new RectF();
    //外蒙版框
    private RectF rectF = new RectF();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.clipOutRect(  )
//        paintRange.
        paintRange.setXfermode(null);
        canvas.drawRect(rectF, paintRange);
        paintRange.setXfermode(porterDuffXfermode);
        canvas.drawRect(rectFCenter, paintRange);
        Log.i(TAG, "onDraw: ");

    }

    PorterDuffXfermode porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);

    private static final String TAG = "MView";
    private int videoH = 0;
    private int videoW = 0;
    //蒙版框
    private Paint paintRange = new Paint();


    {
        paintRange.setColor(getResources().getColor(R.color.colorMeng));
//        Xfermode xfermode = new Xfermode();


    }

    public void setVideoH(int videoH) {
        this.videoH = videoH;
    }

    public void setVideoW(int videoW) {
        this.videoW = videoW;
    }


    /**
     * @param width  预览控件宽
     * @param height 预览控件高
     * @param videoW 视频原宽
     * @param videoH 视频原高
     * @param clipW  裁剪出来的视频宽
     * @param clipH  裁剪出来的视频高
     * @param clipX  起始点x
     * @param clipY  起始点y
     */
    public void preview(int width, int height, int videoW, int videoH,
                        int clipW, int clipH, int clipX, int clipY) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        rectF.right = layoutParams.width;
        rectF.bottom = layoutParams.height;
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        //计算中间框,逻辑部分

        float scaleX = width / (float) videoW;//预览缩放比例
        float scaleY = height / (float) videoH;//预览缩放比例
        Log.i(TAG, String.format("preview1: width:%d,height:%d,videoW:%d,videoH:%d,clipW:%d,clipH:%d,clipX:%d,clipY:%d  ",
                width, height, videoW, videoH,
                clipW, clipH, clipX, clipY)
                );

        rectFCenter.left = clipX * scaleX;
        rectFCenter.top = clipY * scaleY;
        rectFCenter.right = rectFCenter.left + clipW * scaleX;
        rectFCenter.bottom = rectFCenter.top + clipH * scaleY;

        setLayoutParams(layoutParams);
        invalidate();
    }

}
