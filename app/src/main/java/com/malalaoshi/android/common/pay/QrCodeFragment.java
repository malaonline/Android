package com.malalaoshi.android.common.pay;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.WriterException;
import com.malalaoshi.android.R;
import com.malalaoshi.android.core.base.BaseFragment;
import com.malalaoshi.android.core.network.api.ApiExecutor;
import com.malalaoshi.android.core.network.api.BaseApiContext;
import com.malalaoshi.android.core.utils.EmptyUtils;
import com.malalaoshi.android.entity.Charge;
import com.malalaoshi.android.entity.ChargeOrder;
import com.malalaoshi.android.entity.CreateCourseOrderResultEntity;
import com.malalaoshi.android.qr.QRCodeUtil;
import com.malalaoshi.android.utils.JsonUtil;
import com.malalaoshi.android.utils.MiscUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.content.Context.WINDOW_SERVICE;

/**
 * Created by kang on 16/11/8.
 */

public class QrCodeFragment extends BaseFragment implements View.OnClickListener {

    public static final String ARG_ORDER_INFO = "order info";

    @Bind(R.id.view_loading)
    protected View viewLoading;

    @Bind(R.id.iv_load_anim)
    protected ImageView ivLoadAnim;

    private AnimationDrawable loadAnim;

    @Bind(R.id.view_qr_code)
    protected View viewQrCode;

    @Bind(R.id.tv_total_pay)
    protected TextView tvTotalPay;

    @Bind(R.id.iv_qr)
    protected ImageView ivQr;

    @Bind(R.id.view_qr_load_failed)
    protected View viewQrLoadFailed;

    @Bind(R.id.tv_failed_tip)
    protected TextView tvFailedTip;

    @Bind(R.id.tv_reload)
    protected TextView tvReload;

    private String payChannel;

    CreateCourseOrderResultEntity resultEntity;

    public static QrCodeFragment newInstance(CreateCourseOrderResultEntity resultEntity, String payChannel) {
        if (resultEntity == null || EmptyUtils.isEmpty(payChannel)) {
            return null;
        }
        QrCodeFragment fragment = new QrCodeFragment();
        Bundle bundle = new Bundle();
        bundle.putString("paychannel", payChannel);
        bundle.putSerializable(ARG_ORDER_INFO, resultEntity);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            resultEntity = (CreateCourseOrderResultEntity) bundle.getSerializable(ARG_ORDER_INFO);
            payChannel = bundle.getString("paychannel");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qr_code, container, false);
        ButterKnife.bind(this, view);

        init();
        if (PayManager.Pay.wx_pub_qr.name().equals(payChannel)){
            initData();
        }
        //        initData();
        setEvent();
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null){
            parent.removeView(view);
        }
        return view;
    }

