package com.punuo.sys.sdk.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by han.chen.
 * Date on 2019-06-15.
 **/
public class BaseModel extends PNBaseModel {

    @SerializedName("success")
    public boolean success;

    @SerializedName("message")
    public String message;
}
