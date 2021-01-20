package com.punuo.sys.net.push.request;

import com.punuo.sys.net.push.BaseRequest;
import com.punuo.sys.sdk.model.BaseModel;

public class PushRequest extends BaseRequest<BaseModel> {

    public PushRequest(){
        setRequestPath("/DY_DATE/addDynamicData");
        setRequestType(RequestType.POST);
    }
}
