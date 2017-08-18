package com.mifos.objects.organisation;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mayankjindal on 18/08/17.
 */

public class ProductSharing implements Parcelable {

    @NonNull
    @SerializedName("id")
    Integer id;

    @SerializedName("name")
    String name;

    @SerializedName("shortName")
    String shortName;

    @SerializedName("totalShares")
    String totalShares;

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

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getTotalShares() {
        return totalShares;
    }

    public void setTotalShares(String totalShares) {
        this.totalShares = totalShares;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.name);
        dest.writeString(this.shortName);
        dest.writeString(this.totalShares);
    }

    public ProductSharing() {
    }

    protected ProductSharing(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.name = in.readString();
        this.shortName = in.readString();
        this.totalShares = in.readString();
    }

    @Override
    public String toString() {
        return "ProductSharing{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", shortName='" + shortName + '\'' +
                ", totalShares='" + totalShares + '\'' +
                '}';
    }

    public static final Parcelable.Creator<ProductSharing> CREATOR =
            new Parcelable.Creator<ProductSharing>() {
                @Override
                public ProductSharing createFromParcel(Parcel source) {
                    return new ProductSharing(source);
                }

                @Override
                public ProductSharing[] newArray(int size) {
                    return new ProductSharing[size];
                }
            };
}
