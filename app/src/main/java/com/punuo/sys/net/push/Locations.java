package com.punuo.sys.net.push;

import com.google.gson.annotations.SerializedName;

public class Locations {
    @SerializedName("LONGITUDE")
    public double longitude;

    @SerializedName("LATITUDE")
    public double latitude;

    @SerializedName("Data")
    public int paraData;
}
