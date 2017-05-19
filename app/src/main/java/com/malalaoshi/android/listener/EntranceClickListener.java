package com.malalaoshi.android.listener;

/**
 * 会员专享  自定义View 中按钮点击事件
 * Created by donald on 2017/5/9.
 */

public interface EntranceClickListener {
    /**
     * 显示<错题错哪里来>说明对话框
     */
    void showPoint();

    /**
     * 重新获取错题数据
     */
    void retry();

    /**
     * 查看错题本样本
     */
    void lookSample();
}
