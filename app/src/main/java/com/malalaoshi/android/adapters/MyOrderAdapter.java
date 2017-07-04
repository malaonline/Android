package com.malalaoshi.android.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.activitys.CourseConfirmActivity;
import com.malalaoshi.android.activitys.OrderInfoActivity;
import com.malalaoshi.android.common.pay.PayActivity;
import com.malalaoshi.android.common.pay.api.DeleteOrderApi;
import com.malalaoshi.android.common.pay.utils.OrderDef;
import com.malalaoshi.android.core.base.BaseRecycleAdapter;
import com.malalaoshi.android.core.network.api.ApiExecutor;
import com.malalaoshi.android.core.network.api.BaseApiContext;
import com.malalaoshi.android.core.utils.EmptyUtils;
import com.malalaoshi.android.core.utils.MiscUtil;
import com.malalaoshi.android.entity.CreateCourseOrderResultEntity;
import com.malalaoshi.android.entity.LiveCourse;
import com.malalaoshi.android.entity.Order;
import com.malalaoshi.android.entity.Subject;
import com.malalaoshi.android.network.result.OkResult;
import com.malalaoshi.android.ui.widgets.DoubleImageView;
import com.malalaoshi.android.utils.DialogUtil;
import com.malalaoshi.android.utils.StringUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by donald on 2017/6/28.
 */

public class MyOrderAdapter extends BaseRecycleAdapter<MyOrderAdapter.OrderViewHolder, Order> {


