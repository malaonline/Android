package com.malalaoshi.android.common.pay;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.malalaoshi.android.BuildConfig;
import com.malalaoshi.android.entity.CreateCourseOrderResultEntity;
import com.malalaoshi.android.entity.JsonBodyBase;
import com.pingplusplus.android.PaymentActivity;
import com.pingplusplus.android.PingppLog;
import com.pingplusplus.libone.PingppOne;
//import com.pingplusplus.libone.PingppOnePayment;

/**
 * PayManger
 * Created by tianwei on 2/27/16.
 */
public class PayManager {

    public enum Pay {
        /**
         * 支付支付渠道
         */
        alipay,
        /**
         * 微信支付渠道
         */
        wx,
        /**
         * 扫码支付
         */
        qr
    }

    private static final class Holder {
        private static final PayManager instance = new PayManager();
    }

    public static final int REQUEST_CODE_PAYMENT = 0x8;
    private Payer payer;

    private PayManager() {
        init();
    }

    public static PayManager getInstance() {
        return Holder.instance;
    }

    private void init() {
        //设置需要使用的支付方式,true:显示该支付通道，默认为false
        PingppOne.SHOW_CHANNEL_WECHAT = true;
        PingppOne.SHOW_CHANNEL_ALIPAY = true;

        //设置支付通道的排序,最小的排在最前
        PingppOne.CHANNEL_ALIPAY_INDEX = 1;
        PingppOne.CHANNEL_WECHAT_INDEX = 3;

        //提交数据的格式，默认格式为json
        PingppOne.CONTENT_TYPE = "application/json";
        PingppLog.DEBUG = BuildConfig.DEBUG;
        payer = new PingppPayer();
    }

/*    public CreateCourseOrderResultEntity createOrder(CreateCourseOrderEntity entity) throws Exception {
        return payer.createOrder(entity);
    }

    public CreateCourseOrderResultEntity createOrder(CreateLiveCourseOrderEntity entity) throws Exception {
        return payer.createOrder(entity);
    }*/

    public <T extends JsonBodyBase> CreateCourseOrderResultEntity createOrder(T entity) throws Exception {
        return payer.createOrder(entity);
    }

    public String getOrderInfo(String orderId, String channel) throws Exception {
        return payer.createOrderInfo(orderId, channel);
    }

    public void pay(String charge, Activity activity) {
        if (null == charge) {
            return;
        }
        Log.d("charge", charge);
        Intent intent = new Intent(activity, PaymentActivity.class);
        intent.putExtra(PaymentActivity.EXTRA_CHARGE, charge);
        activity.startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }
}
