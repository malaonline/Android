package com.malalaoshi.android.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.malalaoshi.android.R;
import com.malalaoshi.android.common.pay.utils.OrderDef;
import com.malalaoshi.android.core.base.BaseActivity;
import com.malalaoshi.android.core.utils.EmptyUtils;
import com.malalaoshi.android.core.view.TitleBarView;
import com.malalaoshi.android.fragments.OrderDetailFragment;
import com.malalaoshi.android.utils.FragmentUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by kang on 16/5/5.
 */
public class OrderInfoActivity extends BaseActivity implements TitleBarView.OnTitleBarClickListener {
    @Bind(R.id.title_view)
    protected TitleBarView titleView;

    public static void launch(Context context, String orderId, int orderType) {
        if (!EmptyUtils.isEmpty(orderId)) {
            Intent intent = new Intent(context, OrderInfoActivity.class);
            intent.putExtra(OrderDetailFragment.ARG_ORDER_ID, orderId);
            intent.putExtra(OrderDetailFragment.ARG_ORDER_TYPE, orderType);
            context.startActivity(intent);
        }
    }

    public static void launch(Context context, String orderId, Bundle bundle) {
        if (!EmptyUtils.isEmpty(orderId)) {
            Intent intent = new Intent(context, OrderInfoActivity.class);
            intent.putExtras(bundle);
            intent.putExtra(OrderDetailFragment.ARG_ORDER_ID, orderId);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ButterKnife.bind(this);
        initViews(savedInstanceState);
        titleView.setOnTitleBarClickListener(this);
    }

    private void initViews(Bundle savedInstanceState) {
        Intent intent = getIntent();
        titleView.setTitle("订单详情");
        String orderId = intent.getStringExtra(OrderDetailFragment.ARG_ORDER_ID);
        int orderType = intent.getIntExtra(OrderDetailFragment.ARG_ORDER_TYPE, OrderDef.ORDER_TYPE_NORMAL);
        if (savedInstanceState==null){
            OrderDetailFragment orderDetailFragment = OrderDetailFragment.newInstance(orderId,orderType);
            FragmentUtil.openFragment(R.id.order_fragment, getSupportFragmentManager(), null, orderDetailFragment, OrderDetailFragment.class.getName());
        }
    }


    @Override
    public void onTitleLeftClick() {
        this.finish();
    }

    @Override
    public void onTitleRightClick() {

    }

    @Override
    protected String getStatName() {
        return "订单详情";
    }
}
