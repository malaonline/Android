package com.malalaoshi.android.core.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.PtrUIHandler;
import com.chanven.lib.cptr.indicator.PtrIndicator;
import com.malalaoshi.android.core.R;

//import static com.google.gson.jpush.a.a.z.R;

/**
 * 下拉刷新动效
 * Created by donald on 2017/4/17.
 */

public class RefreshHeaderEffectView extends FrameLayout implements PtrUIHandler{

    private Status status;

    enum Status {
        RESET,
        REFRESH_PREPARE,
        REFRESHING,
        FINISHED,
    }

    ImageView mIvRefreshProgress;
    ImageView mIvRefreshLeftEye;
    ImageView mIvRefreshRightEye;
    private Animation mProgressAnim;
    private Animation mLeftAnim;
    private Animation mRightAnim;

    public RefreshHeaderEffectView(Context context) {
        super(context);
        setupView(context);
    }


    public RefreshHeaderEffectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupView(context);
    }

    public RefreshHeaderEffectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupView(context);
    }

//    public RefreshEffectView(Context mContext, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(mContext, attrs, defStyleAttr, defStyleRes);
//        setupView(mContext);
//    }


    private void setupView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_refresh_effect, this);
        mIvRefreshProgress = (ImageView) view.findViewById(R.id.iv_refresh_progress);
        mIvRefreshLeftEye = (ImageView) view.findViewById(R.id.iv_refresh_left_eye);
        mIvRefreshRightEye = (ImageView) view.findViewById(R.id.iv_refresh_right_eye);
        mProgressAnim = AnimationUtils.loadAnimation(context, R.anim.refresh_rotate);
        mLeftAnim = AnimationUtils.loadAnimation(context, R.anim.refresh_rotate);
        mRightAnim = AnimationUtils.loadAnimation(context, R.anim.refresh_rotate);
    }
    public void startAnim(){
        mIvRefreshProgress.startAnimation(mProgressAnim);
        mIvRefreshLeftEye.startAnimation(mLeftAnim);
        mIvRefreshRightEye.startAnimation(mRightAnim);
    }
    public void stopAnim(){
        mIvRefreshProgress.clearAnimation();
        mIvRefreshLeftEye.clearAnimation();
        mIvRefreshRightEye.clearAnimation();
    }

    public void setLayout(Status status){
        this.status = status;
        switch (status){
            case RESET:
                break;
            case REFRESH_PREPARE:
                break;
            case REFRESHING:
                startAnim();
                break;
            case FINISHED:
                stopAnim();
                break;
        }
    }

    @Override
    public void onUIReset(PtrFrameLayout frame) {
        setLayout(Status.RESET);
    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {
        setLayout(Status.REFRESH_PREPARE);
    }

    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {
        setLayout(Status.REFRESHING);
    }

    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {
        setLayout(Status.FINISHED);
    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {

    }
}
