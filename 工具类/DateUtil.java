package com.gzpsc.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期转换工具类
 * Created by ZJH on 2017/7/21.
 */
public class DateUtil {

    private DateUtil() {
    }

    /**
     * 字符串转化为Date
     *
     * @param origin  日期字符串
     * @param pattern 要转换的日期格式 例如 yyyy-MM-dd
     * @return
     */
    public static Date convertFrom(String origin, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        try {
            return format.parse(origin);
        } catch (ParseException | NullPointerException e) {
            //e.printStackTrace();
            return null;
        }
    }

    /**
     * Date转换为字符串
     *
     * @param date    日期
     * @param pattern 日期格式
     * @return
     */
    public static String convertToString(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

}
