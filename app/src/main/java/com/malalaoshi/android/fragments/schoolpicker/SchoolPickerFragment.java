package com.malalaoshi.android.fragments.schoolpicker;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.activitys.schoolpicker.CityPickerActivity;
import com.malalaoshi.android.adapters.SchoolPickerAdapter;
import com.malalaoshi.android.network.api.SchoolListApi;
import com.malalaoshi.android.core.base.BaseFragment;
import com.malalaoshi.android.core.network.api.ApiExecutor;
import com.malalaoshi.android.core.network.api.BaseApiContext;
import com.malalaoshi.android.entity.City;
import com.malalaoshi.android.entity.School;
import com.malalaoshi.android.network.result.SchoolListResult;
import com.malalaoshi.android.utils.MiscUtil;
import com.malalaoshi.android.ui.widgets.ScrollListView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by kang on 16/8/16.
 */
public class SchoolPickerFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    public static String ARGS_CITY = "city";
    public static String EXTRA_IS_INIT_SCHOOL = "isInitSchool";

    @Bind(R.id.ll_city)
    protected RelativeLayout rlCity;

    @Bind(R.id.tv_picker_tiptext)
    protected TextView tvPickerTiptext;

    @Bind(R.id.tv_city)
    protected TextView tvCity;

    @Bind(R.id.gv_all_schools)
    protected ScrollListView gvAllSchools;

    protected SchoolPickerAdapter schoolPickerAdapter;

    public boolean isInitSchool = true;

    private City city;

    private List<School> schools;

    private OnSchoolClick onSchoolClick;

    public static SchoolPickerFragment newInstance(City city,boolean isInitSchool) {
        SchoolPickerFragment schoolPickerFragment = new SchoolPickerFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARGS_CITY,city);
        bundle.putBoolean(EXTRA_IS_INIT_SCHOOL,isInitSchool);
        schoolPickerFragment.setArguments(bundle);
        return schoolPickerFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_school_picker, container, false);
        ButterKnife.bind(this, view);
        init();
        initData();
        initView();
        setEvent();
        return view;
    }

    private void init() {
        Bundle bundle = getArguments();
        if (bundle!=null){
            city = bundle.getParcelable(ARGS_CITY);
            isInitSchool = bundle.getBoolean(EXTRA_IS_INIT_SCHOOL);
        }
    }

    private void initView() {
        if (isInitSchool){
            rlCity.setVisibility(View.GONE);
            tvPickerTiptext.setVisibility(View.GONE);
        }else{
            tvCity.setText(city.getName());
        }
    }

    private void setEvent() {
        gvAllSchools.setOnItemClickListener(this);
    }

    private void initData() {
        schoolPickerAdapter = new SchoolPickerAdapter(getContext());
        gvAllSchools.setAdapter(schoolPickerAdapter);
        loadData();
    }

    private void loadData() {
        ApiExecutor.exec(new FetchSchoolListRequest(this,city.getId()));
    }

    public void setOnSchoolClick(OnSchoolClick onSchoolClick) {
        this.onSchoolClick = onSchoolClick;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (onSchoolClick!=null){
            onSchoolClick.onSchoolClick(city, schools.get(position));
        }
    }

    private static final class FetchSchoolListRequest extends BaseApiContext<SchoolPickerFragment, SchoolListResult> {

        private Long cityId;
        @Override
        public void onApiStarted() {
            get().startProcessDialog("加载中...");
            super.onApiStarted();
        }

        public FetchSchoolListRequest(SchoolPickerFragment cityPickerFragment, Long cityId) {
            super(cityPickerFragment);
            this.cityId = cityId;
        }

        @Override
        public SchoolListResult request() throws Exception {
            return new SchoolListApi().getSchoolsByCityId(cityId);
        }

        @Override
        public void onApiSuccess(@NonNull SchoolListResult response) {
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

    private void onLoadSuccess(SchoolListResult response) {
        if (response == null) {
            onLoadFailed();
        }
        schools = response.getResults();
        if (schools!=null){
            schoolPickerAdapter.clear();
            schoolPickerAdapter.addAll(schools);
            schoolPickerAdapter.notifyDataSetChanged();
        }
    }

    public interface OnSchoolClick {
        void onSchoolClick(City city, School school);
    }

    @OnClick(R.id.ll_city)
    void onClickPickerCity(View view){
        CityPickerActivity.openForResult(this,CityPickerActivity.RESULT_CODE_CITY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (CityPickerActivity.RESULT_CODE_CITY==resultCode&&data!=null){
            //更新老师列表数据
            city = data.getParcelableExtra(CityPickerActivity.EXTRA_CITY);
            tvCity.setText(city.getName());

            schoolPickerAdapter.clear();
            schoolPickerAdapter.notifyDataSetChanged();
            loadData();
        }
    }

    @Override
    public String getStatName() {
        return "城市选择";
    }
}
