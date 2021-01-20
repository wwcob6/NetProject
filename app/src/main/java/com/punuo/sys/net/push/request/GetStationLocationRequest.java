package com.punuo.sys.net.push.request;

import com.punuo.sys.net.push.model.getstationlocationmodel;
import com.punuo.sys.sdk.httplib.BaseRequest;

public class GetStationLocationRequest extends BaseRequest<getstationlocationmodel> {
    public GetStationLocationRequest() {
        setRequestPath("/5G_info/getLongitudeById");
        setRequestType(RequestType.GET);
    }
}
