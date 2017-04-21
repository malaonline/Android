package com.malalaoshi.android.core.view;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.chanven.lib.cptr.loadmore.ILoadMoreViewFactory;
import com.malalaoshi.android.core.MalaContext;
import com.malalaoshi.android.core.R;

/**
 * Created by donald on 2017/4/20.
 */

public class RefreshFooterEffectView implements ILoadMoreViewFactory {

    private final Context mContext;
    private final LoadMoreHandler mLoadMoreHandler;

    public RefreshFooterEffectView(Context context) {
        mContext = context;
        mLoadMoreHandler = new LoadMoreHandler();
    }

    private class LoadMoreHandler implements ILoadMoreView {
        private TextView mTvLoadMore;
        private ImageView mIvLoadMorePlanet;
        private ImageView mIvLoadMoreProjection;
        private ImageView mIvLoadMoreLion;
        private FrameLayout mFlLoadMorePlanet;
        private FrameLayout mFlLoadMoreEffect;
        private View mFootView;
        private View.OnClickListener mLoadMoreListener;
        private Animation mLionDownAnim;
        private Animation mLionRevertAnim;
        private Animation mLionShrinkAnim;
        private Animation mShadowShowAnim;
        private Animation mPlanetShowAnim;
        private Animation mTextShowAnim;

        @Override
        public void init(FootViewAdder footViewHolder, View.OnClickListener onClickLoadMoreListener) {
            mFootView = footViewHolder.addFootView(R.layout.core__view_refresh_foot);
            mTvLoadMore = (TextView) mFootView.findViewById(R.id.tv_load_more);
            mIvLoadMorePlanet = (ImageView) mFootView.findViewById(R.id.iv_load_more_planet);
            mIvLoadMoreProjection = (ImageView) mFootView.findViewById(R.id.iv_load_more_projection);
            mFlLoadMorePlanet = (FrameLayout) mFootView.findViewById(R.id.fl_load_more_planet);
            mIvLoadMoreLion = (ImageView) mFootView.findViewById(R.id.iv_load_more_lion);
            mFlLoadMoreEffect = (FrameLayout) mFootView.findViewById(R.id.fl_load_more_effect);
            mLoadMoreListener = onClickLoadMoreListener;
            initAnimation();
            showNormal();
//            startAnimation();
        }

        private void initAnimation() {
            Context context = MalaContext.getContext();
            mLionDownAnim = AnimationUtils.loadAnimation(context, R.anim.core__lion_down_to_show);
            mLionRevertAnim = AnimationUtils.loadAnimation(context, R.anim.core__lion_revert);
            mLionShrinkAnim = AnimationUtils.loadAnimation(context, R.anim.core__lion_shrink);
            mShadowShowAnim = AnimationUtils.loadAnimation(context, R.anim.core__shadow_show);
            mPlanetShowAnim = AnimationUtils.loadAnimation(context, R.anim.core__planet_up_to_show);
            mTextShowAnim = AnimationUtils.loadAnimation(context, R.anim.core__text_alphoa_show);
            Log.e("LoadMoreHandler", "initAnimation: "+mLionDownAnim);

            mLionDownAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    Log.e("LoadMoreHandler", "onAnimationStart: ");
                    mFlLoadMoreEffect.setVisibility(View.VISIBLE);

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mTvLoadMore.startAnimation(mTextShowAnim);
                    mIvLoadMoreLion.startAnimation(mLionShrinkAnim);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            mLionShrinkAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mIvLoadMoreLion.startAnimation(mLionRevertAnim);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
        public void startAnimation(){
            Log.e("LoadMoreHandler", "startAnimation: ");
            mIvLoadMoreLion.startAnimation(mLionDownAnim);
            mFlLoadMorePlanet.startAnimation(mPlanetShowAnim);
            mIvLoadMoreProjection.startAnimation(mShadowShowAnim);
        }
        public void stopAnimation(){

        }

        @Override
        public void showNormal() {
            Log.e("LoadMoreHandler", "showNormal: ");
            mTvLoadMore.setText(R.string.click_load_more);
            mFlLoadMoreEffect.setVisibility(View.GONE);
            mFootView.setOnClickListener(mLoadMoreListener);
        }

        @Override
        public void showLoading() {
            Log.e("LoadMoreHandler", "showLoading: ");
            mTvLoadMore.setText(R.string.loading_more);
            mFlLoadMoreEffect.setVisibility(View.GONE);
        }

        @Override
        public void showFail(Exception e) {
            mTvLoadMore.setText(R.string.load_error_retry);
            mFlLoadMoreEffect.setVisibility(View.GONE);
        }

        @Override
        public void showNomore() {
            Log.e("LoadMoreHandler", "showNomore: ");
            mTvLoadMore.setText(R.string.no_more_content);
            mTvLoadMore.startAnimation(mLionDownAnim);

            mFlLoadMoreEffect.setVisibility(View.VISIBLE);
//            startAnimation();

        }

        @Override
        public void setFooterVisibility(boolean isVisible) {
            mFootView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public ILoadMoreView madeLoadMoreView() {
        return mLoadMoreHandler;
    }
    public void click(){
        mLoadMoreHandler.startAnimation();
    }
}
