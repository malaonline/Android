package com.malalaoshi.android.utils;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.widget.TextView;

import com.malalaoshi.android.entity.BaseEntity;

import java.util.List;

/**
 * Created by liumengjun on 12/31/15.
 */
public class StringUtil {

    public static final String DEFAULT_SEPERATOR = " | ";

    public static String join(String[] ss) {
        return join(ss, DEFAULT_SEPERATOR);
    }

    public static String join(String[] ss, String spot) {
        if (ss == null || ss.length == 0) {
            return "";
        }
        if (spot == null) {
            spot = DEFAULT_SEPERATOR;
        }

        StringBuilder sb = new StringBuilder(ss.length * 8);
        for (String s : ss) {
            sb.append(s).append(spot);
        }
        sb.setLength(sb.length() - spot.length());

        return sb.toString();
    }

    public static String joinEntityName(List<? extends BaseEntity> entities) {
        return joinEntityName(entities, DEFAULT_SEPERATOR);
    }

    public static String joinEntityName(List<? extends BaseEntity> entities, String spot) {
        if (entities == null || entities.size() == 0) {
            return "";
        }
        if (spot == null) {
            spot = DEFAULT_SEPERATOR;
        }

        StringBuilder sb = new StringBuilder(entities.size() * 8);
        for (BaseEntity ele : entities) {
            sb.append(ele.getName()).append(spot);
        }
        sb.setLength(sb.length() - spot.length());

        return sb.toString();
    }

    public static boolean compareUrls(String url1,String url2){
        boolean res = false;
        if (url1!=null&&url2!=null){
            String str1[] = url1.split("\\?");
            String str2[] = url2.split("\\?");
            if (str2[0].equals(str1[0])){
                res = true;
            }
        }
        return res;
    }

    public static void setHumpText(Context context, TextView textView, String str1, int style1Id, String str2, int style2Id){
        String text = String.format("%s / %s",str1,str2);
        SpannableString styledText = new SpannableString(text);
        styledText.setSpan(new TextAppearanceSpan(context, style1Id), 0, text.indexOf("/")+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        styledText.setSpan(new TextAppearanceSpan(context, style2Id), text.indexOf("/")+1, text.length() , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(styledText, TextView.BufferType.SPANNABLE);
    }
}
