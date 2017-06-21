package com.lihao.mobilplay.page;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
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
import com.lihao.mobilplay.tools.Constants;
import com.lihao.mobilplay.tools.LogUtil;
import com.powyin.scroll.widget.SwipeControl;
import com.powyin.scroll.widget.SwipeRefresh;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by hbm on 2017/3/27.
 */

public class NetVideoPage extends BasePage {
    private static final int REFRESH = 1;
    private static final int LOADING = 2;
    private View view;
    private SwipeRefresh swipeRefresh;
    private ArrayList<MyVideo> videos;
    //刷新的数据
    private ArrayList<MyVideo> refreshData;
    private ArrayList<MyVideo> loadingData;
    private ListView mNetVideoLv;
    private MyVideoAdapter adapter;
    private boolean isRefresh;
    private boolean isLoading;

    private ProgressBar mPb;
    private TextView mTv;


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case REFRESH:
                    setData();
                    break;
                case LOADING:
                    setData();
                    break;
            }
        }
    };

    public NetVideoPage(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        LogUtil.e("网络视屏视图初始化");
//        tv = new TextView(context);
//        tv.setGravity(Gravity.CENTER);
        view = View.inflate(context, R.layout.layout_net_video,null);
        swipeRefresh = (SwipeRefresh) view.findViewById(R.id.re);
        swipeRefresh.setSwipeModel(SwipeControl.SwipeModel.SWIPE_BOTH);
        mNetVideoLv = (ListView) view.findViewById(R.id.lv_net_video);
        mNetVideoLv.setOnItemClickListener(new MyOnItemClickListenenr());
        mTv = (TextView) view.findViewById(R.id.tv_net);
        mPb = (ProgressBar) view.findViewById(R.id.pb_net_loading);
        return view;
    }

    @Override
    public void initData() {
        //加载数据并设置给view
        LogUtil.e("网络视屏数据初始化");
//        tv.setText("网络视屏");
        //加载数据
        setListenet();
        getDataFromNet();
    }

    private void setListenet() {
        swipeRefresh.setOnRefreshListener(new SwipeRefresh.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LogUtil.d("正在刷新");
                isRefresh = true;
                isLoading = false;
                new Thread(){
                    @Override
                    public void run() {
                        String data = getData(Constants.NET_URL);
                        refreshData = parseJson(data);
                        handler.sendEmptyMessage(1);
                    }
                }.start();
                swipeRefresh.finishRefresh();
            }

            @Override
            public void onLoading() {
                LogUtil.d("正在加载");
                isRefresh = false;
                isLoading = true;
                new Thread(){
                    @Override
                    public void run() {
                        String data = getData(Constants.NET_URL);
                        loadingData = parseJson(data);
                        handler.sendEmptyMessage(2);
                    }
                }.start();

            }
        });
    }

    private void getDataFromNet() {

        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                return getData(params[0]);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                if (!TextUtils.isEmpty(s)) {
                    //解析String 数据为json格式
                    videos = parseJson(s);
                    if (videos==null){
                        mTv.setVisibility(View.VISIBLE);
                        return;
                    }
                    //设置数据
                    mPb.setVisibility(View.INVISIBLE);
                    setData();
                }
            }
        }.execute(Constants.NET_URL);
    }

    /**
     * 为listView设置数据
     */
    private void setData() {
        if (isRefresh){
            //刷新的数据
            if (refreshData!=null) {
                if (videos==null){
                    videos = new ArrayList<>();
                }
                videos.addAll(refreshData);
                adapter.notifyDataSetChanged();
                swipeRefresh.hiddenLoadMore();
            }
        }else if (isLoading){
            //加载的数据
            if (loadingData!=null) {
                if (videos==null){
                    videos = new ArrayList<>();
                }
                videos.addAll(loadingData);
                adapter.notifyDataSetChanged();
                swipeRefresh.hiddenLoadMore();
            }
        }else {
            //第一次加载数据
            adapter = new MyVideoAdapter(context, videos, true, true);
            mNetVideoLv.setAdapter(adapter);
        }
        mPb.setVisibility(View.INVISIBLE);
        mTv.setVisibility(View.INVISIBLE);
    }

    private ArrayList<MyVideo> parseJson(String json) {
        if (json!=null) {
            ArrayList<MyVideo> Videos = new ArrayList<>();
            try {
                JSONObject jsonObject = new JSONObject(json);
                JSONArray jsonArray = jsonObject.optJSONArray("trailers");
                if (jsonArray != null && jsonArray.length() > 0) {

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObjectItem = (JSONObject) jsonArray.get(i);

                        if (jsonObjectItem != null) {
                            MyVideo mediaItem = new MyVideo();

                            String movieName = jsonObjectItem.optString("movieName");//name
                            mediaItem.setName(movieName);

                            String videoTitle = jsonObjectItem.optString("videoTitle");//desc
                            mediaItem.setDesc(videoTitle);

                            String imageUrl = jsonObjectItem.optString("coverImg");//imageUrl
                            mediaItem.setIvUri(imageUrl);

                            String hightUrl = jsonObjectItem.optString("hightUrl");//data
                            mediaItem.setData(hightUrl);

                            //把数据添加到集合
                            Videos.add(mediaItem);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return Videos;
        }else {
            return null;
        }
    }

        //将inputstream转化为string
    private String getData(String uri){
        InputStream is = null;
        StringBuilder result = new StringBuilder();
        try {
            is = new URL(uri).openStream();
            if (is!=null){
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line = "";
                while ((line = br.readLine())!= null){
                    result.append(line);
                }
            }else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }


    private class MyOnItemClickListenenr implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MyVideo video = videos.get(position);
            if (videos!=null&&videos.size()>0) {
                Intent intent = new Intent(context, SystemMediaPlayActivity.class);
                intent.putParcelableArrayListExtra("videos", videos);
                intent.putExtra("position", position);
                context.startActivity(intent);
            }
        }
    }
}

