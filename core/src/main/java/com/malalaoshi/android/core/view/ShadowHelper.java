package com.malalaoshi.android.core.view;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.hitomi.cslibrary.CrazyShadow;
import com.hitomi.cslibrary.base.CrazyShadowDirection;
import com.malalaoshi.android.core.R;
import com.malalaoshi.android.core.utils.MiscUtil;


/**
 * 封装设置卡片阴影的第三方库
 * Created by donald on 2017/5/16.
 */

public class ShadowHelper {
    private static int mCorner = MiscUtil.dp2px(4);
    private static int mRadius = MiscUtil.dp2px(4);
    private static int mShadowColor = MiscUtil.getColor(R.color.main_color);

    /**
     * 以draw的方式设置阴影，使用默认背景和圆角值。 draw方式，必须设置背景颜色 默认白色
     * @param context
     * @param view 需要设置阴影的view
     */
    public static void setDrawShadow(Context context, View view){
        new CrazyShadow.Builder()
                .setContext(context)
                .setDirection(CrazyShadowDirection.ALL)
                .setShadowRadius(mRadius)
                .setBaseShadowColor(mShadowColor)
                .setBackground(Color.WHITE)
                .setCorner(mCorner)
                .setImpl(CrazyShadow.IMPL_DRAW)
                .action(view);
    }

    /**
     * 设置阴影，使用默认背景 draw方式，必须设置背景颜色 默认白色
     * @param context
     * @param radius 圆角值
     * @param view 需要设置阴影的view
     */
    public static CrazyShadow setDrawShadow(Context context, int radius, View view){
        radius = MiscUtil.dp2px(radius);
        CrazyShadow shadow = new CrazyShadow.Builder()
                .setContext(context)
                .setDirection(CrazyShadowDirection.ALL)
                .setShadowRadius(radius)
                .setBaseShadowColor(mShadowColor)
                .setBackground(Color.WHITE)
                .setCorner(mCorner)
                .setImpl(CrazyShadow.IMPL_DRAW)
                .action(view);
        return shadow;
    }
    /**
     * 设置阴影，使用默认背景 draw方式，必须设置背景颜色 默认白色
     * @param context
     * @param corner 圆角值
     * @param view 需要设置阴影的view
     */
    public static CrazyShadow setDrawShadow(Context context, View view,int corner){
        corner = MiscUtil.dp2px(corner);
        CrazyShadow shadow = new CrazyShadow.Builder()
                .setContext(context)
                .setDirection(CrazyShadowDirection.ALL)
                .setShadowRadius(mRadius)
                .setBaseShadowColor(mShadowColor)
                .setBackground(Color.TRANSPARENT)
                .setCorner(corner)
                .setImpl(CrazyShadow.IMPL_DRAW)
                .action(view);
        return shadow;
    }
    /**
     * 设置指定方向的阴影，使用默认背景 draw方式，必须设置背景颜色 默认白色
     * @param context
     * @param corner 圆角值
     * @param view 需要设置阴影的view
     */
    public static CrazyShadow setDrawShadow(Context context, View view,int corner, int direction){
        corner = MiscUtil.dp2px(corner);
        CrazyShadow shadow = new CrazyShadow.Builder()
                .setContext(context)
                .setDirection(direction)
                .setShadowRadius(mRadius)
                .setBaseShadowColor(mShadowColor)
                .setBackground(Color.TRANSPARENT)
                .setCorner(corner)
                .setImpl(CrazyShadow.IMPL_DRAW)
                .action(view);
        return shadow;
    }

    /**
     * 顶部阴影
     * @param context
     * @param view
     * @param corner
     */
    public static void setTopDrawShadow(Context context, View view,int corner){
        setDrawShadow(context, view, corner, CrazyShadowDirection.TOP);
    }

    /**
     * 底部阴影
     * @param context
     * @param view
     * @param corner
     */
    public static void setBottomDrawShadow(Context context, View view, int corner){
        setDrawShadow(context, view, corner, CrazyShadowDirection.BOTTOM);
    }


    /***
     * 设置阴影，使用默认背景和圆角
     * @param context
     * @param impl 阴影实现方式
     * @param view  需要设置阴影的view
     */
    public static void setShadow(Context context, String impl, View view){
        new CrazyShadow.Builder()
                .setContext(context)
                .setDirection(CrazyShadowDirection.ALL)
                .setShadowRadius(mRadius)
                .setBaseShadowColor(mShadowColor)
                .setBackground(Color.WHITE)
                .setCorner(mCorner)
                .setImpl(impl)
                .action(view);
    }

    /**
     * 以wrap的方式设置阴影， 默认背景和圆角 不需要设置背景颜色
     * @param context
     * @param view
     */
    public static void setWrapShadow(Context context, View view){
        new CrazyShadow.Builder()
                .setContext(context)
                .setDirection(CrazyShadowDirection.ALL)
                .setShadowRadius(mRadius)
                .setBaseShadowColor(mShadowColor)
                .setCorner(mCorner)
                .setImpl(CrazyShadow.IMPL_WRAP)
                .action(view);
    }
    /**
     * 以wrap的方式设置阴影， 默认背景和圆角 不需要设置背景颜色
     * @param context
     * @param view
     */
    public static void setWrapShadow(Context context, View view, int corner){
        corner = MiscUtil.dp2px(corner);
        new CrazyShadow.Builder()
                .setContext(context)
                .setDirection(CrazyShadowDirection.ALL)
                .setShadowRadius(mRadius)
                .setBaseShadowColor(mShadowColor)
                .setCorner(corner)
                .setImpl(CrazyShadow.IMPL_WRAP)
                .action(view);
    }

    /***
     * 以float的方式设置阴影， 默认背景和圆角 不需要设置背景颜色
     * view的位置大小不能发生变化
     * @param context
     * @param view
     */
    public static void setFloatShadow(Context context, View view){
        new CrazyShadow.Builder()
                .setContext(context)
                .setDirection(CrazyShadowDirection.ALL)
                .setShadowRadius(mRadius)
                .setBaseShadowColor(mShadowColor)
                .setCorner(mCorner)
                .setImpl(CrazyShadow.IMPL_FLOAT)
                .action(view);
    }

}
