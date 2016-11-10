package com.malalaoshi.android.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malalaoshi.android.R;
import com.malalaoshi.android.core.base.BaseFragment;

import butterknife.ButterKnife;

/**
 * Created by kang on 16/11/10.
 */

public class LiveCourseIntroduceFragment extends BaseFragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_live_course_introduce, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public String getStatName() {
        return "双师直播介绍";
    }
}
