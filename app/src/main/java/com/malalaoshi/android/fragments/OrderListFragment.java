package com.malalaoshi.android.fragments;

import com.malalaoshi.android.R;
import com.malalaoshi.android.adapters.MyOrderAdapter;
import com.malalaoshi.android.core.base.BaseRecycleAdapter;
import com.malalaoshi.android.core.base.BaseRefreshFragment;
import com.malalaoshi.android.network.api.MoreOrderListApi;
import com.malalaoshi.android.network.api.OrderListApi;
import com.malalaoshi.android.network.result.OrderListResult;

/**
 * Created by kang on 16/5/5.
 */
public class OrderListFragment extends BaseRefreshFragment<OrderListResult>{
    private String nextUrl;

    private MyOrderAdapter adapter;

    public static OrderListFragment newInstance() {
        OrderListFragment f = new OrderListFragment();
        return f;
    }

    @Override
    protected BaseRecycleAdapter createAdapter() {
        adapter = new MyOrderAdapter(getContext());
        return adapter;
    }

    @Override
    protected OrderListResult refreshRequest() throws Exception {
        return new OrderListApi().get();
    }

    @Override
    protected void refreshFinish(OrderListResult response) {
        super.refreshFinish(response);
        if (response != null) {
            nextUrl = response.getNext();
        }
    }

    @Override
    protected void loadMoreFinish(OrderListResult response) {
        super.loadMoreFinish(response);
        if (response != null) {
            nextUrl = response.getNext();
        }
    }

    @Override
    protected OrderListResult loadMoreRequest() throws Exception {
        return new MoreOrderListApi().getOrderList(nextUrl);
    }

    @Override
    protected void afterCreateView() {
        setEmptyViewText("没有订单");
        setEmptyViewIcon(R.drawable.ic_empty_order);
    }

    @Override
    public String getStatName() {
        return "订单列表页";
    }


}
