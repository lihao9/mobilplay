package com.lihao.mobilplay.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lihao.mobilplay.R;
import com.lihao.mobilplay.bean.MyVideo;
import com.lihao.mobilplay.customview.VitmiaoVideoView;
import com.lihao.mobilplay.tools.LogUtil;
import com.lihao.mobilplay.tools.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;

public class VitmiaomMediaPlayActivity extends AppCompatActivity implements View.OnClickListener {
    //判断uri地址是否是网络地址
    private boolean isNetUri;
    //是否是全屏
    private boolean isFull;
    //视屏播放默认的大小
    private int defualtWidth;
    private int defualtHeight;

    private LinearLayout loadingLayout;

    //调节音量
    private AudioManager am;
    //最大声音
    private int maxVioce;
    //当前声音
    private int currentVoidce;
    //是否是静音
    private boolean isMute;


    //屏幕的真实大小
    private int windowWidth;
    private int windowHeight;
    //
    private boolean isUseSystem = true;

    //用于更新进度
    private static final int PROGESS = 1;
    //用于判断视屏播放
    private static final int ISSHOW = 2;
    //更新网速
    private static final int SHOW_SPEED = 3;
    
    //监听电量的广播
    private MyReceiver myReceiver;
    //电量
    private int level;
    //控制器
    private RelativeLayout mMediaControllerLayout;
    //当前是否显示了视屏控制界面
    private boolean isShowControllerLayout;

    private Utils util;
    private VitmiaoVideoView mVideoView;
    private Uri mVideoUri;
    //当前播放视屏的列表集合
    private ArrayList<MyVideo> videos;
    private int position;
    //手势识别器
    private GestureDetector dector;

    private LinearLayout bufferLL;
    private TextView bufferTv;
    private TextView mediaControllerTvname;
    private ImageView mediaControllerIvbattery;
    private TextView mediaControllerTvtime;
    private TextView mediaControllerSysTime;
    private Button mediaControllerBtvoice;
    private SeekBar pbVoice;
    private Button mediaControllerBtSwichPlayer;
    private TextView tvCurrentTime;
    private SeekBar pbTime;
    private TextView tvTotalTime;
    private Button btnVideoExit;
    private Button btnVideoPre;
    private Button btnVideoPause;
    private Button btnVideoNext;
    private Button btnVideoSwichScreen;
    private TextView loadingTv;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2017-03-29 14:37:43 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        loadingTv = (TextView) findViewById(R.id.loading_tv);
        loadingLayout = (LinearLayout) findViewById(R.id.loading_ll);
        bufferLL = (LinearLayout) findViewById(R.id.buffer_ll);
        bufferTv = (TextView) findViewById(R.id.buffer_tv);
        mediaControllerTvname = (TextView)findViewById( R.id.media_controller_tvname );
        mediaControllerIvbattery = (ImageView)findViewById( R.id.media_controller_ivbattery );
//        mediaControllerTvtime = (TextView)findViewById( R.id.media_controller_tvtime );
        mediaControllerSysTime = (TextView)findViewById( R.id.media_controller_sys_tvtime );
        mediaControllerBtvoice = (Button)findViewById( R.id.media_controller_btvoice );
        pbVoice = (SeekBar)findViewById( R.id.pb_voice );
        mediaControllerBtSwichPlayer = (Button)findViewById( R.id.media_controller_bt_swich_player );
        tvCurrentTime = (TextView)findViewById( R.id.tv_current_time );
        pbTime = (SeekBar)findViewById( R.id.pb_time );
        tvTotalTime = (TextView)findViewById( R.id.tv_total_time );
        btnVideoExit = (Button)findViewById( R.id.btn_video_exit );
        btnVideoPre = (Button)findViewById( R.id.btn_video_pre );
        btnVideoPause = (Button)findViewById( R.id.btn_video_pause );
        btnVideoNext = (Button)findViewById( R.id.btn_video_next );
        btnVideoSwichScreen = (Button)findViewById( R.id.btn_video_swich_screen );

