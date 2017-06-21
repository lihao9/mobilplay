package com.lihao.mobilplay.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.lihao.mobilplay.R;
import com.lihao.mobilplay.bean.MyVideo;
import com.lihao.mobilplay.service.AudioService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class PlayMusicActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PROGRESS = 1;
    private LinearLayout llBottom;
    private TextView tvTime;
    private SeekBar seekbarAudio;
    private Button btnAudioPlaymode;
    private Button btnAudioPre;
    private Button btnAudioStartPause;
    private Button btnAudioNext;
    private Button btnLyrc;

    private AudioService.ServiceBinder binder;
    private AudioService service;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case PROGRESS:
                    seekbarAudio.setProgress(service.getCurrentPosition());
                    handler.sendEmptyMessageDelayed(PROGRESS,1000);
                    break;
            }
        }
    };
    //当前播放的歌曲位置
    private int position;
    private MyVideo audioItem;
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder ibinder) {
            binder = (AudioService.ServiceBinder) ibinder;
            service = binder.getInstence();
            if (isNotifi){
                showData();
            }else {
                service.openAudio(position);
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (service!=null){
                service.stop();
                service = null;
            }
        }
    };


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MyVideo event) {
        showData();
        }

    private void showData() {
        //显示播放的信息
//        tvArtist.setText(service.getArtist());
//        tvName.setText(service.getName());
        //设置进度条的最大值
        seekbarAudio.setMax(service.getDuration());

        //发消息
        handler.sendEmptyMessage(PROGRESS);
    }


    private static final int SIGON = 1;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2017-04-13 12:06:16 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        llBottom = (LinearLayout)findViewById( R.id.ll_bottom );
        tvTime = (TextView)findViewById( R.id.tv_time );
        seekbarAudio = (SeekBar)findViewById( R.id.seekbar_audio );
        btnAudioPlaymode = (Button)findViewById( R.id.btn_audio_playmode );
        btnAudioPre = (Button)findViewById( R.id.btn_audio_pre );
        btnAudioStartPause = (Button)findViewById( R.id.btn_audio_start_pause );
        btnAudioNext = (Button)findViewById( R.id.btn_audio_next );
        btnLyrc = (Button)findViewById( R.id.btn_lyrc );

        btnAudioPlaymode.setOnClickListener( this );
        btnAudioPre.setOnClickListener( this );
        btnAudioStartPause.setOnClickListener( this );
        btnAudioNext.setOnClickListener( this );
        btnLyrc.setOnClickListener( this );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);
        EventBus.getDefault().register(this);
        findViews();
        getData();
        initData();
        bindAndStartService();
    }

    private void initData() {

    }

    //是否来自notify
    private boolean isNotifi;
    private void getData() {
        isNotifi = getIntent().getBooleanExtra("isNotifi", false);
        if(!isNotifi) {
            position = getIntent().getIntExtra("position", 0);
        }
    }

    private void bindAndStartService() {
        Intent intent = new Intent(this, AudioService.class);
        bindService(intent,conn, Context.BIND_AUTO_CREATE);
        startService(intent);
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2017-04-13 12:06:16 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if ( v == btnAudioPlaymode ) {
            // Handle clicks for btnAudioPlaymode
            //播放模式

        } else if ( v == btnAudioPre ) {
            // Handle clicks for btnAudioPre
            //上一首

        } else if ( v == btnAudioStartPause ) {
            // Handle clicks for btnAudioStartPause
            //开始播放和暂停播放

        } else if ( v == btnAudioNext ) {
            // Handle clicks for btnAudioNext
            //播放下一首

        } else if ( v == btnLyrc ) {
            // Handle clicks for btnLyrc
            //显示歌词

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
        EventBus.getDefault().unregister(this);
    }
}
