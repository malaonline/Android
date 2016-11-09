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

public class QrCodeFragment extends BaseFragment {

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

    @Bind(R.id.tv_reload)
    protected TextView tvReload;

    private String payChannel;

    CreateCourseOrderResultEntity resultEntity;

    public static QrCodeFragment newInstance(CreateCourseOrderResultEntity resultEntity, PayManager.Pay currentPay) {
        if (resultEntity == null || currentPay == null) {
            return null;
        }
        QrCodeFragment fragment = new QrCodeFragment();
        Bundle bundle = new Bundle();
        bundle.putString("paychannel", currentPay.name());
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
        initData();
        return view;
    }

    public void init(){
        loadAnim = (AnimationDrawable) ivLoadAnim.getDrawable();
        if (resultEntity != null) {
            double value = Double.valueOf(resultEntity.getTo_pay()) * 0.01d;
            tvTotalPay.setText("￥"+com.malalaoshi.android.utils.Number.subZeroAndDot(value));
        }
    }

    private void initData() {
        loadData();
    }

    private void loadData() {
        if (resultEntity == null || TextUtils.isEmpty(resultEntity.getOrder_id())) {
            return;
        }
        setLoadingView();
        ApiExecutor.exec(new QrCodeFragment.FetchOrderInfoRequest(this, resultEntity.getId(), payChannel));

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
        if (payChannel == PayManager.Pay.wx.name()) {
            drawableId = R.drawable.ic_wx;
            qrUrl = charge.getCredential().getWx_pub_qr();
        } else {
            drawableId = R.drawable.ic_ali;
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
        setQrCodeView(charge);

        Log.e("QRCODE", response);
        if (charge!=null&&charge.getCredential()!=null){
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
            MiscUtil.toast("订单状态不正确");
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
