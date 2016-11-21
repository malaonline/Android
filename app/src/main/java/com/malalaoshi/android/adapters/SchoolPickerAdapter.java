package com.malalaoshi.android.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.core.base.MalaBaseAdapter;
import com.malalaoshi.android.entity.School;

/**
 * Created by kang on 16/1/5.
 */
public class SchoolPickerAdapter extends MalaBaseAdapter<School> {

    public SchoolPickerAdapter(Context context) {
        super(context);
    }

    @Override
    protected View createView(int position, ViewGroup parent) {
        View convertView = View.inflate(context, R.layout.view_school_item, null);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
        viewHolder.tvAddress = (TextView) convertView.findViewById(R.id.tv_address);
        convertView.setTag(viewHolder);
        return convertView;
    }

    @Override
    protected void fillView(int position, View convertView, School data) {
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();

        viewHolder.tvName.setText(data.getName());
        viewHolder.tvAddress.setText("");
    }

    class ViewHolder {
        TextView tvName;
        TextView tvAddress;
    }
}
