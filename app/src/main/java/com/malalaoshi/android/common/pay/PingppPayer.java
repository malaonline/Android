package com.malalaoshi.android.common.pay;

import android.util.Log;

import com.malalaoshi.android.entity.CreateChargeEntity;
import com.malalaoshi.android.entity.CreateCourseOrderResultEntity;
import com.malalaoshi.android.entity.JsonBodyBase;
import com.malalaoshi.android.common.pay.api.CreateOrderApi;
import com.malalaoshi.android.common.pay.api.FetchChargeApi;

/**
 * Pingplusplus payer
 * Created by tianwei on 2/28/16.
 */
public class PingppPayer implements Payer {

    @Override
    public CreateCourseOrderResultEntity createOrder(JsonBodyBase body) throws Exception {
       return new CreateOrderApi().createOrder(body.toJson());
    }

    @Override
    public String createOrderInfo(String orderId, String channel) throws Exception {
        CreateChargeEntity chargeEntity = new CreateChargeEntity();
        chargeEntity.setAction("pay");
        chargeEntity.setChannel(channel);
        Log.e("PingppPayer", "createOrderInfo: channel="+channel);
        return new FetchChargeApi().getCharge(orderId, chargeEntity);
    }
}
