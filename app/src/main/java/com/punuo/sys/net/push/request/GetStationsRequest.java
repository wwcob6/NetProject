package com.punuo.sys.net.push.request;

import com.punuo.sys.net.push.model.GetStationsModel;
import com.punuo.sys.sdk.httplib.BaseRequest;

public class GetStationsRequest extends BaseRequest<GetStationsModel> {
    public GetStationsRequest() {
        setRequestPath("/5G_info/getOnePoint");
        setRequestType(RequestType.GET);
    }
}
