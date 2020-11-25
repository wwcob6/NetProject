package com.punuo.sys.sdk.account.request;

import com.punuo.sys.sdk.httplib.BaseRequest;
import com.punuo.sys.sdk.model.UserInfoModel;

/**
 * Created by han.chen.
 * Date on 2019-06-28.
 **/
public class GetUserInfoRequest extends BaseRequest<UserInfoModel> {
    public GetUserInfoRequest() {
        setRequestType(RequestType.GET);
        setRequestPath("/users/getUserInfo");
    }
}
