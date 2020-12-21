package com.punuo.sys.net.push;

import com.google.gson.annotations.SerializedName;
import com.punuo.sys.net.Stations;
import com.punuo.sys.sdk.model.BaseModel;


import java.util.List;

public class GetStationsModel extends BaseModel {
    @SerializedName("stations")
    public List<Stations>  stationsList;
    /*public static class Location {
        @SerializedName("lng")
        public double longitude;

        @SerializedName("lat")
        public double latitude;
    }*/
}

