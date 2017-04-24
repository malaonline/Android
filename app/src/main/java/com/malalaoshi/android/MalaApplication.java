package com.malalaoshi.android;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.malalaoshi.android.core.BaseApplication;
import com.malalaoshi.android.core.usercenter.UserManager;
import com.malalaoshi.android.exception.CrashHandler;
import com.malalaoshi.android.common.push.MalaPushClient;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.hawk.HawkBuilder;
import com.orhanobut.hawk.LogLevel;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by liumengjun on 11/16/15.
 */
public class MalaApplication extends BaseApplication {

    private static String TAG = "MalaApplication";
    private static MalaApplication instance;

    private String mMalaHost = BuildConfig.API_HOST;

    // 运行信息
    private boolean isNetworkOk;
    public boolean isFirstStartApp = true;

    private RefWatcher refWatcher;

    @Override
    protected void attachBaseContext(Context base) {
        MultiDex.install(base);
        super.attachBaseContext(base);
    }

    @Override
    protected void initOnMainProcess() {
        Hawk.init(this)
                .setEncryptionMethod(HawkBuilder.EncryptionMethod.MEDIUM)
                .setStorage(HawkBuilder.newSqliteStorage(this))
                .setLogLevel(LogLevel.FULL)
                .build();
    }

    @Override
    protected void initOnOtherProcess() {

    }

    @Override
    protected void initAlways() {
        instance = this;
        //启动应用后设置用户初始化并设置用户别名
        MalaPushClient.getInstance().init();
        MalaPushClient.getInstance().setAliasAndTags(UserManager.getInstance().getUserId(), null);
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        //LeakCanary.install(this);
        refWatcher = LeakCanary.install(this);
        CrashHandler.getInstance().init(this);//初始化全局异常管理
    }

    public static RefWatcher getRefWatcher(Context context) {
        MalaApplication application = (MalaApplication) context.getApplicationContext();
        return application.refWatcher;
    }

    public static MalaApplication getInstance() {
        return instance;
    }


    public String getMalaHost() {
        return mMalaHost;
    }

    public boolean isNetworkOk() {
        return isNetworkOk;
    }

    public void setIsNetworkOk(boolean isNetworkOk) {
        this.isNetworkOk = isNetworkOk;
    }

    @Override
    protected void onUserLogined() {
        //设置tag和别名(在登录和登出处需要添加设置别名)
        Log.e(TAG, "用户登录后设置JPush别名uid:" + UserManager.getInstance().getUserId());
        MalaPushClient.getInstance().setAliasAndTags(UserManager.getInstance().getUserId(), null);
    }

    @Override
    protected void onUserLogout() {
        //退出登录后,应该清空jpush别名,重置tags
        Log.e(TAG, "用户退出登录后清空JPush别名uid:" + UserManager.getInstance().getUserId());
        MalaPushClient.getInstance().setAliasAndTags(UserManager.getInstance().getUserId(), null);
    }
}
