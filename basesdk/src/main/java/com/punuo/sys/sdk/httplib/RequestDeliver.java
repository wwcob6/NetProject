package com.punuo.sys.sdk.httplib;

import okhttp3.Response;

/**
 * Created by han.chen.
 * Date on 2019/4/23.
 **/
public interface RequestDeliver {
    void deliverResponse(Response response, String parse);

    void deliverError(Exception e);
}
