package com.malalaoshi.android.core.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;

import java.util.ArrayList;

/**
 * Created by kang on 2017/3/13.
 */

public class PinyinUtils {

    /**
     * 获取汉字字符串的首字母，英文字符不变
     */
    public static String getHeadLetter(String chinese) {
        StringBuilder sb = new StringBuilder();
        char[] chars = chinese.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] > 128) {
                try {
                    sb.append(PinyinHelper.toHanyuPinyinStringArray(chars[i], defaultFormat)[0].charAt(0));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                sb.append(chars[i]);
            }
        }
        return sb.toString();
    }

    /**
     * 获取汉字字符串的汉语拼音，英文字符不变
     */
    public static String HanziToPinyin(String chines) {
        StringBuilder sb = new StringBuilder();
        char[] nameChar = chines.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < nameChar.length; i++) {
            if (nameChar[i] > 128) {
                try {
                    sb.append(PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat)[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                sb.append(nameChar[i]);
            }
        }
        return sb.toString();
    }

    /**
     * 获取汉字字符串的第一个字母
     */
    /*public static String getHeadLetter(String str) {
        StringBuilder sb = new StringBuilder();
        char c = str.charAt(0);
        String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c);
        if (pinyinArray != null) {
            sb.append(pinyinArray[0].charAt(0));
        } else {
            sb.append(c);
        }
        return sb.toString();
    }*/

}
