package com.malalaoshi.android.ui.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.malalaoshi.android.R;
import com.malalaoshi.android.core.image.MalaImageView;

/**
 * Created by kang on 16/10/28.
 */

public class DoubleAvatarView extends RelativeLayout {
    private com.malalaoshi.android.core.image.MalaImageView ivleftavator;
    private com.malalaoshi.android.core.image.MalaImageView ivrightavator;
    public DoubleAvatarView(Context context) {
        this(context,null);
    }

    public DoubleAvatarView(Context context, AttributeSet attrs) {
        this(context,attrs,-1);
    }

    public DoubleAvatarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = LayoutInflater.from(context).inflate(R.layout.view_double_avator, this);
        this.ivrightavator = (MalaImageView) view.findViewById(R.id.iv_right_avator);
        this.ivleftavator = (MalaImageView) view.findViewById(R.id.iv_left_avator);
    }

    public void setLeftCircleImage(String url, int defImage){
        ivleftavator.loadCircleImage(url, defImage);
    }

    public void setRightCircleImage(String url, int defImage){
        ivrightavator.loadCircleImage(url, defImage);
    }
}