        mediaControllerBtvoice.setOnClickListener( this );
        mediaControllerBtSwichPlayer.setOnClickListener( this );
        btnVideoExit.setOnClickListener( this );
        btnVideoPre.setOnClickListener( this );
        btnVideoPause.setOnClickListener( this );
        btnVideoNext.setOnClickListener( this );
        btnVideoSwichScreen.setOnClickListener( this );
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2017-03-29 14:37:43 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if ( v == mediaControllerBtvoice ) {
            // Handle clicks for mediaControllerBtvoice
            //控制--声音
//            isMute = true;
            if(isMute){
                //静音--开启声音
                setVoice(currentVoidce);
            }else {
                setVoice(0);
            }
        } else if ( v == mediaControllerBtSwichPlayer ) {
            // Handle clicks for mediaControllerBtSwichPlayer
            // 选择系统播放器
            startSystemVideo();
        } else if ( v == btnVideoExit ) {
            // Handle clicks for btnVideoExit
            // 退出播放
            finish();
        } else if ( v == btnVideoPre ) {
            // Handle clicks for btnVideoPre
            //点击播放上一个视屏
            playPreVideo();
        } else if ( v == btnVideoPause ) {
            // Handle clicks for btnVideoPause
            playAndPause();
        } else if ( v == btnVideoNext ) {
            // Handle clicks for btnVideo
            //点击播放下一个视屏
            playNextVideo();

        } else if ( v == btnVideoSwichScreen ) {
            // Handle clicks for btnVideoSwichScreen
            // 点击全屏
            setScreenSize();
        }
        handler.removeMessages(ISSHOW);
        handler.sendEmptyMessageDelayed(ISSHOW,4000);
    }

    private void startSystemVideo() {
        //选择系统播放器
        if (mVideoView!=null){
            mVideoView.stopPlayback();
        }
        //数据完整传送过去
        //关闭当前播放器
        Intent intent = new Intent(VitmiaomMediaPlayActivity.this,SystemMediaPlayActivity.class);
        if (videos!=null&&videos.size()>0){
            intent.putParcelableArrayListExtra("videos",videos);
            intent.putExtra("position",position);
        }else if (mVideoUri!=null){
            intent.setDataAndType(mVideoUri,"video/*");
        }
        startActivity(intent);
        finish();
    }

    private void playAndPause() {
        //点击了播放--暂停按钮
        if (mVideoView.isPlaying()){
            //如果视屏正在播放--让视屏暂停，并修改按钮状态
            mVideoView.pause();
            btnVideoPause.setBackgroundResource(R.drawable.btn_video_start_select);
        }else {
            mVideoView.start();
            btnVideoPause.setBackgroundResource(R.drawable.btn_video_pause_select);
        }
    }

    private void playPreVideo() {
        loadingLayout.setVisibility(View.VISIBLE);
        position--;
        mediaControllerTvname.setText(videos.get(position).getName());
        mVideoView.setVideoPath(videos.get(position).getData());
        if (position==0){
            btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
            btnVideoPre.setEnabled(false);
        }else {
            btnVideoPre.setEnabled(true);
            btnVideoNext.setEnabled(true);
            btnVideoPre.setBackgroundResource(R.drawable.btn_video_pre_select);
            btnVideoNext.setBackgroundResource(R.drawable.btn_video_next_select);
        }
        btnVideoPause.setBackgroundResource(R.drawable.btn_video_pause_select);
    }

    private void playNextVideo() {
        loadingLayout.setVisibility(View.VISIBLE);
        position++;
        mediaControllerTvname.setText(videos.get(position).getName());
        //判断当前播放的视屏是否来自网络
        isNetUri = util.isNetUri(videos.get(position).getData());

        mVideoView.setVideoPath(videos.get(position).getData());
        if (position==videos.size()-1){
            btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
            btnVideoNext.setEnabled(false);
        }else {
            btnVideoNext.setEnabled(true);
            btnVideoPre.setEnabled(true);
            btnVideoPre.setBackgroundResource(R.drawable.btn_video_pre_select);
            btnVideoNext.setBackgroundResource(R.drawable.btn_video_next_select);
        }
        btnVideoPause.setBackgroundResource(R.drawable.btn_video_pause_select);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Vitamio.isInitialized(getApplicationContext());
        setContentView(R.layout.activity_vitmiao_media_play);
        init();
        getData();
        setData();
    }

    private void getData() {
        //数据来自文件夹，图片浏览器
        mVideoUri = getIntent().getData();

        videos = getIntent().getParcelableArrayListExtra("videos");
//        LogUtil.e(""+videos.size());
        position = getIntent().getIntExtra("position",0);

    }

    private void init(){
//        getWindowManager().getDefaultDisplay().getHeight();
//        getWindowManager().getDefaultDisplay().getWidth();
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        windowWidth = outMetrics.widthPixels;
        windowHeight = outMetrics.heightPixels;

        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        maxVioce = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentVoidce = am.getStreamVolume(AudioManager.STREAM_MUSIC);

        mMediaControllerLayout = (RelativeLayout) findViewById(R.id.media_controller_layout);
        mMediaControllerLayout.setVisibility(View.GONE);
        myReceiver = new MyReceiver();
        findViews();
        util = new Utils();
        mVideoView = (VitmiaoVideoView) findViewById(R.id.vitmiao_vv);
//        mVideoView.setMediaController(new MediaController(this));
        setListener();
        IntentFilter filter = new IntentFilter();
        //电量改变的广播
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(myReceiver,filter);
        dector = new GestureDetector(VitmiaomMediaPlayActivity.this,new MyOnGestureListener());
        handler.sendEmptyMessage(SHOW_SPEED);
    }

    private void setData(){
//        mVideoView.setVideoURI(mVideoUri);
        if (videos!=null&&videos.size()>0){
            //从播放列表传过来有数据
            MyVideo myVideo = videos.get(position);
            mVideoView.setVideoPath(myVideo.getData());
            mediaControllerTvname.setText(myVideo.getName());
            if (position==0){
                btnVideoPre.setEnabled(false);
                btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
            }if (position==videos.size()-1){
                btnVideoNext.setEnabled(false);
                btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);

            }
            pbVoice.setMax(maxVioce);
            pbVoice.setProgress(currentVoidce);
        }else if(mVideoUri!=null){
            //一般是点击文件夹，图片浏览器中的文件传过来的uri地址
            isNetUri = util.isNetUri(mVideoUri.toString());
            mVideoView.setVideoURI(mVideoUri);
            btnVideoPre.setEnabled(false);
            btnVideoNext.setEnabled(false);
            pbVoice.setMax(maxVioce);
            pbVoice.setProgress(currentVoidce);
        }else {
            Toast.makeText(this, "当前无视屏播放", Toast.LENGTH_SHORT).show();
        }

    }

    private float startY;
    private float endY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        dector.onTouchEvent(event);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
