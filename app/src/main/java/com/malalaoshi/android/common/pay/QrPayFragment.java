package com.malalaoshi.android.common.pay;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.adapters.FragmentGroupAdapter;
import com.malalaoshi.android.common.pay.api.OrderStatusApi;
import com.malalaoshi.android.core.base.BaseFragment;
import com.malalaoshi.android.core.network.api.ApiExecutor;
import com.malalaoshi.android.core.network.api.BaseApiContext;
import com.malalaoshi.android.entity.CreateCourseOrderResultEntity;
import com.malalaoshi.android.entity.OrderStatusModel;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by kang on 16/11/7.
 */

public class QrPayFragment extends BaseFragment implements FragmentGroupAdapter.IFragmentGroup, View.OnClickListener, FragmentGroupAdapter.IGetPageTitleListener {
    public static final String ARG_ORDER_INFO = "order info";

    @Bind(R.id.indicator_tabs)
    protected TabLayout tabLayout;

    private String[] tabs = new String[]{"微信支付","支付宝支付"};

    @Bind(R.id.viewpage)
    protected ViewPager vpQr;

    @Bind(R.id.tv_pay_result)
    protected TextView tvPayResult;

    //具体数据内容页面
    private Map<Integer, Fragment> fragments = new HashMap<>();
    CreateCourseOrderResultEntity resultEntity;
    @Override
    public String getStatName() {
        return null;
    }

    public static QrPayFragment newInstance(CreateCourseOrderResultEntity resultEntity) {
        if (resultEntity == null ) {
            return null;
        }
        QrPayFragment fragment = new QrPayFragment();
        Bundle bundle = new Bundle();
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
        }
    }
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qr_pay,container,false);
        ButterKnife.bind(this,view);
        init();
        setEvent();
        return view;
    }

    private void setEvent() {
        tvPayResult.setOnClickListener(this);
    }

    void init(){
        FragmentGroupAdapter fragmentAdapter = new FragmentGroupAdapter(getContext(), getFragmentManager(), this);
        fragmentAdapter.setGetPageTitleListener(this);
        vpQr.setAdapter(fragmentAdapter);
        vpQr.setOffscreenPageLimit(2);//缓存页面
        vpQr.setCurrentItem(0);
        //TabLayout加载viewpager
        tabLayout.setupWithViewPager(vpQr);
    }

    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = fragments.get(position);
        if (fragment == null) {
            switch (position) {
                case 0:
                    fragment = new QrCodeFragment().newInstance(resultEntity, PayManager.Pay.wx);
                    break;
                case 1:
                    fragment = new QrCodeFragment().newInstance(resultEntity, PayManager.Pay.alipay);
                    break;
            }
        }
        return fragment;
    }

    @Override
    public int getFragmentCount() {
        return tabs.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs[position];
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.tv_pay_result){
            if (resultEntity == null) {
                return;
            }
            startProcessDialog("加载中...");
            ApiExecutor.exec(new QrPayFragment.FetchOrderStatusRequest(this, resultEntity.getId()));
        }
    }

    private static final class FetchOrderStatusRequest extends BaseApiContext<QrPayFragment, OrderStatusModel> {

        private String orderId;

        public FetchOrderStatusRequest(QrPayFragment qrPayFragment, String orderId) {
            super(qrPayFragment);
            this.orderId = orderId;
        }

        @Override
        public OrderStatusModel request() throws Exception {
            return new OrderStatusApi().getOrderStatus(orderId);
        }

        @Override
        public void onApiSuccess(@NonNull OrderStatusModel response) {
            get().onGetOrderStatusSuccess(response);
        }

        @Override
        public void onApiFailure(Exception exception) {
            get().onGetOrderStatusFailed();
        }

        @Override
        public void onApiFinished() {
            super.onApiFinished();
            get().stopProcessDialog();
        }
    }

    private void onGetOrderStatusFailed() {
    }

    private void onGetOrderStatusSuccess(OrderStatusModel response) {

    }


}
