package com.punuo.sys.sdk.httplib.upload;


import com.punuo.sys.sdk.httplib.BaseRequest;

import okhttp3.MediaType;

/**
 * Created by han.chen.
 * Date on 2019/5/27.
 **/
public class UploadPictureRequest extends BaseRequest<UploadResult> {

    public UploadPictureRequest() {
        setRequestType(RequestType.UPLOAD);
        setRequestPath("/addPics");
        contentType(MediaType.parse("image/*"));
    }
}
