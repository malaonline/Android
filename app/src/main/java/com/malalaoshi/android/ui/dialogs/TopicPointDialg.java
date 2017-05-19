package com.malalaoshi.android.ui.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.core.utils.MiscUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by donald on 2017/5/18.
 */

public class TopicPointDialg extends DialogFragment {

    @Bind(R.id.iv_topic_dialog_close)
    ImageView mIvTopicDialgoClose;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_topic_point, container);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window;
        if (getDialog() != null) {
            window = getDialog().getWindow();
        } else {
            window = getActivity().getWindow();
        }
        int width = (int) (Math.min(MiscUtil.getDisplayMetrics().widthPixels, MiscUtil.getDisplayMetrics().heightPixels) * 0.9);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = width;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(layoutParams);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.iv_topic_dialog_close)
    public void onViewClicked() {
        dismiss();
    }
}
