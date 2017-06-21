package com.lihao.mobilplay.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.VideoView;


/**
 * Created by hbm on 2017/3/30.
 */

public class MyVideoView extends VideoView {
//    在代码中调用
    public MyVideoView(Context context) {
        this(context,null);
    }
//在布局文件中调用
    public MyVideoView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }
//在有样式（style）时调用
    public MyVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    //重新设置大小
    public void setSize(int width,int height){
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.height = height;
        layoutParams.width = width;
        setLayoutParams(layoutParams);

    }
}
