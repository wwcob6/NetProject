package com.punuo.sys.sdk;

import com.punuo.pet.model.PetData;

/**
 * Created by han.chen.
 * Date on 2019-06-22.
 **/
public class Constant {

    /**
     * 微信APP_ID
     */
    public static final String WX_APP_ID = "wx52df0b92ba3388e7";
    public static final String WX_APP_SECRET = "03cae9438b751b256495a89e9cdc758f";

    /**
     * 首页选中的pet
     */
    public static PetData petData;

    /**
     * 日常护理中九种不同搭配的alarm的action和目标时间。
     */
    public static final String ALARM_ONE = "com.punuo.pet.home.ALARM_ONE";
    public static final String ALARM_TWO="com.punuo.pet.home.ALARM_TWO";
    public static final String ALARM_THREE="com.punuo.pet.home.ALARM_THREE";
    public static final String ALARM_FOUR="com.punuo.pet.home.ALARM_FOUR";
    public static final String ALARM_FIVE="com.punuo.pet.home.ALARM_FIVE";
    public static final String ALARM_SIX="com.punuo.pet.home.ALARM_SIX";
    public static final String ALARM_SEVEN="com.punuo.pet.home.ALARM_SEVEN";
    public static final String ALARM_EIGHT="com.punuo.pet.home.ALARM_EIGHT";
    public static long bathDateAndTime;
    public static long checkDateAndTime;
    public static long buyDateAndTime;
    public static long vitroDateAndTime;
    public static long vivoDateAndTime;
    public static long vaccineDateAndTime;
    public static long beautyDateAndTime;
    public static long walkDateAndTime;
}
