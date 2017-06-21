package com.lihao.mobilplay.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lihao.mobilplay.base.BasePage;

/**
 * Created by hbm on 2017/3/27.
 */

public class MyFragment extends Fragment {
    private BasePage page;
    public MyFragment(){}
    @SuppressLint({"NewApi", "ValidFragment"})
    public MyFragment(BasePage page){
        this.page = page;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return page.viewRoot;
    }
}
