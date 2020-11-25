package com.punuo.sys.sdk.account;

import com.punuo.sys.sdk.account.request.GetUserInfoRequest;
import com.punuo.sys.sdk.httplib.HttpManager;
import com.punuo.sys.sdk.httplib.RequestListener;
import com.punuo.sys.sdk.model.UserInfoModel;
import com.punuo.sys.sdk.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by han.chen.
 * Date on 2019-06-28.
 **/
public class UserManager {
    private static GetUserInfoRequest mGetUserInfoRequest;

    public static void getUserInfo(String phone) {
        if (mGetUserInfoRequest != null && !mGetUserInfoRequest.isFinish()) {
            return;
        }
        mGetUserInfoRequest = new GetUserInfoRequest();
        mGetUserInfoRequest.addUrlParam("userName", phone);
        mGetUserInfoRequest.setRequestListener(new RequestListener<UserInfoModel>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(UserInfoModel result) {
                if (result == null) {
                    return;
                }
                if (result.userInfo != null) {
                    AccountManager.setUserInfo(result.userInfo);
                }
            }

            @Override
            public void onError(Exception e) {
                ToastUtils.showToast("拉取用户信息失败");
            }
        });
        HttpManager.addRequest(mGetUserInfoRequest);
    }
}
