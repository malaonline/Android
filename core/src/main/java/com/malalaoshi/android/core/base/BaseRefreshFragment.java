package com.malalaoshi.android.core.base;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.PtrHandler;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import com.chanven.lib.cptr.recyclerview.RecyclerAdapterWithHF;
import com.malalaoshi.android.core.R;
import com.malalaoshi.android.core.network.api.ApiExecutor;
import com.malalaoshi.android.core.network.api.BaseApiContext;
import com.malalaoshi.android.core.utils.EmptyUtils;
import com.malalaoshi.android.core.utils.MiscUtil;
import com.malalaoshi.android.core.view.EmptyView;
import com.malalaoshi.android.core.view.ErrorView;
import com.malalaoshi.android.core.view.RefreshHeaderEffectView;

/**
 * Base fragment
 * Created by tianwei on 3/5/16.
 */
public abstract class BaseRefreshFragment<T extends BaseResult> extends BaseFragment {

    private Animation mFloatingButtonShow;
    private Animation mFloatingButtonHide;
    private ImageButton ibFloatingTop;
    private RecyclerView recyclerView;
    private LinearLayoutManager mLayoutManager;

    public enum LayoutType {
        REFRESH_FAILED,
        LIST,
        EMPTY
    }

    public abstract String getStatName();

    //刷新容器
    private PtrFrameLayout refreshLayout;

    private BaseRecycleAdapter adapter;

    private EmptyView emptyView;
    private ErrorView errorView;

    protected abstract BaseRecycleAdapter createAdapter();

    protected abstract T refreshRequest() throws Exception;

    protected abstract T loadMoreRequest() throws Exception;

