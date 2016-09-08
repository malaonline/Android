package com.malalaoshi.android.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.adapter.CityPickerAdapter;
import com.malalaoshi.android.api.CityListApi;
import com.malalaoshi.android.core.base.BaseFragment;
import com.malalaoshi.android.core.network.api.ApiExecutor;
import com.malalaoshi.android.core.network.api.BaseApiContext;
import com.malalaoshi.android.core.usercenter.UserManager;
import com.malalaoshi.android.entity.City;
import com.malalaoshi.android.result.CityListResult;
import com.malalaoshi.android.util.MiscUtil;
import com.malalaoshi.android.view.ScrollGridView;
import com.malalaoshi.android.view.ScrollListView;
import com.malalaoshi.android.view.SideBar;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by kang on 16/8/16.
 */
public class CityPickerFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    @Bind(R.id.gv_all_cities)
    protected ScrollListView gvAllCities;

    @Bind(R.id.vt_tip_dialog)
    protected TextView vtTipDialog;

    @Bind(R.id.sidebar)
    protected SideBar sidebar;

    protected CityPickerAdapter cityPickerAdapter;

    private List<City> cities;

    private OnCityClick onCityClick;

    public static CityPickerFragment newInstance() {
        CityPickerFragment cityPickerFragment = new CityPickerFragment();
        return cityPickerFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_city_picker, container, false);
        ButterKnife.bind(this, view);
        initData();
        initView();
        setEvent();
        return view;
    }

    private void initView() {
    }

    private void setEvent() {
        gvAllCities.setOnItemClickListener(this);
    }

    private void initData() {
        cityPickerAdapter = new CityPickerAdapter(getContext());
        gvAllCities.setAdapter(cityPickerAdapter);
        loadData();
    }

    private void loadData() {
        ApiExecutor.exec(new FetchCityListRequest(this));
    }

    public void setOnCityClick(OnCityClick onCityClick) {
        this.onCityClick = onCityClick;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (onCityClick!=null){
            onCityClick.onCityClick(cities.get(position));
        }
    }

    private static final class FetchCityListRequest extends BaseApiContext<CityPickerFragment, CityListResult> {

        @Override
        public void onApiStarted() {
            get().startProcessDialog("加载中...");
            super.onApiStarted();
        }

        public FetchCityListRequest(CityPickerFragment cityPickerFragment) {
            super(cityPickerFragment);
        }

        @Override
        public CityListResult request() throws Exception {
            return new CityListApi().getCityList();
        }

        @Override
        public void onApiSuccess(@NonNull CityListResult response) {
            get().onLoadSuccess(response);
        }

        @Override
        public void onApiFailure(Exception exception) {
            get().onLoadFailed();
        }

        @Override
        public void onApiFinished() {
            super.onApiFinished();
            get().stopProcessDialog();
        }
    }

    private void onLoadFailed() {
        MiscUtil.toast("加载失败,请检查网络");
    }

    private void onLoadSuccess(CityListResult response) {
        if (response == null) {
            onLoadFailed();
        }
        cities = response.getResults();
        if (cities!=null){
            cityPickerAdapter.clear();
            cityPickerAdapter.addAll(cities);
            cityPickerAdapter.notifyDataSetChanged();
        }
    }

    public interface OnCityClick{
        public void onCityClick(City city);
    }

    @Override
    public String getStatName() {
        return "城市选择";
    }
}
