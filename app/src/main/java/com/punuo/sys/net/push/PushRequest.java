package com.punuo.sys.net.push;

import com.punuo.sys.sdk.model.BaseModel;

public class PushRequest extends BaseRequest<BaseModel> {

    public PushRequest(){
        setRequestPath("/DY_DATE/addDynamicData");
        setRequestType(RequestType.POST);
    }
}