    protected abstract void afterCreateView();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.core__fragment_refresh_base, container, false);
        initAnim();
        initView(view);
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                autoRefresh();
            }
        }, 100);
        afterCreateView();
        return view;
    }

    private void initAnim() {
        mFloatingButtonShow = AnimationUtils.loadAnimation(getContext(),R.anim.floating_button_show);
        mFloatingButtonHide = AnimationUtils.loadAnimation(getContext(), R.anim.floating_button_hide);
    }

    /**
     * 设置空view的文案
     */
    public void setEmptyViewText(String txt) {
        emptyView.setText(txt);
    }

    /**
     * 设置空view的图标
     * @param rid
     */
    public void setEmptyViewIcon(int rid){
        emptyView.setImage(rid);
    }

    public void setErrorViewText(String txt){
        errorView.setText(txt);
    }

    public void setErrorViewIcon(int rid){
        errorView.setImage(rid);
    }

    public void setLayout(LayoutType type) {
        switch (type) {
            case EMPTY:
                emptyView.setVisibility(View.VISIBLE);
                errorView.setVisibility(View.GONE);
                break;
            case REFRESH_FAILED:
                emptyView.setVisibility(View.GONE);
                errorView.setVisibility(View.VISIBLE);
                break;
            case LIST:
                emptyView.setVisibility(View.GONE);
                errorView.setVisibility(View.GONE);
                break;
        }
    }

    public BaseRecycleAdapter getAdapter() {
        return adapter;
    }

    @SuppressWarnings("unchecked")
    private void initView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        emptyView = (EmptyView) view.findViewById(R.id.view_empty);
        errorView = (ErrorView) view.findViewById(R.id.view_error);
        errorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoRefresh();
            }
        });
        initRefresh(view);
        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = createAdapter();
        recyclerView.setAdapter(new RecyclerAdapterWithHF(adapter));

        ibFloatingTop = (ImageButton) view.findViewById(R.id.ib_floating_top);
        ibFloatingTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.smoothScrollToPosition(0);
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && mLayoutManager.findFirstVisibleItemPosition() == 0){
                    floatingButtonHide();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy < 0){
                    floatingButtonShow();
                }else if (dy > 0){
                    floatingButtonHide();
                }
            }
        });

    }

    private void floatingButtonHide() {
        if (ibFloatingTop.getVisibility() == View.VISIBLE){
            ibFloatingTop.setVisibility(View.GONE);
            ibFloatingTop.startAnimation(mFloatingButtonHide);
        }
    }

    private void floatingButtonShow() {
        if (ibFloatingTop.getVisibility() == View.GONE || ibFloatingTop.getVisibility() == View.INVISIBLE){
                ibFloatingTop.setVisibility(View.VISIBLE);
                ibFloatingTop.startAnimation(mFloatingButtonShow);
        }
    }


    private void initRefresh(View view) {
        refreshLayout = (PtrFrameLayout) view.findViewById(R.id.refresh);
        //Header
        RefreshHeaderEffectView headerView = new RefreshHeaderEffectView(getContext());
        refreshLayout.setHeaderView(headerView);
        refreshLayout.addPtrUIHandler(headerView);
        refreshLayout.setKeepHeaderWhenRefresh(true);
        refreshLayout.setPullToRefresh(false);
        //这个会引起自动刷新刷新两次
        //refreshLayout.setEnabledNextPtrAtOnce(true);
        refreshLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                BaseRefreshFragment.this.onRefreshBegin();
            }
        });
        //Footer
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void loadMore() {
                onLoadMoreBegin();
            }
        });
    }

    /**
     * 刷新开始
     */
    protected void onRefreshBegin() {
        setLayout(LayoutType.LIST);
        ApiExecutor.exec(new RefreshTask<T>(this));
    }

    /**
     * 加载更多开始
     */
    protected void onLoadMoreBegin() {
        ApiExecutor.exec(new LoadMoreTask<T>(this));
    }

    /**
     * 自动刷新
     */
    protected void autoRefresh() {
        refreshLayout.autoRefresh();
    }

    /**
     * 刷新完成
     */
    @SuppressWarnings("unchecked")
    protected void refreshFinish(T response) {
        refreshLayout.refreshComplete();
        if (response == null) {
            if(adapter.getItemCount()<=0){
                setLayout(LayoutType.REFRESH_FAILED);
//                setErrorViewText(getErrorString());
            }else {
                MiscUtil.toast(R.string.network_error);
            }
            return;
        }
        if (EmptyUtils.isEmpty(response.getResults())) {
            adapter.clear();
            setLayout(LayoutType.EMPTY);
//            setEmptyViewText(getEmptyString());
        } else {
            setLayout(LayoutType.LIST);
            adapter.clear();
            adapter.addData(response.getResults());
        }

        if (EmptyUtils.isEmpty(response.getNext())) {
            refreshLayout.setLoadMoreEnable(false);
        } else {
            refreshLayout.setLoadMoreEnable(true);
        }
    }
    protected String getEmptyString(){
        return "没有数据";
    }
    protected String getErrorString(){
        return "加载失败，请重试";
    }

    /**
     * 加载更多完成
     */
    protected void loadMoreFinish(T response) {
        if (response == null) {
            refreshLayout.loadMoreComplete(false);
            return;
        }
        adapter.addData(response.getResults());
        if (EmptyUtils.isEmpty(response.getNext())) {
            refreshLayout.loadMoreComplete(false);
        } else {
            refreshLayout.loadMoreComplete(true);
        }
    }


    @SuppressWarnings("unchecked")
    private static final class RefreshTask<T extends BaseResult> extends BaseApiContext<BaseRefreshFragment, T> {

        public RefreshTask(BaseRefreshFragment baseRefreshFragment) {
            super(baseRefreshFragment);
        }

        @Override
        public T request() throws Exception {
            return (T) get().refreshRequest();
        }

        @Override
        public void onApiSuccess(@NonNull BaseResult response) {
            get().refreshFinish(response);
        }

        @Override
        public void onApiFailure(Exception exception) {
            get().refreshFinish(null);
        }
    }

    @SuppressWarnings("unchecked")
    private static final class LoadMoreTask<T extends BaseResult> extends BaseApiContext<BaseRefreshFragment, T> {

        public LoadMoreTask(BaseRefreshFragment baseRefreshFragment) {
            super(baseRefreshFragment);
        }

        @Override
        public T request() throws Exception {
            return (T) get().loadMoreRequest();
        }

        @Override
        public void onApiSuccess(@NonNull BaseResult response) {
            get().loadMoreFinish(response);
        }

        @Override
        public void onApiFailure(Exception exception) {
            get().loadMoreFinish(null);
        }
    }
}