//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        if (getUserVisibleHint()){
//            Log.e("QrCodeFragment", "setUserVisibleHint: "+payChannel);
//            initData();
//        }
//    }

    private void setEvent() {
        tvReload.setOnClickListener(this);
    }

    public void init(){
        loadAnim = (AnimationDrawable) ivLoadAnim.getDrawable();
        if (resultEntity != null) {
            double value = Double.valueOf(resultEntity.getTo_pay()) * 0.01d;
            tvTotalPay.setText("￥"+com.malalaoshi.android.utils.Number.subZeroAndDot(value));
        }
    }

    public void initData() {
        loadData();
    }

    private void loadData() {
        if (resultEntity == null || TextUtils.isEmpty(resultEntity.getOrder_id())) {
            return;
        }
        setLoadingView();
        ApiExecutor.exec(new FetchOrderInfoRequest(this, resultEntity.getId(), payChannel));

    }

    @Override
    public void onClick(View view) {
        loadData();
    }

    void setLoadingView(){
        loadAnim.start();
        viewLoading.setVisibility(View.VISIBLE);
        viewQrCode.setVisibility(View.GONE);
        viewQrLoadFailed.setVisibility(View.GONE);
    }

    void setQrCodeView(Charge charge){
        loadAnim.stop();
        viewLoading.setVisibility(View.GONE);
        viewQrCode.setVisibility(View.VISIBLE);
        viewQrLoadFailed.setVisibility(View.GONE);
        int drawableId;
        String qrUrl = null;
        if (PayManager.Pay.wx_pub_qr.name().equals(payChannel)) {
            drawableId = R.drawable.ic_wx_pay;
            qrUrl = charge.getCredential().getWx_pub_qr();
        } else {
            drawableId = R.drawable.ic_ali_pay;
            qrUrl = charge.getCredential().getAlipay_qr();
        }
        setQrCode(qrUrl,drawableId);
    }

    private void setQrCode(String qrUrl, int drawableId) {
        WindowManager manager = (WindowManager) getContext().getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point displaySize = new Point();
        display.getSize(displaySize);
        int width = displaySize.x;
        int height = displaySize.y;
        int smallerDimension = width < height ? width : height;
        smallerDimension = smallerDimension * 7 / 8;
        try {
            Bitmap bitmap = QRCodeUtil.createQRCodeWithLogo(getContext(), qrUrl, smallerDimension, drawableId);
            if (bitmap != null) {
                ivQr.setImageBitmap(bitmap);
            }

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    void setLoadErrorView(int errCode){
        loadAnim.stop();
        viewLoading.setVisibility(View.GONE);
        viewQrCode.setVisibility(View.GONE);
        viewQrLoadFailed.setVisibility(View.VISIBLE);

        if(errCode==-2){
            tvReload.setVisibility(View.GONE);
            tvFailedTip.setText("该课程已经被占用，请重新选择！");
        }else{
            tvFailedTip.setText("支付码加载失败！");
        }
    }

    private void onPayResult(String response) {
        if (response == null) {
            //加载失败
            onPayResultFailed(-1);
            return;
        }
        ChargeOrder chargeOrder = JsonUtil.parseStringData(response, ChargeOrder.class);
        if (chargeOrder != null && chargeOrder.isOk() && -1 == chargeOrder.getCode()) {
            //"部分课程时间已被占用，请重新选择上课时间!","确定";
            onPayResultFailed(-2);
            return;
        }

        Charge charge = JsonUtil.parseStringData(response, Charge.class);

        Log.e("QRCODE", response);
        if (charge!=null
                &&charge.getCredential()!=null
                &&((PayManager.Pay.alipay_qr.name().equals(payChannel)&& !EmptyUtils.isEmpty(charge.getCredential().getAlipay_qr()))
                 ||(PayManager.Pay.wx_pub_qr.name().equals(payChannel)&& !EmptyUtils.isEmpty(charge.getCredential().getWx_pub_qr())))){
            setQrCodeView(charge);
        }else{
            //加载失败
            onPayResultFailed(-1);
        }


    }

    private void onPayResultFailed(int errorCode) {
        setLoadErrorView(errorCode);
    }


    private static final class FetchOrderInfoRequest extends BaseApiContext<QrCodeFragment, String> {

        private String orderId;
        private String payment;

        public FetchOrderInfoRequest(QrCodeFragment qrCodeFragment, String orderId, String payChannel) {
            super(qrCodeFragment);
            this.orderId = orderId;
            this.payment = payChannel;
        }

        @Override
        public String request() throws Exception {
            return PayManager.getInstance().getOrderInfo(orderId, payment);
        }

        @Override
        public void onApiSuccess(@NonNull String response) {
            get().onPayResult(response);
        }

        @Override
        public void onApiFailure(Exception exception) {
            get().onPayResultFailed(-1);
            MiscUtil.toast("支付码加载失败");
        }

        @Override
        public void onApiFinished() {
            super.onApiFinished();
            get().loadAnim.stop();
        }
    }

    @Override
    public String getStatName() {
        return "二维码";
    }
}
