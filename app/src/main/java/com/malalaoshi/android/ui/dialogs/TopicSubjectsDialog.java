package com.malalaoshi.android.ui.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.core.utils.MiscUtil;
import com.malalaoshi.android.entity.TopicSubject;
import com.malalaoshi.android.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by donald on 2017/5/17.
 */

public class TopicSubjectsDialog extends DialogFragment {

    @Bind(R.id.tv_dialog_item_first)
    TextView mTvDialogItemFirst;
    @Bind(R.id.tv_dialog_item_second)
    TextView mTvDialogItemSecond;
    private OnItemClickListener mListener;
    private ArrayList<TextView> mItems;
    private List<TopicSubject> mSubjects;
    private int mPosition;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog_topic_subjects, container);
        ButterKnife.bind(this, view);
        initData();
        return view;
    }

    private void initData() {
        mItems = new ArrayList<>();
        mItems.add(mTvDialogItemFirst);
        mItems.add(mTvDialogItemSecond);
        if (mItems != null && mSubjects != null && mSubjects.size() > 0){
            for (int i = 0; i < mSubjects.size(); i++) {
                TopicSubject topicSubject = mSubjects.get(i);
                TextView textView = mItems.get(i);
                textView.setSelected(false);
                textView.setText(topicSubject.getSubject() + " "+topicSubject.getTopicNum());
            }
        }
        if (mItems != null && mPosition < mItems.size())
            mItems.get(mPosition).setSelected(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window;
        if (getDialog() != null) {
            window = getDialog().getWindow();
        } else {
            window = getActivity().getWindow();
        }
        if (window != null) {
            int width = (int) (Math.min(MiscUtil.getDisplayMetrics().widthPixels, MiscUtil.getDisplayMetrics().heightPixels) * 0.7);
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.width = width;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.gravity = Gravity.TOP;
            layoutParams.y = MiscUtil.dp2px(88);
            window.setAttributes(layoutParams);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.tv_dialog_item_first, R.id.tv_dialog_item_second})
    public void onViewClicked(View view) {
        if (mListener == null) return;
        switch (view.getId()) {
            case R.id.tv_dialog_item_first:
                mListener.itemClick(0);
                if (mPosition != 0){
                    mTvDialogItemFirst.setSelected(true);
                    mTvDialogItemSecond.setSelected(false);
                    mPosition = 0;
                }
                dismiss();
                break;
            case R.id.tv_dialog_item_second:
                mListener.itemClick(1);
                if (mPosition != 1){
                    mTvDialogItemFirst.setSelected(false);
                    mTvDialogItemSecond.setSelected(true);
                    mPosition = 1;
                }
                dismiss();
                break;
        }
    }

    public void setOnItemClickListener(OnItemClickListener clickListener){
        mListener = clickListener;
    }
    public void setData(List<TopicSubject> subjects,int selectedPosition){
        mSubjects = subjects;
        mPosition = selectedPosition;
    }
}
