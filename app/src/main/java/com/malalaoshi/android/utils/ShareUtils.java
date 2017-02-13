package com.malalaoshi.android.utils;

import android.content.Context;

import com.malalaoshi.android.R;

import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by kang on 17/2/13.
 */

public class ShareUtils {

    /**
     * 一键分享
     * @param context
     * @param title        分享标题
     * @param content      分享短语说明
     * @param imgUrl       分享logo
     * @param redirectUrl  微信分享url地址
     */
    public static void showWxShare(Context context, String title, String content, String imgUrl, String redirectUrl ) {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        oks.setTitle(title);
        oks.setText(content);
        oks.setImageUrl(imgUrl);
        oks.setUrl(redirectUrl);
        // 启动分享GUI
        oks.show(context);
    }
}
