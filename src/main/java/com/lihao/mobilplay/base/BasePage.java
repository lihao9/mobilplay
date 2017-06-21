package com.lihao.mobilplay.base;

import android.content.Context;
import android.view.View;

/**
 * Created by hbm on 2017/3/27.
 */

public abstract class BasePage{
    public View viewRoot;
    public Context context;
    public boolean isInitDate;

    public BasePage(Context context){
        this.context = context;
        viewRoot = initView();
    }

    //页面初始化view
    public abstract View initView();

    //加载数据调用
    public void initData(){

    }

}
