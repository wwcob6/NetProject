package com.punuo.sys.sdk.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by han.chen.
 * Date on 2019-06-28.
 **/
public class UserInfoModel extends BaseModel implements Parcelable {

    @SerializedName("userInfo")
    public UserInfo userInfo;

    protected UserInfoModel(Parcel in) {
        userInfo = in.readParcelable(UserInfo.class.getClassLoader());
    }

    public static final Creator<UserInfoModel> CREATOR = new Creator<UserInfoModel>() {
        @Override
        public UserInfoModel createFromParcel(Parcel in) {
            return new UserInfoModel(in);
        }

        @Override
        public UserInfoModel[] newArray(int size) {
            return new UserInfoModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(userInfo, flags);
    }
}
