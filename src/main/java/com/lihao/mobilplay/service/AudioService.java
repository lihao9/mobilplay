package com.lihao.mobilplay.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.provider.MediaStore;

import com.lihao.mobilplay.R;
import com.lihao.mobilplay.activity.PlayMusicActivity;
import com.lihao.mobilplay.bean.MyVideo;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;

public class AudioService extends Service {
    private MediaPlayer musicPlayer;
    private ArrayList<MyVideo> audios;
    private MyVideo audioItem;
    private int position;

    /**
     * 顺序播放
     */
    public static final int REPEAT_NORMAL = 1;
    /**
     * 单曲循环
     */
    public static final int REPEAT_SINGLE = 2;
    /**
     * 全部循环
     */
    public static final int REPEAT_ALL = 3;

    /**
     * 播放模式
     */
    private int playmode = REPEAT_NORMAL;

    private NotificationManager manager;

    public AudioService() {
    }

    //bindService时调用
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return new ServiceBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        getAudioDateFromSDcard();
    }

    private void getAudioDateFromSDcard() {
        new Thread(){
            @Override
            public void run() {
                ContentResolver resolver = getContentResolver();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] obj = {
                        MediaStore.Audio.Media.DISPLAY_NAME,//音乐文件在sdcard的名称
                        MediaStore.Audio.Media.DURATION,//音乐总时长
                        MediaStore.Audio.Media.SIZE,//音乐文件的大小
                        MediaStore.Audio.Media.DATA,//音乐的绝对地址
                        MediaStore.Audio.Media.ARTIST,//音乐的演唱者
                };
                Cursor query = resolver.query(uri, obj, null, null, null);
                if (query!=null){
                    audios = new ArrayList<MyVideo>();
                    while (query.moveToNext()){
                        MyVideo video = new MyVideo();
                        audios.add(video);
                        String name = query.getString(0);//视频文件在sdcard的名称
                        video.setName(name);

                        long duration = query.getLong(1);//视频总时长
                        video.setDuration(duration);

                        long size = query.getLong(2);//视频的文件大小
                        video.setSize(size);

                        String data = query.getString(3);//视频的绝对地址
                        video.setData(data);

                        String artist = query.getString(4);//歌曲的演唱者
                        video.setArtist(artist);
                    }
                    query.close();
                }
            }

        }.start();
    }

    //startService时调用
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 停止音乐
     */
    public void stop(){

    }

    /**
     * 根据位置打开对应的音频文件,并且播放
     *
     * @param position
     */
    public void openAudio(int position) {
        this.position = position;
        audioItem = audios.get(position);
        if (audios != null && audios.size() > 0) {
            audioItem = audios.get(position);
            if (musicPlayer != null) {
//                musicPlayer.release();
                musicPlayer.reset();
            }
            musicPlayer = new MediaPlayer();
            try {
                musicPlayer.setOnPreparedListener(new MyOnPreparedListener());
                musicPlayer.setOnCompletionListener(new MyOnCompletionListener());
                musicPlayer.setOnErrorListener(new MyOnErrorListener());
                musicPlayer.setDataSource(audioItem.getData());
                musicPlayer.prepareAsync();
                if (playmode == REPEAT_SINGLE) {
                    //单曲循环
                    musicPlayer.setLooping(true);
                } else {
                    musicPlayer.setLooping(false);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class MyOnErrorListener implements MediaPlayer.OnErrorListener {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            next();
            return true;
        }
    }

    class  MyOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            next();
        }
    }

    class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {

        @Override
        public void onPrepared(MediaPlayer mp) {
            //通知Activity来获取信息--广播
//            notifyChange(OPENAUDIO);
            EventBus.getDefault().post(audioItem);
            start();
        }
    }


    /**
     * 播放音乐
     */
    public void start(){
        musicPlayer.start();
        //在状态栏显示出来
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent in = new Intent(this, PlayMusicActivity.class);
        in.putExtra("isNotifi",true);
        PendingIntent intent = PendingIntent.getActivity(this,1,in,PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.notification_music_playing)
                .setContentTitle("音乐播放器")
                .setContentText("正在播放")
                .setContentIntent(intent)
                .build();
        manager.notify(1,notification);
    }

    /**
     * 播暂停音乐
     */
    public void pause(){}

    /**
     * 得到当前音频的总时长
     *
     * @return
     */
    public int getDuration(){
        return (int) audioItem.getDuration();
    }

    /**
     * 得到艺术家
     *
     * @return
     */
    public String getArtist(){
        return audioItem.getArtist();
    }

    /**
     * 得到歌曲名字
     *
     * @return
     */
    public String getName(){
        return audioItem.getName();
    }

    /**
     * 得到歌曲播放的路径
     *
     * @return
     */
    public String getAudioPath(){
        return audioItem.getData();
    }

    /**
     * 播放下一个音频
     */
    public void next(){}

    /**
     * 播放上一个音频
     */
    public void pre(){}

    /**
     * 设置播放模式
     *
     * @param playmode
     */
    public void setPlayMode(int playmode){}

    /**
     * 得到播放模式
     *
     * @return
     */
    public int getPlayMode(){
        return 0;
    }

    /**
     * 是否在播放音频
     * @return
     */
    public boolean isPlaying(){
        return musicPlayer.isPlaying();
    }

    /**
     * 得到当前的播放进度
     *
     * @return
     */
    public int getCurrentPosition() {
        return musicPlayer.getCurrentPosition();
    }


    //获取服务实例的内部类
    public class ServiceBinder extends Binder {
        public AudioService getInstence(){
            return AudioService.this;
        }
    }
}
