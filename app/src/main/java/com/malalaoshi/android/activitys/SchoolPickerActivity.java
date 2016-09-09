package com.malalaoshi.android.activitys;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.malalaoshi.android.MainActivity;
import com.malalaoshi.android.core.base.BaseTitleActivity;
import com.malalaoshi.android.core.usercenter.UserManager;
import com.malalaoshi.android.entity.City;
import com.malalaoshi.android.entity.School;
import com.malalaoshi.android.fragments.SchoolPickerFragment;


/**
 * Created by kang on 16/9/6.
 */
public class SchoolPickerActivity extends BaseTitleActivity implements SchoolPickerFragment.OnSchoolClick {
    public static int RESULT_CODE_SCHOOL_CITY = 1001;
    public static String EXTRA_CITY = "city";
    public static String EXTRA_SCHOOL = "school";
    public static String EXTRA_IS_INIT_SCHOOL = "init school";

    public boolean isInitSchool = true;
    private City city;
    private School school;
    public static void openForInit(Context context, City city) {
        Intent intent = new Intent(context, SchoolPickerActivity.class);
        intent.putExtra(EXTRA_CITY,city);
        intent.putExtra(EXTRA_IS_INIT_SCHOOL,true);
        context.startActivity(intent);
    }

    public static void openForResult(Activity activity, School school, City city) {
        Intent intent = new Intent(activity, SchoolPickerActivity.class);
        intent.putExtra(EXTRA_CITY,city);
        intent.putExtra(EXTRA_SCHOOL,school);
        intent.putExtra(EXTRA_IS_INIT_SCHOOL,false);
        activity.startActivityForResult(intent,RESULT_CODE_SCHOOL_CITY);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        SchoolPickerFragment schoolPickerFragment = null;
        if(savedInstanceState==null){
            //cityPickerFragment = (CityPickerFragment) Fragment.instantiate(this, CityPickerFragment.class.getName(), getIntent().getExtras());
            schoolPickerFragment = SchoolPickerFragment.newInstance(city,isInitSchool);
            replaceFragment(schoolPickerFragment);
        }else{
            schoolPickerFragment = (SchoolPickerFragment) getSupportFragmentManager().getFragments().get(0);
        }
        schoolPickerFragment.setOnSchoolClick(this);
    }

    private void init() {
        Intent intent = getIntent();
        city = intent.getParcelableExtra(EXTRA_CITY);
        school = intent.getParcelableExtra(EXTRA_SCHOOL);
        isInitSchool = intent.getBooleanExtra(EXTRA_IS_INIT_SCHOOL,true);
    }

    @Override
    public void onSchoolClick(City city, School school) {
        UserManager userManager = UserManager.getInstance();
        if (isInitSchool){
            Intent intent = new Intent(this,MainActivity.class);
            this.startActivity(intent);
        }else{
            setResult(city,school);
        }
        userManager.setCity(city.getName());
        userManager.setCityId(city.getId());
        userManager.setSchool(school.getName());
        userManager.setSchoolId(school.getId());

        finish();
    }

    private void setResult(City city, School school) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_CITY, city);
        intent.putExtra(EXTRA_SCHOOL, school);
        setResult(RESULT_CODE_SCHOOL_CITY,intent);
    }

    @Override
    protected String getStatName() {
        return "选择校区";
    }


}
