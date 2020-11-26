package com.punuo.sys.net.push;

import com.google.gson.annotations.SerializedName;
import com.punuo.sys.sdk.model.BaseModel;

public class GetStationsModel extends BaseModel {
    @SerializedName("stations")
    public Location stations;
    public static class Location {
        @SerializedName("lng")
        public double longitude;

        @SerializedName("lat")
        public double latitude;
    }
}