//                int startX = (int) event.getX();
                startY =  event.getY();
//                showAndHindControllerLayout();
                handler.removeMessages(ISSHOW);
                break;
            case MotionEvent.ACTION_MOVE:
//                int endX = (int) event.getX();
                endY = event.getY();
                float distense = endY - startY;
//                滑动的距离：屏幕的高度 = 增加的音量：总音量
                int voiceAdd = (int) ((distense/windowHeight)*maxVioce);
                currentVoidce = Math.min(maxVioce,Math.max(0,currentVoidce+voiceAdd));
                setVoice(currentVoidce);
                handler.removeMessages(ISSHOW);
                break;
            case MotionEvent.ACTION_UP:
                handler.removeMessages(ISSHOW);
                handler.sendEmptyMessageDelayed(ISSHOW,2000);
                break;
        }
        return super.onTouchEvent(event);
    }

    //设置监听
    private void setListener() {
        if (isUseSystem){
            //使用系统的监听卡
            mVideoView.setOnInfoListener(new MyOnINfoListener());
        }
        mVideoView.setOnPreparedListener(new MyOnPrepareListener());
        mVideoView.setOnErrorListener(new MyOnErrorListener());
        mVideoView.setOnCompletionListener(new MyOnCompletionListener());
        pbTime.setOnSeekBarChangeListener(new VideoOnSeekBarChangeListener());
        pbVoice.setOnSeekBarChangeListener(new VoiceOnSeekBarChangeListener());
    }

    //上一个
    private int prePosition;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SHOW_SPEED:
                    //更新显示网速
                    String netSpeed = util.getNetSpeed(VitmiaomMediaPlayActivity.this);
                    bufferTv.setText("正在缓冲"+netSpeed);
                    loadingTv.setText("正在加载"+netSpeed);
                    handler.removeMessages(SHOW_SPEED);
                    handler.sendEmptyMessageDelayed(SHOW_SPEED,2000);
                    break;
                case ISSHOW:
                    //隐藏视频控制面板
                    showAndHindControllerLayout();
                    break;
                case PROGESS:
