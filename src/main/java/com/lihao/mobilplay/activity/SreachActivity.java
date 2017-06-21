package com.lihao.mobilplay.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.lihao.mobilplay.R;
import com.lihao.mobilplay.bean.SearchBean;
import com.lihao.mobilplay.tools.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class SreachActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SreachActivity";
    private EditText etInput;
    private ImageView ivVoice;
    private TextView tvSearch;
    private ListView listview;
    private ProgressBar progressBar;
    private TextView tvNodata;

    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
    private String url;
    private List<SearchBean.ItemData> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=58e8dbd9");
        setContentView(R.layout.activity_sreach);
        findViews();
        setListener();
    }

    private void setListener() {
        ivVoice.setOnClickListener(this);
        tvSearch.setOnClickListener(this);
    }

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2017-04-08 22:38:28 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        etInput = (EditText) findViewById(R.id.et_input);
        ivVoice = (ImageView) findViewById(R.id.iv_voice);
        tvSearch = (TextView) findViewById(R.id.tv_search);
        listview = (ListView) findViewById(R.id.listview);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tvNodata = (TextView) findViewById(R.id.tv_nodata);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_search:
                //搜索

                break;
            case R.id.iv_voice:
                //点击输入语音
                inputVoice();
                break;
        }
    }

    private void inputVoice1(){
        //1.创建RecognizerDialog对象
        RecognizerDialog mDialog = new RecognizerDialog(this, new MyInitListener());
        //2.设置accent、 language等参数
        mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");//中文
        mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");//普通话
        //若要将UI控件用于语义理解，必须添加以下参数设置，设置之后onResult回调返回将是语义理解
        //结果
        mDialog.setParameter("asr_sch", "1");
        mDialog.setParameter("nlp_version", "2.0");
        //3.设置回调接口
        mDialog.setListener(new MyRecognizerDialogListener());
        //4.显示dialog，接收语音输入
        mDialog.show();
    }

    private void inputVoice() {

        //1.创建SpeechRecognizer对象，第二个参数：本地听写时传InitListener
        SpeechRecognizer mIat = SpeechRecognizer.createRecognizer(SreachActivity.this, null);
//2.设置听写参数，详见《科大讯飞MSC API手册(Android)》SpeechConstant类
        mIat.setParameter(SpeechConstant.DOMAIN, "iat");
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin ");
//3.开始听写
        mIat.startListening(new MyRecognizerListener());
    }

    class MyRecognizerListener implements RecognizerListener {

        //听写结果回调接口(返回Json格式结果，用户可参见附录12.1)；
//一般情况下会通过onResults接口多次返回结果，完整的识别内容是多次结果的累加；
//关于解析Json的代码可参见MscDemo中JsonParser类；
//isLast等于true时会话结束。
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.d("Result:", results.getResultString());
            //解析声音
            String result = results.getResultString();
            Log.e("MainActivity", "result ==" + result);
            String text = JsonParser.parseIatResult(result);
            //解析好的
            Log.e("MainActivity", "text ==" + text);

            String sn = null;
            // 读取json结果中的sn字段
            try {
                JSONObject resultJson = new JSONObject(results.getResultString());
                sn = resultJson.optString("sn");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mIatResults.put(sn, text);

            StringBuffer resultBuffer = new StringBuffer();//拼成一句
            for (String key : mIatResults.keySet()) {
                resultBuffer.append(mIatResults.get(key));
            }

            etInput.setText(resultBuffer.toString());
            etInput.setSelection(etInput.length());
        }

        //会话发生错误回调接口
        public void onError(SpeechError error) {
            error.getPlainDescription(true);
        }//获取错误码描述

        @Override
        public void onVolumeChanged(int i, byte[] bytes) {

        }

        //开始录音
        public void onBeginOfSpeech() {
            Log.i(TAG, "onEndOfSpeech: "+"开始录音");

        }

        //结束录音
        public void onEndOfSpeech() {
            Log.i(TAG, "onEndOfSpeech: "+"结束录音");
        }

        //扩展用接口
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
        }
    }


    class MyRecognizerDialogListener implements RecognizerDialogListener{

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {

            //解析声音
            String result = recognizerResult.getResultString();
            Log.e("MainActivity", "result ==" + result);
            String text = JsonParser.parseIatResult(result);
            //解析好的
            Log.e("MainActivity", "text ==" + text);

            String sn = null;
            // 读取json结果中的sn字段
            try {
                JSONObject resultJson = new JSONObject(recognizerResult.getResultString());
                sn = resultJson.optString("sn");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mIatResults.put(sn, text);

            StringBuffer resultBuffer = new StringBuffer();//拼成一句
            for (String key : mIatResults.keySet()) {
                resultBuffer.append(mIatResults.get(key));
            }

            etInput.setText(resultBuffer.toString());
            etInput.setSelection(etInput.length());
        }

        @Override
        public void onError(SpeechError speechError) {
             Log.i(TAG,speechError.getMessage());
        }
    }

    private class MyInitListener implements InitListener {
        @Override
        public void onInit(int i) {
            if (i != ErrorCode.SUCCESS) {
                Toast.makeText(SreachActivity.this, "初始化失败", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
