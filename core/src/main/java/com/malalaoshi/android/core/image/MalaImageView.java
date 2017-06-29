package com.malalaoshi.android.core.image;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.malalaoshi.android.core.image.glide.GlideUtils;
import com.malalaoshi.android.core.utils.MiscUtil;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * 麻辣ImageView
 * Created by tianwei on 8/21/16.
 */
public class MalaImageView extends AppCompatImageView {

    public MalaImageView(Context context) {
        super(context);
    }

    public MalaImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MalaImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 普通加载
     */
    public void loadImage(String url) {
        GlideUtils.loadImage(getContext(), url, this);
    }

    /**
     * 普通加载
     */
    public void loadImage(String url, int defImage) {
        GlideUtils.loadImage(getContext(), url, this, defImage);
    }

    /**
     * 圆图
     */
    public void loadCircleImage(String url, int defImage) {
        GlideUtils.loadCircleImage(getContext(), url, this, defImage);
    }

    public void loadBlurImage(String url, int defImage) {
        GlideUtils.loadBlurImage(getContext(), url, this, defImage);
    }
    public void loadCircleStrokeImage(String url, int defImage){
        if (TextUtils.isEmpty(url)){
            loadCircleImage(url,defImage);
        }else
            GlideUtils.loadCircleStrokeImage(getContext(), url, this);
    }
    public void loadCircleStrokeImage(String url, int defImage, @ColorRes int borderColor, int borderWidth){
        if (TextUtils.isEmpty(url)){
            loadCircleImage(url,defImage);
        }else
            GlideUtils.loadCircleStrokeImage(getContext(), url, this, borderWidth, borderColor);
    }
    public void loadRoundedImage(String url){
        GlideUtils.loadCustomImage(getContext(), url, this, new RoundedCornersTransformation(getContext(), MiscUtil.dp2px(6), 0));
    }
}
