package com.punuo.sys.net.push.request;

import com.punuo.sys.net.push.BaseRequest;
import com.punuo.sys.net.push.model.GetHistoryTrackModel;

public class GetHistoryTrackRequest extends BaseRequest<GetHistoryTrackModel> {

        public GetHistoryTrackRequest() {
            setRequestPath("/DY_DATE/getMovingLine");
            setRequestType(RequestType.GET);
        }

}
