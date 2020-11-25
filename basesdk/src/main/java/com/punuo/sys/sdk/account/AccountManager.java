package com.punuo.sys.sdk.account;

import android.text.TextUtils;

import com.punuo.sys.sdk.httplib.JsonUtil;
import com.punuo.sys.sdk.model.UserInfo;
import com.punuo.sys.sdk.util.MMKVUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by han.chen.
 * Date on 2019-06-15.
 **/
public class AccountManager {

    private static boolean sIsLogined = false;
    private static String sSession = "";
    private static UserInfo sUserInfo = null;
    public static boolean isLoginned() {
        if (!sIsLogined || TextUtils.isEmpty(sSession)) {
            sSession = MMKVUtil.getString("wsq_pref_session");
            sIsLogined = !TextUtils.equals(sSession, "");
        }
        return sIsLogined;
    }

    public static void setUserInfo(UserInfo userInfo) {
        if (userInfo != null) {
            sUserInfo = userInfo;
            MMKVUtil.setString("wsq_pref_user", JsonUtil.toJson(userInfo));
            //重新设置本地用户信息时候会发出通知具体页面可以接收做相应的处理
            EventBus.getDefault().post(userInfo);
        }
    }

    public static UserInfo getUserInfo() {
        if (sUserInfo == null) {
            sUserInfo = (UserInfo) JsonUtil.fromJson(MMKVUtil.getString("wsq_pref_user"), UserInfo.class);
            sUserInfo = sUserInfo == null ? new UserInfo() : sUserInfo;
        }

        return sUserInfo;
    }

    public static String getUserName() {
        String userName = getUserInfo().userName;
        if (TextUtils.isEmpty(userName)) {
            userName = getSession();
        }
        return userName;
    }

    public static void setSession(String session) {
        sSession = session;
        MMKVUtil.setString("wsq_pref_session", session);
    }

    public static String getSession() {
        if (TextUtils.isEmpty(sSession)) {
            sSession = MMKVUtil.getString("wsq_pref_session");
        }
        return sSession;
    }


    public static void clearAccountData() {
        sIsLogined = false;
        sSession = "";
        sUserInfo = null;
        MMKVUtil.removeData("wsq_pref_session");
        MMKVUtil.removeData("wsq_pref_user");
    }


}