//每秒更新操作
                    //设置当前进度
                    int currentPosition = (int) mVideoView.getCurrentPosition();
                    pbTime.setProgress(currentPosition);
                    //更新当前已经播放的时间
                    tvCurrentTime.setText(util.stringForTime(currentPosition));
                    //设置系统时间
                    mediaControllerSysTime.setText(getSysTime());
                    //每秒更新
                    if (isNetUri){
                        //是网络视屏资源
                        //0--100的缓冲值
                        int buffer = mVideoView.getBufferPercentage();
                        int totalBuffer = buffer*pbTime.getMax();
                        int currentBuffer = totalBuffer/100;
                        pbTime.setSecondaryProgress(currentBuffer);
                    }else {
                        pbTime.setSecondaryProgress(0);
                    }
                    if (!isUseSystem){
                        if (mVideoView.isPlaying()) {
                            int buffer = currentPosition - prePosition;
                            if (buffer < 500) {
                                //视屏卡了
                                bufferLL.setVisibility(View.VISIBLE);
                            } else {
//                            视屏不卡
                                bufferLL.setVisibility(View.GONE);
                            }
                        }else {
                            bufferLL.setVisibility(View.GONE);
                        }
                    }
                    prePosition = currentPosition;
                    sendEmptyMessageDelayed(PROGESS,1000);
                    break;
            }
        }
    };

    private String getSysTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());
    }

    @Override
    protected void onDestroy() {
        if (myReceiver!=null){
            //取消广播的监听
            unregisterReceiver(myReceiver);
        }
        super.onDestroy();
    }

    //
    private class MyReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            //获取系统发出的电量变化广播
            level = intent.getIntExtra("level", 0);
            LogUtil.e("受到了广播"+level);
            setBattery(level);
        }
    }

    //设置电量图标
    private void setBattery(int lever) {
        if (lever<=0){
            mediaControllerIvbattery.setImageResource(R.drawable.ic_battery_0);
        }else if (lever<=10){
            mediaControllerIvbattery.setImageResource(R.drawable.ic_battery_10);
        }else if (lever<=20){
            mediaControllerIvbattery.setImageResource(R.drawable.ic_battery_20);
        }else if (lever<=40){
            mediaControllerIvbattery.setImageResource(R.drawable.ic_battery_40);
        }else if (lever<=60){
            mediaControllerIvbattery.setImageResource(R.drawable.ic_battery_60);
        }else if (lever<=80){
            mediaControllerIvbattery.setImageResource(R.drawable.ic_battery_80);
        }else {
            mediaControllerIvbattery.setImageResource(R.drawable.ic_battery_100);
        }
    }

    private class MyOnPrepareListener implements MediaPlayer.OnPreparedListener {

        /**
         * Called when the media file is ready for playback.
         *
         * @param mp the MediaPlayer that is ready for playback
         */
        @Override
        public void onPrepared(MediaPlayer mp) {

            LogUtil.i("Vitmiao准备完成");
            loadingLayout.setVisibility(View.INVISIBLE);
            LogUtil.i("Vitmiao准备完成");
            mp.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {

                }
            });
            defualtWidth = windowWidth;
            defualtHeight = windowHeight;
//            int height = mp.getVideoHeight();
//            int width = mp.getVideoWidth();
//            if(windowWidth*height>width*windowHeight){
//                defualtWidth = width *windowHeight/ height;
//            }else {
//                defualtHeight = height*windowWidth/width;
//            }
            mVideoView.setSize(defualtWidth,defualtHeight);
            //准备好了调用--开始播放视屏
            mVideoView.start();
            int duration = (int) mVideoView.getDuration();//总时长
            //设置各种tv
            tvTotalTime.setText(util.stringForTime(duration));
            pbTime.setMax(duration);
