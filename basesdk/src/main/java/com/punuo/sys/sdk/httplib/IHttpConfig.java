package com.punuo.sys.sdk.httplib;

/**
 * Created by han.chen.
 * Date on 2019/4/23.
 **/
public interface IHttpConfig {

    String getHost();

    int getPort();

    boolean isUseHttps();

    String getUserAgent();

    String getPrefixPath();
}
