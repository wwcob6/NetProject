package com.punuo.sys.net.push;

import com.google.gson.annotations.SerializedName;
import com.punuo.sys.sdk.model.BaseModel;

public class GetStationsModel extends BaseModel {
    @SerializedName("stations")
    public getstationlocationmodel.location stations;
    public static class location {
        @SerializedName("longitude")
        public double longitude;

        @SerializedName("latitude")
        public double latitude;
    }
}

