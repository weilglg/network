package com.dragon.finder;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.view.annotation.BindView;
import com.view.api.ViewInjector;

/**
 * Created by wll on 2017/9/8.
 */

public class MainFragment extends Fragment {

    @BindView(R.id.fragment_tv_one)
    TextView fragment_tv_one;
    @BindView(R.id.fragment_tv_two)
    TextView fragment_tv_two;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ViewInjector.injectView(this, view);
        return view;
    }
}
