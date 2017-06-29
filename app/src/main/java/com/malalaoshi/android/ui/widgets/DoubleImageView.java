package com.malalaoshi.android.ui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.core.utils.MiscUtil;

/**
 * Created by donald on 2017/6/28.
 */

public class DoubleImageView extends FrameLayout {
    public static final int LOAD_SIGNLE_BIG = 547;
    public static final int LOAD_DOUBLE = 781;
    private int mBigImgSize;
    private int mSmallImgSize;
    private int mBigBorderWidth;
    private int mSmllBorderWidth;
    private int mBigBorderColor;
    private int mSmallBorderColor;
    private CircleImageView mTeacherAvatar;
    private CircleImageView mAssistantAvatar;

    public DoubleImageView(@NonNull Context context) {
        this(context, null);
    }

    public DoubleImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DoubleImageView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DoubleImageView, defStyleAttr, 0);
        mBigImgSize = typedArray.getDimensionPixelOffset(R.styleable.DoubleImageView_div_bigImgSize, 0);
        mSmallImgSize = typedArray.getDimensionPixelOffset(R.styleable.DoubleImageView_div_smallImgSize, 0);
        mBigBorderWidth = typedArray.getDimensionPixelOffset(R.styleable.DoubleImageView_div_bigBorderWidth, 0);
        mSmllBorderWidth = typedArray.getDimensionPixelOffset(R.styleable.DoubleImageView_div_smallBorderWidth, 0);
        mBigBorderColor = typedArray.getColor(R.styleable.DoubleImageView_div_bigBorderColor, MiscUtil.getColor(R.color.main_color));
        mSmallBorderColor = typedArray.getColor(R.styleable.DoubleImageView_div_smallBorderColor, MiscUtil.getColor(R.color.main_color));
        typedArray.recycle();
        initView(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mBigImgSize + mSmallImgSize - MiscUtil.dp2px(6), mBigImgSize);
    }

    private void initView(Context context) {
        mTeacherAvatar = new CircleImageView(context);
        LayoutParams bigLayoutParams = new LayoutParams(mBigImgSize, mBigImgSize);
        mTeacherAvatar.setLayoutParams(bigLayoutParams);
        mTeacherAvatar.setBorderColor(mBigBorderColor);
        mTeacherAvatar.setBorderWidth(mBigBorderWidth);
        mTeacherAvatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mAssistantAvatar = new CircleImageView(context);
        LayoutParams smallLayoutParams = new LayoutParams(mSmallImgSize, mSmallImgSize);
        smallLayoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        mAssistantAvatar.setLayoutParams(smallLayoutParams);
        mAssistantAvatar.setBorderColor(mSmallBorderColor);
        mAssistantAvatar.setBorderWidth(mSmllBorderWidth);
        mAssistantAvatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
        addView(mTeacherAvatar);
        addView(mAssistantAvatar);
    }

    public void loadImg(String url1, String url2, int mode) {
        if (TextUtils.isEmpty(url1)) {
            mTeacherAvatar.setBorderWidth(0);
        }
        if (mode == LOAD_SIGNLE_BIG) {
            mAssistantAvatar.setVisibility(GONE);
            FrameLayout.LayoutParams layoutParams = (LayoutParams) mTeacherAvatar.getLayoutParams();
            if (layoutParams == null) {
                layoutParams = new LayoutParams(mBigImgSize, mBigImgSize);
            }
            layoutParams.gravity = Gravity.CENTER;
            mTeacherAvatar.setLayoutParams(layoutParams);
            mTeacherAvatar.loadImage(url1, R.drawable.ic_default_avatar);
        } else if (mode == LOAD_DOUBLE) {
            mAssistantAvatar.setVisibility(VISIBLE);
            if (TextUtils.isEmpty(url2)) {
                mAssistantAvatar.setBorderWidth(0);
            }
            FrameLayout.LayoutParams layoutParams = (LayoutParams) mTeacherAvatar.getLayoutParams();
            if (layoutParams == null) {
                layoutParams = new LayoutParams(mBigImgSize, mBigImgSize);
            }
            layoutParams.gravity = Gravity.LEFT;
            mTeacherAvatar.setLayoutParams(layoutParams);
            mTeacherAvatar.loadImage(url1, R.drawable.ic_default_avatar);
            mAssistantAvatar.loadImage(url2);
        }
    }

    public void setBigImgSize(int bigImgSize) {
        mBigImgSize = bigImgSize;
    }

    public void setSmallImgSize(int smallImgSize) {
        mSmallImgSize = smallImgSize;
    }

    public void setBigBorderWidth(int bigBorderWidth) {
        mBigBorderWidth = bigBorderWidth;
    }

    public void setSmllBorderWidth(int smllBorderWidth) {
        mSmllBorderWidth = smllBorderWidth;
    }

    public void setBigBorderColor(int bigBorderColor) {
        mBigBorderColor = bigBorderColor;
    }

    public void setSmallBorderColor(int smallBorderColor) {
        mSmallBorderColor = smallBorderColor;
    }
}
