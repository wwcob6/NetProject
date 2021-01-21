package com.punuo.sys.net.push;

import com.google.gson.annotations.SerializedName;

public class Locations {
    @SerializedName("lng")
    public double longitude;

    @SerializedName("lat")
    public double latitude;

    @SerializedName("paraData")
    public int paraData;
}
