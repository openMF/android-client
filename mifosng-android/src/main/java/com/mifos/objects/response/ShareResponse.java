package com.mifos.objects.response;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Created by mayankjindal on 21/08/17.
 */

public class ShareResponse implements Parcelable {

    @NonNull
    @SerializedName("resourceId")
    Integer resourceId;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.resourceId);
    }

    public ShareResponse() {
    }

    protected ShareResponse(Parcel in) {
        this.resourceId = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<ShareResponse> CREATOR =
            new Parcelable.Creator<ShareResponse>() {
                @Override
                public ShareResponse createFromParcel(Parcel source) {
                    return new ShareResponse(source);
                }

                @Override
                public ShareResponse[] newArray(int size) {
                    return new ShareResponse[size];
                }
            };
}
