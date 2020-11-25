package com.punuo.pet.request;

import com.punuo.pet.model.PetModel;
import com.punuo.sys.sdk.httplib.BaseRequest;

/**
 * Created by han.chen.
 * Date on 2019-07-11.
 **/
public class GetPetInfoRequest extends BaseRequest<PetModel> {

    public GetPetInfoRequest() {
        setRequestPath("/pets/getPetInfo");
        setRequestType(RequestType.GET);
    }
}
