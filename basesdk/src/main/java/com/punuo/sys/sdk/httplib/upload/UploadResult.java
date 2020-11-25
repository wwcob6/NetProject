package com.punuo.sys.sdk.httplib.upload;

import com.google.gson.annotations.SerializedName;

/**
 * Created by han.chen.
 * Date on 2019/5/27.
 **/
public class UploadResult {

    @SerializedName("message")
    public String message;

    @SerializedName("success")
    public boolean success;

    @SerializedName("url")
    public String url;
}