    public MyOrderAdapter(Context context) {
        super(context);
    }

    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderViewHolder holder, int position) {
        holder.setup(dataList.get(position));
    }

    class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.tv_order_item_id)
        TextView mTvOrderItemId;
        @Bind(R.id.tv_order_item_status)
        TextView mTvOrderItemStatus;
        @Bind(R.id.tv_order_item_teacher_name)
        TextView mTvOrderItemTeacherName;
        @Bind(R.id.tv_order_item_course_name)
        TextView mTvOrderItemCourseName;
        @Bind(R.id.tv_order_item_address)
        TextView mTvOrderItemAddress;
        @Bind(R.id.tv_order_item_total_price)
        TextView mTvOrderItemTotalPrice;
        @Bind(R.id.div_order_item_avatar)
        DoubleImageView mDivOrderItemAvatar;
        @Bind(R.id.view_order_item_divide)
        View mViewOrderItemDivide;
        @Bind(R.id.tv_order_item_cancel)
        TextView mTvOrderItemCancel;
        @Bind(R.id.tv_order_item_buy)
        TextView mTvOrderItemBuy;
        @Bind(R.id.ll_order_item_operate)
        LinearLayout mLlOrderItemOperate;
        @Bind(R.id.ll_order_item_root)
        LinearLayout mLlOrderItemRoot;
        private Order mOrder;

        public OrderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setup(Order order) {
            if (order == null) return;
            mOrder = order;
            itemView.setOnClickListener(this);
            mTvOrderItemId.setText("订单编号：" + order.getOrder_id());
            StringUtil.setCourseInfo(mTvOrderItemCourseName, "课程名称：" + order.getGrade() + order.getSubject());
            mTvOrderItemAddress.setText(order.getSchool());
            String price = mContext.getString(R.string.total) + String.format("%.2f", order.getTo_pay() * 0.01d);
            SpannableString spannableString = new SpannableString(price);
            spannableString.setSpan(new ForegroundColorSpan(MiscUtil.getColor(R.color.color_red_fe3059)), 5, price.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(MiscUtil.getColor(R.color.color_black_a0a3ab)), 0, 5, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            mTvOrderItemTotalPrice.setText(spannableString);
            if (order.is_live()) {
                LiveCourse liveCourse = order.getLive_class();
                if (liveCourse != null) {
                    mDivOrderItemAvatar.loadImg(liveCourse.getLecturer_avatar(), liveCourse.getAssistant_avatar(), DoubleImageView.LOAD_DOUBLE);
                }
                mTvOrderItemTeacherName.setVisibility(View.GONE);
            } else {
                mTvOrderItemTeacherName.setVisibility(View.VISIBLE);
                StringUtil.setCourseInfo(mTvOrderItemTeacherName, "教师姓名：" + order.getTeacher_name());
                mDivOrderItemAvatar.loadImg(order.getTeacher_avatar(), "", DoubleImageView.LOAD_SIGNLE_BIG);
            }
            setWithStatus();
        }

        private void setWithStatus() {
            String status = mOrder.getStatus();

            switch (status) {
                case "u"://待支付
                    mViewOrderItemDivide.setVisibility(View.VISIBLE);
                    mLlOrderItemOperate.setVisibility(View.VISIBLE);
                    mTvOrderItemStatus.setText("订单待支付");
                    mLlOrderItemRoot.setBackground(MiscUtil.getDrawable(R.drawable.bg_order_item_red));
                    mTvOrderItemCancel.setVisibility(View.VISIBLE);
                    mTvOrderItemBuy.setTextColor(MiscUtil.getColor(R.color.color_red_fe3059));
                    mTvOrderItemBuy.setBackground(MiscUtil.getDrawable(R.drawable.selector_semicircle_red_frame_btn_bg));
                    mTvOrderItemBuy.setText("立即支付");
                    break;
                case "p"://支付成功
                    mTvOrderItemStatus.setText("交易完成");
                    mLlOrderItemRoot.setBackground(MiscUtil.getDrawable(R.drawable.bg_order_item_blue));
                    mTvOrderItemCancel.setVisibility(View.GONE);
                    if (!mOrder.is_live()){
                        mViewOrderItemDivide.setVisibility(View.VISIBLE);
                        mLlOrderItemOperate.setVisibility(View.VISIBLE);
                        mTvOrderItemBuy.setTextColor(MiscUtil.getColor(R.color.main_color));
                        mTvOrderItemBuy.setBackground(MiscUtil.getDrawable(R.drawable.selector_semicircle_blue_frame_btn_bg));
                        mTvOrderItemBuy.setText("再次购买");
                    }else {
                        mViewOrderItemDivide.setVisibility(View.GONE);
                        mLlOrderItemOperate.setVisibility(View.GONE);
                    }
                    break;
                case "r"://退款成功
                    mTvOrderItemStatus.setText("退款成功");
                    mViewOrderItemDivide.setVisibility(View.GONE);
                    mLlOrderItemOperate.setVisibility(View.GONE);
                    break;
                case "d"://订单关闭
                    mTvOrderItemStatus.setText("订单已关闭");
                    mLlOrderItemRoot.setBackground(MiscUtil.getDrawable(R.drawable.bg_order_item_gray));
                    mViewOrderItemDivide.setVisibility(View.GONE);
                    mLlOrderItemOperate.setVisibility(View.GONE);
                    break;
            }
        }

        @OnClick({R.id.tv_order_item_cancel, R.id.tv_order_item_buy})
        public void onViewClicked(View view) {
            switch (view.getId()) {
                case R.id.tv_order_item_cancel:
                    if (mOrder.getId() != null) {
                        //取消订单
                        // TODO: 2017/5/5 添加dialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                        builder.setMessage("确认取消订单吗？");
                        builder.setNegativeButton("暂不取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                            }
                        });
                        builder.setPositiveButton("取消订单", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startProcessDialog("正在取消订单...");
                                ApiExecutor.exec(new CancelCourseOrderRequest(OrderViewHolder.this, mOrder.getId() + ""));
                                dialog.dismiss();
                            }
                        });
                        builder.create().show();

                    } else {
                        MiscUtil.toast("订单id错误!");
                    }
                    break;
                case R.id.tv_order_item_buy:
                    if (mOrder.is_live()) {
                        if ("u".equals(mOrder.getStatus())) {
                            //付款页
                            launchPayActivity(mContext);
                        }
                    } else {
                        if ("u".equals(mOrder.getStatus())) {
                            //订单详情页
                            OrderInfoActivity.launch(mContext, mOrder.getId() + "", mOrder.is_live() ? OrderDef.ORDER_TYPE_LIVE_COURSE : OrderDef.ORDER_TYPE_NORMAL);
                        } else {
                            //确认课程页
                            startCourseConfirmActivity();
                        }
                    }
                    break;
            }
        }

        private void startCourseConfirmActivity() {
            if (mOrder != null && mOrder.getTeacher() != null) {
                Subject subject = Subject.getSubjectIdByName(mOrder.getSubject());
                Long teacherId = Long.valueOf(mOrder.getTeacher());
                if (teacherId != null && subject != null) {
                    CourseConfirmActivity.launch(mContext, teacherId, mOrder.getTeacher_name(),
                            mOrder.getTeacher_avatar(), subject, mOrder.getSchool_id());
                }
            }
        }

        private void launchPayActivity(Context context) {
            if (mOrder == null || mOrder.getId() == null
                    || EmptyUtils.isEmpty(mOrder.getOrder_id())
                    || mOrder.getTo_pay() == null)
                return;
            Log.e("OrderViewHolder", "launchPayActivity: ");
            CreateCourseOrderResultEntity entity = new CreateCourseOrderResultEntity();
            entity.setId(mOrder.getId() + "");
            entity.setOrder_id(mOrder.getOrder_id());
            entity.setTo_pay((long) mOrder.getTo_pay().doubleValue());
            if (mOrder.is_live()) {
                entity.setOrderType(OrderDef.ORDER_TYPE_LIVE_COURSE);
            } else {
                entity.setOrderType(OrderDef.ORDER_TYPE_NORMAL);
            }
            PayActivity.launch(entity, context, true);
        }

        private void startProcessDialog(String message) {
            DialogUtil.startCircularProcessDialog(mContext, message, true, true);
        }

        private void stopProcessDialog() {
            DialogUtil.stopProcessDialog();
        }

        @Override
        public void onClick(View v) {
            OrderInfoActivity.launch(mContext, mOrder.getId() + "",
                    mOrder.is_live() ? OrderDef.ORDER_TYPE_LIVE_COURSE : OrderDef.ORDER_TYPE_NORMAL);

        }

        private final class CancelCourseOrderRequest extends BaseApiContext<OrderViewHolder, OkResult> {

            private String orderId;

            public CancelCourseOrderRequest(OrderViewHolder viewHolder, String orderId) {
                super(viewHolder);
                this.orderId = orderId;
            }

            @Override
            public OkResult request() throws Exception {
                return new DeleteOrderApi().delete(orderId);
            }

            @Override
            public void onApiSuccess(@NonNull OkResult response) {
                get().stopProcessDialog();
                if (response.isOk()) {
                    get().mOrder.setStatus("d");
                    get().setWithStatus();
                    com.malalaoshi.android.utils.MiscUtil.toast("订单已取消!");
                } else {
                    com.malalaoshi.android.utils.MiscUtil.toast("订单取消失败,请下拉刷新订单列表!");
                }
            }

            @Override
            public void onApiFinished() {
                get().stopProcessDialog();
            }

            @Override
            public void onApiFailure(Exception exception) {
                com.malalaoshi.android.utils.MiscUtil.toast("订单状态取消失败,请检查网络!");
            }
        }

    }
}
