package com.mifos.objects.templates.clients;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mayankjindal on 13/12/16.
 */

public class IncomeOrLiabilityAccount implements Parcelable {

    @SerializedName("id")
    Integer id;

    @SerializedName("name")
    String name;

    @SerializedName("glCode")
    String glCode;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGlCode() {
        return glCode;
    }

    public void setGlCode(String glCode) {
        this.glCode = glCode;
    }

    @Override
    public String toString() {
        return "IncomeOrLiabilityAccount{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", glCode='" + glCode + '\'' +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.name);
        dest.writeString(this.glCode);
    }

    public IncomeOrLiabilityAccount() {
    }

    protected IncomeOrLiabilityAccount(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.name = in.readString();
        this.glCode = in.readString();
    }

    public static final Parcelable.Creator<IncomeOrLiabilityAccount> CREATOR =
            new Parcelable.Creator<IncomeOrLiabilityAccount>() {
                @Override
                public IncomeOrLiabilityAccount createFromParcel(Parcel source) {
                    return new IncomeOrLiabilityAccount(source);
                }

                @Override
                public IncomeOrLiabilityAccount[] newArray(int size) {
                    return new IncomeOrLiabilityAccount[size];
                }
            };
}
