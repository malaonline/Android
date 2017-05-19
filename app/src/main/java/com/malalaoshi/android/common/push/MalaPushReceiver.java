package com.malalaoshi.android.common.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.malalaoshi.android.MainActivity;
import com.malalaoshi.android.activitys.OrderInfoActivity;
import com.malalaoshi.android.activitys.CommentActivity;
import com.malalaoshi.android.activitys.schoolpicker.CityPickerActivity;
import com.malalaoshi.android.core.usercenter.UserManager;
import com.malalaoshi.android.core.utils.EmptyUtils;
import com.malalaoshi.android.entity.PushNotificationExtra;
import com.malalaoshi.android.utils.JsonUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;


import cn.jpush.android.api.JPushInterface;

/**
 * Created by kang on 16/2/25.
 */
public class MalaPushReceiver extends BroadcastReceiver {
    private static final String TAG = "MalaPushReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if (EmptyUtils.isEmpty(action)){
            Log.e(TAG,"jpush action is null");
            return;
        }
        Bundle bundle = intent.getExtras();
        Log.e(TAG, "action:" + action + ", extras:" + printBundle(bundle));

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(action)) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "JPush用户注册成功，Registration Id:" + regId);
            //send the Registration Id to your server...
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(action)) {
            Log.d(TAG, "接受到推送下来的自定义消息:" + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            processCustomMessage(context, bundle);

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(action)) {
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.d(TAG, "接收到推送下来的通知，ID:" + notifactionId);
            receivingNotification(context,bundle);

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(action)) {
            Log.d(TAG, "用户点击打开了通知,通知Id:"+bundle.getString(JPushInterface.EXTRA_MSG_ID));

            MalaPushClient.getInstance().reportNotificationOpened(bundle.getString(JPushInterface.EXTRA_MSG_ID));
            processNotification(context, bundle);

        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(action)) {
            Log.d(TAG, "user received rich push callback:" + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(action)) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            Log.w(TAG, "connected state change to " + connected);
        } else {
            Log.d(TAG, "Unhandled intent - " + action);
        }
    }

    private void receivingNotification(Context context, Bundle bundle){
        String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
        Log.d(TAG, " title : " + title);
        String message = bundle.getString(JPushInterface.EXTRA_ALERT);
        Log.d(TAG, "message : " + message);
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        Log.d(TAG, "extras : " + extras);
    }

    private void processNotification(Context context, Bundle bundle) {

        //获取通知消息类型
        PushNotificationExtra pushExtra = null;
        try {
            pushExtra = JsonUtil.parseStringData(bundle.getString(JPushInterface.EXTRA_EXTRA),PushNotificationExtra.class);
        } catch (Exception e) {
            Log.e(TAG, "Get message extra JSON error!");
        }

        if (pushExtra==null){
            Log.e(TAG, "Get message extra JSON error!");
            return;
        }

        if (MalaPushDef.PUSH_NOTIFICATION_CLASS_STARTING.equals(pushExtra.getType())) {
            //上课通知
            lunchUserSchedule(context, bundle);
        } else if (MalaPushDef.PUSH_NOTIFICATION_REFUNDS_SUCCESS.equals(pushExtra.getType())) {
            //退费成功通知
            if (EmptyUtils.isEmpty(pushExtra.getCode())){
                Log.e(TAG, "Get message extra order id is null!");
                return;
            }
            lunchOrderDetail(context, bundle, pushExtra.getCode());
        } else if (MalaPushDef.PUSH_NOTIFICATION_CLASS_CHANGED.equals(pushExtra.getType())) {
            //课程变更通知
            lunchUserSchedule(context, bundle);
        } else if (MalaPushDef.PUSH_NOTIFICATION_CLASS_FINISHED.equals(pushExtra.getType())) {
            //课程结束,评价提醒
            lunchCommentList(context, bundle);
        } else if (MalaPushDef.PUSH_NOTIFICATION_COUPON_EXPIRED.equals(pushExtra.getType())) {
            //优惠价到期提醒
            lunchMainPager(context, bundle);
        } else if (MalaPushDef.PUSH_NOTIFICATION_LIVE_COURSE_ACTIVITY.equals(pushExtra.getType())) {
            lunchLiveCourseList(context, bundle);
        } else {
            Log.e(TAG, "Undefined notification type!");
        }
    }

    private void lunchLiveCourseList(Context context, Bundle bundle) {
        UserManager userManager = UserManager.getInstance();
        if (userManager.getCityId()>0&&userManager.getSchoolId()>0){
            Intent i = new Intent(context, MainActivity.class);
            i.putExtra(MainActivity.EXTRAS_PAGE_INDEX, MainActivity.PAGE_INDEX_LIVE_COURSE);
            i.putExtras(bundle);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(i);
        }else{
            CityPickerActivity.openForInit(context);
        }
    }

    private void lunchMainPager(Context context, Bundle bundle) {
        Intent i = new Intent(context, MainActivity.class);
        i.putExtra(MainActivity.EXTRAS_PAGE_INDEX, MainActivity.PAGE_INDEX_LIVE_COURSE);
        i.putExtras(bundle);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(i);
    }

    //打开课程表
    private void lunchUserSchedule(Context context, Bundle bundle) {
        Intent i = new Intent(context, MainActivity.class);
        i.putExtra(MainActivity.EXTRAS_PAGE_INDEX, MainActivity.PAGE_INDEX_COURSES);
        i.putExtras(bundle);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(i);
    }

    //打开订单详情页
    private void lunchOrderDetail(Context context, Bundle bundle, String orderId) {
        OrderInfoActivity.launch(context,orderId,bundle);
    }

    //打开评价列表
    private void lunchCommentList(Context context, Bundle bundle) {
        //打开评价列表
        Intent mIntent = new Intent(context, CommentActivity.class);
        mIntent.putExtras(bundle);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(mIntent);
    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (bundle.getString(JPushInterface.EXTRA_EXTRA).isEmpty()) {
                    Log.i(TAG, "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next().toString();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

    //send msg to MainActivity
    private void processCustomMessage(Context context, Bundle bundle) {
        //自定义消息处理
    }
}
