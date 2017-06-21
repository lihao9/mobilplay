package com.lihao.mobilplay.page;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lihao.mobilplay.R;
import com.lihao.mobilplay.activity.PlayMusicActivity;
import com.lihao.mobilplay.adapter.MyVideoAdapter;
import com.lihao.mobilplay.base.BasePage;
import com.lihao.mobilplay.bean.MyVideo;
import com.lihao.mobilplay.tools.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hbm on 2017/3/27.
 */

public class AudioPage extends BasePage {

    private View view;
    private List<MyVideo> audios;
    private ListView mAudioLv;
    private ProgressBar mAudioPb;
    private TextView mAudioTv;
    private MyVideoAdapter adapter;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    //获取数据成功
                    mAudioPb.setVisibility(View.GONE);
                    setData();
                    break;
            }
        }
    };

    /**
     * 为当前页面设置数据
     */
    private void setData() {
        if (audios!=null&&audios.size()>0){
            mAudioTv.setVisibility(View.GONE);
            adapter = new MyVideoAdapter(context,audios,false,false);
            mAudioLv.setAdapter(adapter);
            mAudioLv.setOnItemClickListener(new MyOnItemClickListener());
        }
    }

    public AudioPage(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        LogUtil.e("本地音频视图初始化");
        view = View.inflate(context, R.layout.audio_layout,null);
        mAudioLv = (ListView) view.findViewById(R.id.audio_lv);
        mAudioPb = (ProgressBar) view.findViewById(R.id.audio_pb);
        mAudioTv = (TextView) view.findViewById(R.id.audio_tv);
        return view;
    }

    @Override
    public void initData() {
        //加载数据并设置给view
        LogUtil.e("本地音频数据初始化");
        getAudioDateFromSDcard();
    }

    private void getAudioDateFromSDcard() {
        new Thread(){
            @Override
            public void run() {
                ContentResolver resolver = context.getContentResolver();
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

                handler.sendEmptyMessage(0);
            }

        }.start();
    }

    private class MyOnItemClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(context,PlayMusicActivity.class);
            intent.putExtra("position",position);
            context.startActivity(intent);
        }
    }
}
