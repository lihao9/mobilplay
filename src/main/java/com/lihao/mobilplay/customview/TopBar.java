package com.lihao.mobilplay.customview;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lihao.mobilplay.R;
import com.lihao.mobilplay.activity.SreachActivity;

/**
 * Created by hbm on 2017/3/27.
 */

public class TopBar extends LinearLayout implements View.OnClickListener {
    private View mTextSearch;
    private View mImageGame;
    private View mImageHistory;
    private Context context;

    public TopBar(Context context) {
        this(context,null);
    }

    public TopBar(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TopBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTextSearch = getChildAt(1);
        mImageGame = getChildAt(2);
        mImageHistory = getChildAt(3);
        mTextSearch.setOnClickListener(this);
        mImageHistory.setOnClickListener(this);
        mImageGame.setOnClickListener(this);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.main_topbar_tv_search:
                Toast.makeText(getContext(),"搜索",Toast.LENGTH_SHORT).show();
                //进入搜索页面
                Intent intent = new Intent(getContext(), SreachActivity.class);
                context.startActivity(intent);
                break;
            case R.id.main_topbar_game:
                break;
            case R.id.main_topbar_history:
                break;
        }
    }
}
