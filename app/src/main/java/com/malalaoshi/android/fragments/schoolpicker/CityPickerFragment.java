package com.malalaoshi.android.fragments.schoolpicker;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.adapters.CityPickerAdapter;
import com.malalaoshi.android.core.utils.PinyinUtils;
import com.malalaoshi.android.network.api.CityListApi;
import com.malalaoshi.android.core.base.BaseFragment;
import com.malalaoshi.android.core.network.api.ApiExecutor;
import com.malalaoshi.android.core.network.api.BaseApiContext;
import com.malalaoshi.android.entity.City;
import com.malalaoshi.android.network.result.CityListResult;
import com.malalaoshi.android.utils.MiscUtil;
import com.malalaoshi.android.ui.widgets.SideBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by kang on 16/8/16.
 */
public class CityPickerFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    @Bind(R.id.gv_all_cities)
    protected ListView gvAllCities;

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
        sidebar.setTextView(vtTipDialog);
    }

    private void setEvent() {
        gvAllCities.setOnItemClickListener(this);
        //设置右侧触摸监听
        sidebar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position =  cityPickerAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    gvAllCities.setSelection(position);
                }
            }
        });
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
            return;
        }
        cities = response.getResults();
        if (cities != null) {
            cityPickerAdapter.clear();
            sortCitys(cities);
            System.out.println(cities);
            cityPickerAdapter.addAll(cities);
            cityPickerAdapter.notifyDataSetChanged();
        }
    }
    private void sortCitys(List<City> cities) {
        ArrayList<City> list = new ArrayList<City>();
        for (int i = 0; i < cities.size(); i++) {
            City city = cities.get(i);
            //汉字转换成拼音
            String pinyin = PinyinUtils.HanziToPinyin(city.getName());
            // 正则表达式，判断首字母是否是英文字母
            if (pinyin.matches("[A-Z]+")) {
                city.setPinyin(pinyin.toUpperCase());
            } else {
                city.setPinyin("#");
            }
        }
        // 根据a-z进行排序源数据
        Collections.sort(cities, new PinyinComparator());
    }

    public interface OnCityClick{
        void onCityClick(City city);
    }

    public class PinyinComparator implements Comparator<City> {
        public int compare(City o1, City o2) {
            if (o1.getPinyin().equals("@")
                    || o2.getPinyin().equals("#")) {
                return -1;
            } else if (o1.getPinyin().equals("#")
                    || o2.getPinyin().equals("@")) {
                return 1;
            } else {
                return o1.getPinyin().compareTo(o2.getPinyin());
            }
        }
    }

    @Override
    public String getStatName() {
        return "城市选择";
    }
}