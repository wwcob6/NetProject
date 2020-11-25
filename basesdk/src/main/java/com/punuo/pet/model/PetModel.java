package com.punuo.pet.model;

import com.google.gson.annotations.SerializedName;
import com.punuo.sys.sdk.model.BaseModel;

import java.util.List;

/**
 * Created by han.chen.
 * Date on 2019-07-11.
 **/
public class PetModel extends BaseModel {

    @SerializedName("pets")
    public List<PetData> mPets;
}
