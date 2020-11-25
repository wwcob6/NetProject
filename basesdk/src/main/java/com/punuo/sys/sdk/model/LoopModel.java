package com.punuo.sys.sdk.model;

import java.util.List;

public class LoopModel extends BaseModel {

    public LoopModel(List<String> list) {
        mList = list;
    }

    public List<String> mList;


    public static LoopModel newInstance(List<String> list) {
        return new LoopModel(list);
    }
}