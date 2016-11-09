package com.malalaoshi.android.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kang on 16/1/15.
 */
public class FragmentGroupAdapter extends FragmentPagerAdapter{
    private List<View> tabs = new ArrayList<View>();
    private Context context;
    private IFragmentGroup fragment;

    private IGetPageTitleListener listener;
    public FragmentGroupAdapter(Context context, FragmentManager fm, IFragmentGroup fragment) {
        super(fm);
        this.context = context;
        this.fragment = fragment;
    }

    @Override
    public Fragment getItem(int position) {
        return fragment.createFragment(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (listener!=null){
            return listener.getPageTitle(position);
        }else{
            return super.getPageTitle(position);
        }
    }

    @Override
    public int getCount() {
        return fragment.getFragmentCount();
    }

    public interface IFragmentGroup{
        Fragment createFragment(int position);
        int getFragmentCount();
    }

    public interface IGetPageTitleListener{
        public CharSequence getPageTitle(int position);
    }

    public void setGetPageTitleListener(IGetPageTitleListener listener) {
        this.listener = listener;
    }
}
