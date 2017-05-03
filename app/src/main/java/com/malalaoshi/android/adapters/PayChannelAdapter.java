package com.malalaoshi.android.adapters;

import android.content.Context;
import android.support.v4.app.FragmentManager;

/**
 * Created by donald on 2017/5/3.
 */

public class PayChannelAdapter extends FragmentGroupAdapter {

    private int mCount = 0;

    public PayChannelAdapter(Context context, FragmentManager fm, IFragmentGroup fragment) {
        super(context, fm, fragment);
    }

    @Override
    public void notifyDataSetChanged() {
        mCount = getCount();
        super.notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        if (mCount > 0){
            mCount --;
            return POSITION_NONE;
        }
        return super.getItemPosition(object);
    }
}
