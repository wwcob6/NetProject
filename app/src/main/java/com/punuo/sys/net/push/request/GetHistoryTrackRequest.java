package com.punuo.sys.net.push.request;

import com.punuo.sys.net.push.BaseRequest;

public class GetHistoryTrackRequest extends BaseRequest {

        public GetHistoryTrackRequest() {
            setRequestPath("/5G_info/");
            setRequestType(RequestType.GET);
        }

}
