package com.malalaoshi.android.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import com.malalaoshi.android.core.base.BaseTitleActivity;
import com.malalaoshi.android.entity.City;
import com.malalaoshi.android.fragments.CityPickerFragment;


/**
 * Created by kang on 16/8/16.
 */
public class CityPickerActivity extends BaseTitleActivity implements CityPickerFragment.OnCityClick {

    public static int RESULT_CODE_CITY = 1000;
    public static String EXTRA_CITY = "city";
    public static String EXTRA_IS_INIT_CITY = "isInitCity";

    public boolean isInitCity = true;

    public static void openForInit(Activity activity) {
        Intent intent = new Intent(activity, CityPickerActivity.class);
        intent.putExtra(EXTRA_IS_INIT_CITY,true);
        activity.startActivity(intent);
    }

    public static void openForResult(Fragment fragment,int requestCode) {
        Intent intent = new Intent(fragment.getContext(), CityPickerActivity.class);
        intent.putExtra(EXTRA_IS_INIT_CITY,false);
        fragment.startActivityForResult(intent,requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        CityPickerFragment cityPickerFragment = null;
        if(savedInstanceState==null){
            //cityPickerFragment = (CityPickerFragment) Fragment.instantiate(this, CityPickerFragment.class.getName(), getIntent().getExtras());
            cityPickerFragment = CityPickerFragment.newInstance();
            replaceFragment(cityPickerFragment);
        }else{
            cityPickerFragment = (CityPickerFragment) getSupportFragmentManager().getFragments().get(0);
        }
        cityPickerFragment.setOnCityClick(this);
    }

    private void init() {
        Intent intent = getIntent();
        isInitCity = intent.getBooleanExtra(EXTRA_IS_INIT_CITY,true);
    }

    @Override
    public void onCityClick(City city) {
        if (city!=null){
            if (isInitCity){
                //跳转到学校选择页面
                SchoolPickerActivity.openForInit(this,city);
            }else{
                setResult(city);
                finish();
            }
        }
    }

    private void setResult(City city) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_CITY, city);
        setResult(RESULT_CODE_CITY, intent);
    }

    @Override
    protected String getStatName() {
        return "选择城市";
    }


}
