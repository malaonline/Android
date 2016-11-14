package com.malalaoshi.android.common.pay.utils;

/**
 * Created by kang on 16/11/10.
 */

public class OrderDef {
    //购买订单类型
    public static final int ORDER_TYPE_NORMAL = 0;
    public static final int ORDER_TYPE_LIVE_COURSE = 1;

    //订单状态
    //未知订单状态
    public static final int ORDER_STATUS_UNKNOWN_ERROR               = -1;        //已付款，已退费
    //双师订单状态
    public static final int LIVE_ORDER_STATUS_UNPAY_WAIT_PAY        = 0x00;        //未付款，等待支付
    public static final int LIVE_ORDER_STATUS_UNPAY_ENROLLMENT_FULL = 0x01;        //未付款，双师课程报名已满
    public static final int LIVE_ORDER_STATUS_UNPAY_ORDER_CLOSE     = 0x02;        //未付款，支付超时，订单已关闭
    public static final int LIVE_ORDER_STATUS_UNPAY_COURSE_OVER     = 0x03;        //未付款，课程已下架
    public static final int LIVE_ORDER_STATUS_PAY_SUCCESS           = 0x04;        //已付款，购课成功
    public static final int LIVE_ORDER_STATUS_PAY_ENROLLMENT_FULL   = 0x05;        //已付款，购课失败，双师课程报名已满
    public static final int LIVE_ORDER_STATUS_PAY_COURSE_OVER       = 0x06;        //已付款，购课失败，课程已下架
    public static final int LIVE_ORDER_STATUS_PAY_ORDER_CLOSE       = 0x07;        //已付款，支付超时，订单已关闭
    public static final int LIVE_ORDER_STATUS_PAY_REFUND_AUDIT      = 0x08;        //已付款，退款审核中
    public static final int LIVE_ORDER_STATUS_PAY_REFUND_SUCCESS    = 0x09;        //已付款，已退费
    //一对一订单状态
    public static final int NORMAL_ORDER_STATUS_UNPAY_WAIT_PAY        = 0x10;      //未付款，等待支付
    public static final int NORMAL_ORDER_STATUS_UNPAY_TIME_OCCUPIED   = 0x11;      //未付款，上课时间被占用
    public static final int NORMAL_ORDER_STATUS_UNPAY_UNPUBLISH       = 0x12;      //未付款，教师已下架
    public static final int NORMAL_ORDER_STATUS_UNPAY_ORDER_CLOSE     = 0x13;      //未付款，支付超时，订单关闭
    public static final int NORMAL_ORDER_STATUS_PAY_SUCCESS           = 0x14;      //已付款，购课成功
    public static final int NORMAL_ORDER_STATUS_PAY_TIME_OCCUPIED     = 0x15;      //已付款，购课失败，上课时间被占用
    public static final int NORMAL_ORDER_STATUS_PAY_UNPUBLISH         = 0x16;      //已付款，购课失败，教师已经下架
    public static final int NORMAL_ORDER_STATUS_PAY_ORDER_CLOSE       = 0x17;      //已付款，支付超时，订单已关闭
    public static final int NORMAL_ORDER_STATUS_PAY_REFUND_AUDIT      = 0x18;      //已付款，退款审核中
    public static final int NORMAL_ORDER_STATUS_PAY_REFUND_SUCCESS    = 0x19;      //已付款，已退费
}
