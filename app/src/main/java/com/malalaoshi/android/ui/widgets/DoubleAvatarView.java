package com.malalaoshi.android.ui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.malalaoshi.android.R;
import com.malalaoshi.android.core.image.MalaImageView;

/**
 * Created by kang on 16/10/28.
 */

public class DoubleAvatarView extends RelativeLayout {
    private android.widget.ImageView ivbgleft;
    private android.widget.ImageView ivbgright;
    private com.malalaoshi.android.core.image.MalaImageView ivleftavator;
    private com.malalaoshi.android.core.image.MalaImageView ivrightavator;

    private boolean isShowRing = true;
    private int avatar_margin = 0;

    public DoubleAvatarView(Context context) {
        this(context,null);
    }

    public DoubleAvatarView(Context context, AttributeSet attrs) {
        this(context,attrs,-1);
    }

    public DoubleAvatarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.d("DoubleAvatarView", "DoubleAvatarView: DoubleAvatarView");
        View view = LayoutInflater.from(context).inflate(R.layout.view_double_avator, this);
        this.ivrightavator = (MalaImageView) view.findViewById(R.id.iv_right_avator);
        this.ivleftavator = (MalaImageView) view.findViewById(R.id.iv_left_avator);
        this.ivbgright = (ImageView) view.findViewById(R.id.iv_bg_right);
        this.ivbgleft = (ImageView) view.findViewById(R.id.iv_bg_left);
        init(context,attrs,defStyleAttr);

    }
    private void init(Context context, AttributeSet attrs, int defStyleAttr) {

        TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.RingProgressbar, defStyleAttr, 0);
        if (typeArray!=null){
            isShowRing = typeArray.getBoolean(R.styleable.DoubleAvatarView_is_show_ring,true);
            avatar_margin = typeArray.getDimensionPixelSize(R.styleable.DoubleAvatarView_avatar_padding,context.getResources().getDimensionPixelSize(R.dimen.tab_divider));

        }

        if (!isShowRing){
            ivbgleft.setVisibility(GONE);
            ivbgright.setVisibility(GONE);
        }

    }

  /*  @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        //获取直径
        int diameter = getMeasuredHeight();

        //int diameter = ivleftavator.getMeasuredHeight();
        LayoutParams leftavatorPara;
        leftavatorPara = (LayoutParams) ivleftavator.getLayoutParams();
        Log.d("ivleftavator before", "layout height: " + leftavatorPara.height);
        Log.d("ivleftavator before", "layout width: " + leftavatorPara.width);
        leftavatorPara.width = diameter;
        leftavatorPara.height = diameter;
        leftavatorPara.setMargins(avatar_margin, avatar_margin, avatar_margin, avatar_margin);
        Log.d("ivleftavator after", "layout height: " + leftavatorPara.height+" padding:"+ avatar_margin);
        Log.d("ivleftavator after", "layout width: " + leftavatorPara.width+" padding:"+ avatar_margin);
        ivleftavator.setLayoutParams(leftavatorPara);

        LayoutParams rightavatorPara;
        rightavatorPara = (LayoutParams) ivrightavator.getLayoutParams();
        Log.d("ivleftavator before", "layout height: " + rightavatorPara.height);
        Log.d("ivleftavator before", "layout width: " + rightavatorPara.width);
        rightavatorPara.width = diameter;
        rightavatorPara.height = diameter;
        rightavatorPara.setMargins(avatar_margin, avatar_margin, avatar_margin, avatar_margin);
        Log.d("leftavatorPara after", "layout height: " + rightavatorPara.height+" padding:"+ avatar_margin);
        Log.d("leftavatorPara after", "layout width: " + rightavatorPara.width+" padding:"+ avatar_margin);
        ivrightavator.setLayoutParams(rightavatorPara);

        LayoutParams bgleftPara;
        bgleftPara = (LayoutParams) ivbgleft.getLayoutParams();
        Log.d("bgleftPara before", "layout height: " + bgleftPara.height);
        Log.d("bgleftPara before", "layout width: " + bgleftPara.width);
        bgleftPara.width = diameter;
        bgleftPara.height = diameter;
        ivbgleft.setLayoutParams(bgleftPara);

        LayoutParams bgrightPara;
        bgrightPara = (LayoutParams) ivbgright.getLayoutParams();
        Log.d("bgrightPara before", "layout height: " + bgrightPara.height);
        Log.d("bgrightPara before", "layout width: " + bgrightPara.width);
        bgrightPara.width = diameter;
        bgrightPara.height = diameter;
        ivbgright.setLayoutParams(bgrightPara);
    }*/

    public void setLeftCircleImage(String url, int defImage){
        ivleftavator.loadCircleImage(url, defImage);
    }

    public void setRightCircleImage(String url, int defImage){
        ivrightavator.loadCircleImage(url, defImage);
    }
}
