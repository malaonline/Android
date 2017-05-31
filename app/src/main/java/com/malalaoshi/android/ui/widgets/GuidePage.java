package com.malalaoshi.android.ui.widgets;

import android.app.Activity;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by donald on 2017/5/31.
 */

public class GuidePage {
    private Activity mActivity;
    private int mLayoutId;
    private int mKnowViewId;
    private String mPageTag;
    private boolean mCancel;
    private FrameLayout mRootLayout;
    private View mLayoutView;


    protected GuidePage() {
    }
    public static class Builder{
        private GuidePage mGuidePage = new GuidePage();

        public Builder(Activity activity) {
            mGuidePage.mActivity = activity;
        }

        /**
         * 引导布局文件
         * @param layoutId
         * @return
         */
        public Builder setLayoutId(@LayoutRes int layoutId){
            mGuidePage.mLayoutId = layoutId;
            return this;
        }

        /**
         * 可点击的View
         * @param knowViewID
         * @return
         */
        public Builder setKnowViewId(@IdRes int knowViewID){
            mGuidePage.mKnowViewId = knowViewID;
            return this;
        }

        /**
         * 引导页唯一标示
         * @param tag
         * @return
         */
        public Builder setPageTag(String tag){
            mGuidePage.mPageTag = tag;
            return this;
        }

        public Builder setCloseOnTouchOutside(boolean cancel){
            mGuidePage.mCancel = cancel;
            return this;
        }

        public GuidePage builder(){
            if (TextUtils.isEmpty(mGuidePage.mPageTag)){
                throw new RuntimeException("必须设置功能引导TAG");
            }
            mGuidePage.setLayoutView();
            mGuidePage.setKnowEvent();
            mGuidePage.setCloseOnTouchOutside();
            return mGuidePage;
        }
    }

    private void setCloseOnTouchOutside() {
        if (mLayoutView != null){
            mLayoutView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (mCancel){
                        cancel();
                    }
                    return true;
                }
            });
        }
    }

    public void setKnowEvent() {
        if (mLayoutView != null){
            mLayoutView.findViewById(mKnowViewId).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancel();
                }
            });
        }
    }

    /**
     * 取消显示
     */
    public void cancel() {
        if (mRootLayout != null && mLayoutView != null){
            mRootLayout.removeView(mLayoutView);
            GuidePageManager.setHasShowGuidePage(mActivity, mPageTag, true);
        }
    }

    public void setLayoutView() {
        mRootLayout = (FrameLayout) mActivity.findViewById(android.R.id.content);
        mLayoutView = View.inflate(mActivity, mLayoutId, null);
    }
    public void apply(){
        if (!GuidePageManager.hasNotShowed(mActivity, mPageTag)){
            return;
        }
        mRootLayout.addView(mLayoutView);
    }
}