//            移除上次的更新progress的消息
            handler.removeMessages(PROGESS);
            handler.sendEmptyMessage(PROGESS);
        }
    }

    private class MyOnErrorListener implements MediaPlayer.OnErrorListener {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            //播放出错--开启万能播放器播放
            Toast.makeText(VitmiaomMediaPlayActivity.this, "播放出错", Toast.LENGTH_SHORT).show();
            finish();
            return true;
        }
    }

    private class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {

        /**
         * Called when the end of a media source is reached during playback.
         *
         * @param mp the MediaPlayer that reached the end of the file
         */
        @Override
        public void onCompletion(MediaPlayer mp) {
            //播放完成
//            Toast.makeText(SystemMediaPlayActivity.this, "播放完成", Toast.LENGTH_SHORT).show();
            if (videos!=null&&videos.size()>0) {
                if (position == videos.size() - 1) {
                    Toast.makeText(VitmiaomMediaPlayActivity.this, "视屏播放完成", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                playNextVideo();
            }else {
                Toast.makeText(VitmiaomMediaPlayActivity.this, "播放完成", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

    }

    private class VideoOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            //seek进度改变时调用
            if (fromUser){
                //用户改变引起的seek变动
                if (progress == 0){
                    isMute = true;
                }
                mVideoView.seekTo(progress);
                handler.removeMessages(ISSHOW);
                isMute = false;
            }
        }

        /**
         * Notification that the user has started a touch gesture. Clients may want to use this
         * to disable advancing the seekbar.
         *
         * @param seekBar The SeekBar in which the touch gesture began
         */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            //用户手指触碰调用
            handler.removeMessages(ISSHOW);
        }

        /**
         * Notification that the user has finished a touch gesture. Clients may want to use this
         * to re-enable advancing the seekbar.
         *
         * @param seekBar The SeekBar in which the touch gesture began
         */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            //用户手指停止触碰时调用
            handler.removeMessages(ISSHOW);
            handler.sendEmptyMessageDelayed(ISSHOW,2000);
        }
    }

    private class MyOnGestureListener extends GestureDetector.SimpleOnGestureListener{

        public MyOnGestureListener() {
            super();
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            //长按--播放和暂停视屏
//            Toast.makeText(SystemMediaPlayActivity.this, "长按", Toast.LENGTH_SHORT).show();
            playAndPause();

        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            //双击
//            Toast.makeText(SystemMediaPlayActivity.this, "双击", Toast.LENGTH_SHORT).show();
            //全屏或者默认大小
            setScreenSize();
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
//            Toast.makeText(SystemMediaPlayActivity.this, "单击", Toast.LENGTH_SHORT).show();
            //显示或者隐藏控制界面
            showAndHindControllerLayout();
            return super.onSingleTapConfirmed(e);
            //
        }
    }

    private void setScreenSize() {
        if (isFull){
            //1.设置为全屏或者默认
            mVideoView.setSize(defualtWidth,defualtHeight);
            //2.设置全屏按钮
            btnVideoSwichScreen.setBackgroundResource(R.drawable.btn_video_swich_screen_full_select);
            //3.修改isFull是值
            isFull=false;
        }else {
            //1.设置为全屏或者默认
            mVideoView.setSize(windowWidth,windowHeight);
            //2.设置全屏按钮
            btnVideoSwichScreen.setBackgroundResource(R.drawable.btn_video_swich_screen_default_select);
            //3.修改isFull是值
            isFull=true;
        }
    }

    /**
     * 显示或者隐藏控制面板
     */
    private void showAndHindControllerLayout() {
        if (isShowControllerLayout){
            //隐藏
            mMediaControllerLayout.setVisibility(View.GONE);
            isShowControllerLayout = false;
            handler.removeMessages(ISSHOW);
        }else {
            //显示
            mMediaControllerLayout.setVisibility(View.VISIBLE);
            isShowControllerLayout = true;
            //在显示一段时间后自动隐藏
            handler.removeMessages(ISSHOW);
            handler.sendEmptyMessageDelayed(ISSHOW,4000);

        }
    }

    private class VoiceOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser){
                //设置音量
                if (progress>0){
                    isMute = false;
                }else {
                    isMute = true;
                }
                setVoice(progress);
                handler.removeMessages(ISSHOW);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            handler.removeMessages(ISSHOW);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.removeMessages(ISSHOW);
            handler.sendEmptyMessageDelayed(ISSHOW,2000);
        }
    }

    private void setVoice(int progress) {
        //根据seekbar设置音量
        if (progress == 0){
            am.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0);
            pbVoice.setProgress(progress);
            isMute = true;
            return;
        }
        am.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0);
        pbVoice.setProgress(progress);
        currentVoidce = progress;
        isMute = false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_VOLUME_DOWN){
            //音量减小
            currentVoidce--;
            setVoice(currentVoidce);
            handler.removeMessages(ISSHOW);
        }else if (keyCode==KeyEvent.KEYCODE_VOLUME_UP){
            currentVoidce++;
            if (currentVoidce>=maxVioce){
                currentVoidce = maxVioce;
            }
            setVoice(currentVoidce);
            handler.removeMessages(ISSHOW);
        }
        return super.onKeyDown(keyCode, event);
    }

    private class MyOnINfoListener implements MediaPlayer.OnInfoListener {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            switch (what){
                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
//                    Toast.makeText(SystemMediaPlayActivity.this, "视屏卡了", Toast.LENGTH_SHORT).show();
                    bufferLL.setVisibility(View.VISIBLE);
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
//                    Toast.makeText(SystemMediaPlayActivity.this, "视屏正常播放", Toast.LENGTH_SHORT).show();
                    bufferLL.setVisibility(View.GONE);
                    break;
            }
            return true;
        }
    }
}

