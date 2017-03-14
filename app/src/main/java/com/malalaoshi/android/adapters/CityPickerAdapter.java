package com.malalaoshi.android.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.core.base.MalaBaseAdapter;
import com.malalaoshi.android.entity.City;

/**
 * Created by kang on 16/1/5.
 */
public class CityPickerAdapter extends MalaBaseAdapter<City> implements SectionIndexer {

    public CityPickerAdapter(Context context) {
        super(context);
    }

    @Override
    protected View createView(int position, ViewGroup parent) {
        View convertView = View.inflate(context, R.layout.view_city_item, null);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.tvCategory =  (TextView) convertView.findViewById(R.id.tv_category);
        viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
        convertView.setTag(viewHolder);
        return convertView;
    }

    @Override
    protected void fillView(int position, View convertView, City data) {
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();

        //根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);

        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if(position == getPositionForSection(section)){
            viewHolder.tvCategory.setVisibility(View.VISIBLE);
            viewHolder.tvCategory.setText(String.valueOf(data.getPinyin().charAt(0)));
        }else{
            viewHolder.tvCategory.setVisibility(View.GONE);
        }
        viewHolder.tvName.setText(data.getName());
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = getList().get(i).getPinyin();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == sectionIndex) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public int getSectionForPosition(int position) {
        return getList().get(position).getPinyin().charAt(0);
    }

    /**
     * 提取英文的首字母，非英文字母用#代替。
     *
     * @param str
     * @return
     */
    private String getAlpha(String str) {
        String  sortStr = str.trim().substring(0, 1).toUpperCase();
        // 正则表达式，判断首字母是否是英文字母
        if (sortStr.matches("[A-Z]")) {
            return sortStr;
        } else {
            return "#";
        }
    }

    class ViewHolder {
        TextView tvCategory;
        TextView tvName;
    }
}
