package com.punuo.pet.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by han.chen.
 * Date on 2019-07-11.
 **/
public class PetData implements Parcelable {

    /**
     * petname : miaomiao
     * avatar : http://pet.qinqingonline.com/uploads/17816890870/397a339c578e4095f451c5da04826d3b.jpg
     * type : 1
     * age : 1
     * breed : 1
     * weight : 15.2
     * unit : kg
     * birth : 2019-1-1
     */

    @SerializedName("petname")
    public String petname;
    @SerializedName("avatar")
    public String avatar;
    @SerializedName("type")
    public int type;
    @SerializedName("age")
    public int age;
    @SerializedName("breed")
    public int breed;
    @SerializedName("weight")
    public double weight;
    @SerializedName("unit")
    public String unit;
    @SerializedName("birth")
    public String birth;

    protected PetData(Parcel in) {
        petname = in.readString();
        avatar = in.readString();
        type = in.readInt();
        age = in.readInt();
        breed = in.readInt();
        weight = in.readDouble();
        unit = in.readString();
        birth = in.readString();
    }

    public static final Creator<PetData> CREATOR = new Creator<PetData>() {
        @Override
        public PetData createFromParcel(Parcel in) {
            return new PetData(in);
        }

        @Override
        public PetData[] newArray(int size) {
            return new PetData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(petname);
        dest.writeString(avatar);
        dest.writeInt(type);
        dest.writeInt(age);
        dest.writeInt(breed);
        dest.writeDouble(weight);
        dest.writeString(unit);
        dest.writeString(birth);
    }
}
