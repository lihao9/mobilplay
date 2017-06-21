package com.lihao.mobilplay.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioGroup;

import com.lihao.mobilplay.R;
import com.lihao.mobilplay.base.BasePage;
import com.lihao.mobilplay.fragment.MyFragment;
import com.lihao.mobilplay.page.AudioPage;
import com.lihao.mobilplay.page.NetAudioPage;
import com.lihao.mobilplay.page.NetVideoPage;
import com.lihao.mobilplay.page.VideoPage;
import com.lihao.mobilplay.tools.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private int mNo;
    private RadioGroup mRadioGroup;
    private List<BasePage> pages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState!=null){
            mNo = savedInstanceState.getInt("no");
        }
        init();
        mRadioGroup.check(R.id.rb_video);
    }

    private void init() {
        pages = new ArrayList<>();
        pages.add(new VideoPage(this));
        pages.add(new AudioPage(this));
        pages.add(new NetVideoPage(this));
        pages.add(new NetAudioPage(this));
        LogUtil.e(pages.size()+"");
        mRadioGroup = (RadioGroup) findViewById(R.id.main_rg);
        mRadioGroup.setOnCheckedChangeListener(new MyOnCheckhCangeListerner());
    }



    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt("no",mNo);
    }

    //radiogroup 的点击状态改变事件
    class MyOnCheckhCangeListerner implements RadioGroup.OnCheckedChangeListener{

        /**
         * <p>Called when the checked radio button has changed. When the
         * selection is cleared, checkedId is -1.</p>
         *
         * @param group     the group in which the checked radio button has changed
         * @param checkedId the unique identifier of the newly checked radio button
         */
        @Override
        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
            switch (checkedId){
                case R.id.rb_video:
                    mNo = 0;
                    break;
                case R.id.net_video:
                    mNo = 1;
                    break;
                case R.id.rb_audio:
                    mNo = 2;
                    break;
                case R.id.net_audio:
                    mNo = 3;
                    break;
            }
            setFragement();
        }
    }

    private void setFragement() {
        //1.获取FragmentManager
        FragmentManager fmManager = getSupportFragmentManager();
        //2.开启事务
        FragmentTransaction fmTransaction = fmManager.beginTransaction();
        //3.执行操作
        fmTransaction.replace(R.id.main_fl,new MyFragment(getBasePage()));
        //4.提交
        fmTransaction.commit();

    }

    private BasePage getBasePage() {
        BasePage basePage = pages.get(mNo);
        if (basePage!=null&&!basePage.isInitDate){
            basePage.initData();
            basePage.isInitDate = true;
        }
        return basePage;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
