package com.malalaoshi.android.common.pay.utils;

import android.support.annotation.NonNull;

import com.malalaoshi.android.R;
import com.malalaoshi.android.entity.OrderStatusModel;

/**
 * Created by kang on 16/11/10.
 */

public class OrderStatusUtils {

    public static int getLiveCourseOrderStatus(OrderStatusModel status){
        if (status.getStatus().equals("u")){             //未付款
            return OrderDef.LIVE_ORDER_STATUS_UNPAY_WAIT_PAY;
        } else if (status.getStatus().equals("p")){      //已付款
            if (status.is_timeslot_allocated()) {        //付款成功
                return OrderDef.LIVE_ORDER_STATUS_PAY_SUCCESS;
            }else{
                return OrderDef.LIVE_ORDER_STATUS_PAY_ENROLLMENT_FULL;
            }
        } else if (status.getStatus().equals("d")){
            return OrderDef.LIVE_ORDER_STATUS_UNPAY_ORDER_CLOSE;
        } else if (status.getStatus().equals("r")){
            return OrderDef.LIVE_ORDER_STATUS_PAY_REFUND_SUCCESS;
        } else{
           return OrderDef.ORDER_STATUS_UNKNOWN_ERROR;
        }
    }

    public static int getNormalOrderStatus(OrderStatusModel status){
        if (status.getStatus().equals("u")){             //未付款
            return OrderDef.NORMAL_ORDER_STATUS_UNPAY_WAIT_PAY;

        } else if (status.getStatus().equals("p")){      //已付款
            if (status.is_timeslot_allocated()) {        //付款成功
                return OrderDef.NORMAL_ORDER_STATUS_PAY_SUCCESS;
            }else{
                return OrderDef.NORMAL_ORDER_STATUS_PAY_TIME_OCCUPIED;
            }
        } else if (status.getStatus().equals("d")){
            if (status.getIs_teacher_published()){
                return OrderDef.NORMAL_ORDER_STATUS_UNPAY_ORDER_CLOSE;
            }else{
                return OrderDef.NORMAL_ORDER_STATUS_UNPAY_UNPUBLISH;
            }

        } else if (status.getStatus().equals("r")){
            return OrderDef.NORMAL_ORDER_STATUS_PAY_REFUND_SUCCESS;
        } else{
            return OrderDef.ORDER_STATUS_UNKNOWN_ERROR;
        }
    }

}
