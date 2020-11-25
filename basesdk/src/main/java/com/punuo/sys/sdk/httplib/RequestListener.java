package com.punuo.sys.sdk.httplib;

/**
 * Created by han.chen.
 * Date on 2019/4/23.
 **/
public interface RequestListener<T> {
    void onComplete();
    void onSuccess(T result);
    void onError(Exception e);
}
