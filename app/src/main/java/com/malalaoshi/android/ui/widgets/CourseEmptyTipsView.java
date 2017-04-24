package com.malalaoshi.android.ui.widgets;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.AttrRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.malalaoshi.android.R;


/**
 * Created by donald on 2017/4/21.
 */

public class CourseEmptyTipsView extends FrameLayout implements View.OnClickListener {

    public enum BtnType{
        LOGIN,
        SIGN,
        NO_COMMIT
    }

    private TextView mTvLoginSign;
    private OnClickListener mListener;

    public CourseEmptyTipsView(@NonNull Context context) {
        this(context,null);
    }

    public CourseEmptyTipsView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CourseEmptyTipsView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = View.inflate(context, R.layout.view_course_empty_tips, this);
        mTvLoginSign = (TextView) view.findViewById(R.id.tv_login_sign);
        mTvLoginSign.setOnClickListener(this);
    }
    public void setBtnText(String text){
        mTvLoginSign.setText(text);
    }
    public void setBtnText(@StringRes int textId){
        mTvLoginSign.setText(textId);
    }
    public void setBtnBackground(Drawable drawable){
        mTvLoginSign.setBackground(drawable);
    }
    public void setBtnBackground(@DrawableRes int backgroundId){
        mTvLoginSign.setBackgroundResource(backgroundId);
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) mListener.onClick();
    }
    public void setOnClickListener(OnClickListener listener){
        this.mListener = listener;
    }
    public interface OnClickListener{
        void onClick();
    }
    public void setBtnType(BtnType type){
        switch (type){
            case LOGIN:
                setBtnText(R.string.login_immediately);
                setBtnBackground(R.drawable.selector_gradient_semicircle_blue_btn_bg);
                break;
            case SIGN:
                setBtnText(R.string.sign_immediately);
                setBtnBackground(R.drawable.selector_gradient_semicircle_red_btn_bg);
                break;
            case NO_COMMIT:
                break;
        }
    }
}
