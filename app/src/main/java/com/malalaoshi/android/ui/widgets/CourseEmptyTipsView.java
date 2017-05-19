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
import com.malalaoshi.android.core.view.ShadowHelper;


/**
 * Created by donald on 2017/4/21.
 */

public class CourseEmptyTipsView extends FrameLayout implements View.OnClickListener {

    private FrameLayout mFlCourseEmpty;
    private TextView mTvCourseSign;
    private boolean hasLayout =false;

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
        final View view = View.inflate(context, R.layout.view_course_empty_tips, this);
        mTvLoginSign = (TextView) view.findViewById(R.id.tv_course_login);
        mTvCourseSign = (TextView) view.findViewById(R.id.tv_course_sign);
        mFlCourseEmpty = (FrameLayout) view.findViewById(R.id.fl_course_empty);
        mTvLoginSign.setOnClickListener(this);
        mTvCourseSign.setOnClickListener(this);
        initShadow(context);
    }

    private void initShadow(Context context) {
        ShadowHelper.setDrawShadow(context, mFlCourseEmpty);
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
    public void setLoginShadowVisibility(boolean visibility){
//        if (visibility){
//            mLoginBtnShadow.show();
//        }else
//            mLoginBtnShadow.hide();
    }
    public void setSignShadowVisibility(boolean visibility){
//        if (visibility)
//            mSignBtnShadow.show();
//        else
//            mSignBtnShadow.hide();
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) mListener.onClick(v);
    }
    public void setOnClickListener(OnClickListener listener){
        this.mListener = listener;
    }
    public interface OnClickListener{
        void onClick(View view);
    }
    public void setBtnType(BtnType type){
        switch (type){
            case LOGIN:
                mTvLoginSign.setVisibility(VISIBLE);
                mTvCourseSign.setVisibility(GONE);
                //                if (mLoginBtnShadow != null)
                //                    mLoginBtnShadow.show();
//                if (mSignBtnShadow != null){
//                    mSignBtnShadow.hide();
//                }
                break;
            case SIGN:
                mTvLoginSign.setVisibility(GONE);
                mTvCourseSign.setVisibility(VISIBLE);
                //                if (mSignBtnShadow != null)
                //                    mSignBtnShadow.show();
//                if (mLoginBtnShadow != null)
//                    mLoginBtnShadow.hide();
                break;
            case NO_COMMIT:
                break;
        }
    }
}
