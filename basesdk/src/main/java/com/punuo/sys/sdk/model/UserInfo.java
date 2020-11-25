package com.punuo.sys.sdk.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by han.chen.
 * Date on 2019-06-15.
 **/
public class UserInfo implements Parcelable {
    @SerializedName("userName")
    public String userName;

    @SerializedName("avatar")
    public String avatar;

    @SerializedName("nickName")
    public String nickName;

    @SerializedName("birth")
    public String birth;

    @SerializedName("gender")
    public int gender;

    @SerializedName("userid")
    public String userId;

    public UserInfo() {

    }

    protected UserInfo(Parcel in) {
        userName = in.readString();
        avatar = in.readString();
        nickName = in.readString();
        birth = in.readString();
        gender = in.readInt();
        userId = in.readString();
    }

    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel in) {
            return new UserInfo(in);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userName);
        dest.writeString(avatar);
        dest.writeString(nickName);
        dest.writeString(birth);
        dest.writeInt(gender);
        dest.writeString(userId);
    }
}
