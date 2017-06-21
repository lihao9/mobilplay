package com.lihao.mobilplay.customview;

import android.content.Context;
import android.util.AttributeSet;

import com.lihao.mobilplay.R;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;

/**
 * Created by hbm on 2017/4/12.
 */

public class JcVideo extends JCVideoPlayer {
    public JcVideo(Context context) {
        super(context);
    }

    public JcVideo(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getLayoutId() {
        return R.id.jcv_video_player;
    }

    @Override
    public void init(Context context) {

    }
}
