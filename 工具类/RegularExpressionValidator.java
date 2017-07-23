package com.gzpsc.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 云翔 on 2016/10/21.
 */
public class RegularExpressionValidator {
    private static Pattern p;
    private static Matcher m;

    /**
     * 判断字符串是否为手机号码
     *
     * @param phone
     */
    public static boolean isPhone(String phone, boolean canEmpty) {
        if (judgeNULL(canEmpty, phone)){
            return true;
        }
        p = Pattern.compile("1(3[0-9]|5[0-35-9]|8[025-9]|4[1-9])\\d{8}");
        m = p.matcher(phone);
        return m.matches();
    }

    /**
     * 判断字符串是否为手机号码或固定电话
     *
     * @param telephone
     */
    public static boolean isTelephoneOrPhone(String telephone, boolean canEmpty) {
        if (judgeNULL(canEmpty, telephone)){
            return true;
        }
        p = Pattern.compile("(1(3[0-9]|5[0-35-9]|8[0235-9]|4[1-9])\\d{8})|(^0\\d{2}-?\\d{8}$)|(^0\\d{3}-?\\d{7}$)|(^\\(0\\d{2}\\)-?\\d{8}$)|(^\\(0\\d{3}\\)-?\\d{7}$)");
        m = p.matcher(telephone);
        return m.matches();
    }


    /**
     * 判断是否为邮箱
     * @param mail
     * @param canEmpty
     * @return
     */
    public static boolean isMail(String mail, boolean canEmpty) {
        if (judgeNULL(canEmpty, mail)){
            return true;
        }
        p = Pattern.compile("[A-Za-z\\d]+([-_.][A-Za-z\\d]+)*@([A-Za-z\\d]+[-.])+[A-Za-z\\d]{2,4}");
        m = p.matcher(mail);
        return m.matches();
    }

    /**
     * 判断字符串是否为身份证号码
     *
     * @param driverIdentityNumber
     */
    public static boolean isDriverIdentityNumber(String driverIdentityNumber, boolean canEmpty) {
        if (judgeNULL(canEmpty, driverIdentityNumber)){
            return true;
        }
        p = Pattern.compile("[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$|^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X)");
        m = p.matcher(driverIdentityNumber);
        return m.matches();
    }

    /**
     * 判断字符串是否为车牌号
     *
     * @param vehicleIdentity
     */
    public static boolean isVehicleIdentity(String vehicleIdentity, boolean canEmpty) {
        if (judgeNULL(canEmpty, vehicleIdentity)){
            return true;
        }
        p = Pattern.compile("[\\u4e00-\\u9fa5]{1}[A-Z]{1}[A-Z_0-9]{5}");
        m = p.matcher(vehicleIdentity);
        return m.matches();
    }

    /**
     * 判断最大长度
     * @param canEmpty
     * @param str
     * @param maxLength
     * @return
     */
    public static boolean judgeMaxLength(boolean canEmpty, String str, int maxLength){
        if (judgeNULL(canEmpty, str)){
            return true;
        }
        if (str.length() > maxLength){
            return false;
        }
        return true;
    }

    /**
     *
     * @param canEmpty 是否可以为空
     * @param str
     * @return
     */
    private static boolean judgeNULL(boolean canEmpty, String str){
        if (canEmpty){
            if (str == null || "".equals(str)){
                return true;
            }
        }
        return false;
    }


}
