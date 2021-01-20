package com.punuo.sys.net.push.model;

import com.google.gson.annotations.SerializedName;
import com.punuo.sys.net.Stations;
import com.punuo.sys.net.push.Locations;
import com.punuo.sys.sdk.model.BaseModel;

import java.util.List;

public class GetHistoryTrackModel extends BaseModel {
    @SerializedName("stations")
    public List<Locations> locationsList;
}
