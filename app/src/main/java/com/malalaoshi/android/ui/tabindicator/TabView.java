package com.malalaoshi.android.ui.tabindicator;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.malalaoshi.android.R;
import com.malalaoshi.android.core.utils.DensityUtil;

import butterknife.ButterKnife;


/**
 * Created by kang on 16/5/17.
 */
public class TabView extends LinearLayout {

    private final Context mContext;
    private TextView tvTabTitle;
    private ImageView ivTabIndicator;
    private View viewTabIndicator;

    private int tabTextColor ;
    private int tabTextFocusColor ;

    public TabView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.view_indicator_tab,null);
        tvTabTitle = (TextView) view.findViewById(R.id.tv_tab_title);
        ivTabIndicator = (ImageView) view.findViewById(R.id.iv_tab_indicator);
        viewTabIndicator = view.findViewById(R.id.view_tab_indicator);
        addView(view);
        ButterKnife.bind(this, view);
    }

    public void setTabTitle(String string){
        tvTabTitle.setText(string);
    }

    public void  setHeightLight(){
        tvTabTitle.setTextColor(tabTextFocusColor);
        viewTabIndicator.setSelected(true);
    }

    public void resetHeightLight(){
        tvTabTitle.setTextColor(tabTextColor);
        viewTabIndicator.setSelected(false);
    }

    public void  setTabIndicatorVisibility(int visibility){
        ivTabIndicator.setVisibility(visibility);
    }
    public void setTabIndicatorSrc(int imgId, int width, int height){
        ViewGroup.LayoutParams layoutParams = ivTabIndicator.getLayoutParams();
        layoutParams.width = DensityUtil.dip2px(mContext, width);
        layoutParams.height = DensityUtil.dip2px(mContext, height);
        ivTabIndicator.setLayoutParams(layoutParams);
        ivTabIndicator.setBackground(getResources().getDrawable(imgId));
    }
    public void setIndicatorHot(){
        setTabIndicatorSrc(R.drawable.hot, 20, 10);
    }
    public void setIndicatorRedDots(){
        setTabIndicatorSrc(R.drawable.bg_circle_indicator, 7, 7);
    }


    public void setTabTextSize(int tabTextSize) {
        tvTabTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,tabTextSize);
    }

    public void setTabTextColor(int tabTextColor) {
        this.tabTextColor = tabTextColor;
    }

    public void setTabTextFocusColor(int tabTextFocusColor) {
        this.tabTextFocusColor = tabTextFocusColor;
    }
}
