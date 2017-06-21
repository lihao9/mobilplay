package com.lihao.mobilplay.page;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lihao.mobilplay.R;
import com.lihao.mobilplay.adapter.MyNetAudioAdapter;
import com.lihao.mobilplay.base.BasePage;
import com.lihao.mobilplay.bean.AudioData;
import com.lihao.mobilplay.tools.Constants;
import com.lihao.mobilplay.tools.LogUtil;
import com.powyin.scroll.widget.SwipeControl;
import com.powyin.scroll.widget.SwipeRefresh;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by hbm on 2017/3/27.
 */

public class NetAudioPage extends BasePage {
    private View view;
    private String data;

    private ListView mNetAideoLv;
    private SwipeRefresh swipeRefresh;
    private ProgressBar mPb;
    private TextView mTv;

    public NetAudioPage(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        LogUtil.e("网络音频视图初始化");
        view = View.inflate(context, R.layout.layout_net_audio,null);
        swipeRefresh = (SwipeRefresh) view.findViewById(R.id.re_audio);
        swipeRefresh.setSwipeModel(SwipeControl.SwipeModel.SWIPE_BOTH);
        mNetAideoLv = (ListView) view.findViewById(R.id.lv_net_audio);
//        mNetAideoLv.setOnItemClickListener(new MyOnItemClickListenenr());
        mTv = (TextView) view.findViewById(R.id.tv_net_audio);
        mPb = (ProgressBar) view.findViewById(R.id.pb_net_loading_audio);
        return view;
    }

    @Override
    public void initData() {
        //加载数据并设置给view
        LogUtil.e("网络音频数据初始化");
        //1.创建okHttpclient实例
        final OkHttpClient client = new OkHttpClient();
        //2.拿到request对象。
        Request.Builder builder = new Request.Builder();
        final Request request = builder.get().url(Constants.NET_AUDIO_URL).build();
        //3.拿到call对象
        final Call call = client.newCall(request);
        //4.执行call
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("info", "onFailure:  失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                data = response.body().string();
                LogUtil.e("数据"+data);
                if (data!=null&&!"".equals(data)){
                    //解析数据
                    analysisData(data);
                }
                Activity a = (Activity) context;
                a.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context.getApplicationContext(), "请求成功", Toast.LENGTH_SHORT).show();
                        if (data!=null&&!"".equals(data)){
                            mTv.setVisibility(View.GONE);
                        }else {
                            mTv.setVisibility(View.VISIBLE);
                        }
                        mPb.setVisibility(View.GONE);
                        if(datas != null && datas.size() >0 ){
                            //有数据
                            //tv_nonet.setVisibility(View.GONE);
                            //设置适配器
                            adapter = new MyNetAudioAdapter(context,datas);
                            mNetAideoLv.setAdapter(adapter);
                        }
                    }
                });

                Log.i("info", "onResponse: 成功"+data);
            }
        });
    }


    private List<AudioData.ListBean> datas;
    private AudioData mAudioData;
    private MyNetAudioAdapter adapter;
    private void analysisData(String data) {
        mAudioData = parseData(data);
        datas = mAudioData.getList();
    }

    private AudioData parseData(String data){
        return new Gson().fromJson(data,AudioData.class);
    }

}
