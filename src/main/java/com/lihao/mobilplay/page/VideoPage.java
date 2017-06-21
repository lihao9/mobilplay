package com.lihao.mobilplay.page;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lihao.mobilplay.R;
import com.lihao.mobilplay.activity.SystemMediaPlayActivity;
import com.lihao.mobilplay.adapter.MyVideoAdapter;
import com.lihao.mobilplay.base.BasePage;
import com.lihao.mobilplay.bean.MyVideo;
import com.lihao.mobilplay.tools.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hbm on 2017/3/27.
 */

public class VideoPage extends BasePage {
    public View view;
    private TextView mainVideoTv;
    private ListView mainVideoLv;
    private ProgressBar mainVideoPb;
    private List<MyVideo> videos;
    private MyVideoAdapter adapter;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (videos!=null&&videos.size()>0){
                //获取到了数据
                adapter = new MyVideoAdapter(context,videos,true,false);
                mainVideoLv.setAdapter(adapter);
                mainVideoTv.setVisibility(View.INVISIBLE);
            }else {
                mainVideoTv.setVisibility(View.VISIBLE);
            }
            mainVideoPb.setVisibility(View.GONE);
        }
    };
    public VideoPage(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        LogUtil.e("本地视屏视图初始化");
        view = View.inflate(context, R.layout.video_layout,null);
        mainVideoLv = (ListView) view.findViewById(R.id.main_video_lv);
        //设置监听事件
        mainVideoLv.setOnItemClickListener(new MyItemOnclickListener());
        mainVideoPb = (ProgressBar) view.findViewById(R.id.main_video_pb);
        mainVideoTv = (TextView) view.findViewById(R.id.main_video_tv);
//        tv = new TextView(context);
//        tv.setTextColor(Color.RED);
//        tv.setGravity(Gravity.CENTER);
        return view;
    }

    @Override
    public void initData() {
        /**
         * 加载本地视频文件到并设置显示到当前page中
         * 方法
         * a.遍历所有的文件，以后缀名来辨别是否是video文件
         * b.从内容提供者里获取video文件的信息
         */
        getVideoDateFromSDcard();

    }

    private void getVideoDateFromSDcard() {
        //耗时操作放在子线程中
        new Thread(){
            @Override
            public void run() {
                super.run();
                //
                ContentResolver videoResolver = context.getContentResolver();
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                String[] obj = {
                        MediaStore.Video.Media.DISPLAY_NAME,//视频文件在sdcard的名称
                        MediaStore.Video.Media.DURATION,//视频总时长
                        MediaStore.Video.Media.SIZE,//视频的文件大小
                        MediaStore.Video.Media.DATA,//视频的绝对地址
                        MediaStore.Video.Media.ARTIST,//歌曲的演唱者
                };
                Cursor videoCursor = videoResolver.query(uri, obj, null, null, null);
                if (videoCursor!=null) {
                    videos = new ArrayList<MyVideo>();
                    while (videoCursor.moveToNext()){
                        MyVideo video = new MyVideo();
                        videos.add(video);
                        String name = videoCursor.getString(0);//视频文件在sdcard的名称
                        video.setName(name);

                        long duration = videoCursor.getLong(1);//视频总时长
                        video.setDuration(duration);

                        long size = videoCursor.getLong(2);//视频的文件大小
                        video.setSize(size);

                        String data = videoCursor.getString(3);//视频的绝对地址
                        video.setData(data);

                        String artist = videoCursor.getString(4);//歌曲的演唱者
                        video.setArtist(artist);
                    }
                    videoCursor.close();
                }

                handler.sendEmptyMessage(0);

            }
        }.start();

    }

    /**
     * 列表的点击事件
     */

    class MyItemOnclickListener implements AdapterView.OnItemClickListener{


        /**
         * Callback method to be invoked when an item in this AdapterView has
         * been clicked.
         * <p>
         * Implementers can call getItemAtPosition(position) if they need
         * to access the data associated with the selected item.
         *
         * @param parent   The AdapterView where the click happened.
         * @param view     The view within the AdapterView that was clicked (this
         *                 will be a view provided by the adapter)
         * @param position The position of the view in the adapter.
         * @param id       The row id of the item that was clicked.
         */
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            //播放点击的视频
            MyVideo video = videos.get(position);
            /**
             * 1.隐式意图调用手机已有的视屏播放器
             */
//            Intent intent = new Intent();
//            intent.setDataAndType(Uri.parse(video.getData()),"video/*");
//            context.startActivity(intent);

            /**
             * 2.调用自己的，显示意图--传入播放的地址
             */
//            Intent intent1 = new Intent((Activity)context, SystemMediaPlayActivity.class);
//            intent1.setDataAndType(Uri.parse(video.getData()),"video/*");
//            context.startActivity(intent1);

            /**
             * 3.传一个列表给播放视屏界面的activity
             * 需要序列化对象
             */
            Intent intent = new Intent(context,SystemMediaPlayActivity.class);
            intent.putParcelableArrayListExtra("videos", (ArrayList<? extends Parcelable>) videos);
            intent.putExtra("position",position);
            context.startActivity(intent);


        }
    }
}
