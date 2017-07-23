package com.gzpsc.util;

/**
 * Created by ZJH on 2017/7/22.
 */
public class StringToNumberUtil {

    public static Float StringToFloat(String str) {
        try {
            return Float.valueOf(str);
        } catch (NumberFormatException e) {
            return 0f;
        }
    }

    public static Double StringToDouble(String str) {
        try {
            return Double.valueOf(str);
        } catch (NumberFormatException e) {
            return 0d;
        }
    }

    public static Integer StringToInteger(String str) {
        try {
            return Integer.valueOf(str);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

}
