package com.mifos.objects.noncore;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Rajan Maurya on 01/10/16.
 */

public class IdentifierPayload implements Parcelable {

    @SerializedName("documentTypeId")
    Integer documentTypeId;

    @SerializedName("documentKey")
    String documentKey;

    @SerializedName("status")
    String status;

    @SerializedName("description")
    String description;

    public Integer getDocumentTypeId() {
        return documentTypeId;
    }

    public void setDocumentTypeId(Integer documentTypeId) {
        this.documentTypeId = documentTypeId;
    }

    public String getDocumentKey() {
        return documentKey;
    }

    public void setDocumentKey(String documentKey) {
        this.documentKey = documentKey;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.documentTypeId);
        dest.writeValue(this.documentKey);
        dest.writeValue(this.status);
        dest.writeString(this.description);
    }

    public IdentifierPayload() {
    }

    protected IdentifierPayload(Parcel in) {
        this.documentTypeId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.documentKey = (String) in.readValue(String.class.getClassLoader());
        this.status = (String) in.readValue(String.class.getClassLoader());
        this.description = in.readString();
    }

    public static final Parcelable.Creator<IdentifierPayload> CREATOR =
            new Parcelable.Creator<IdentifierPayload>() {
        @Override
        public IdentifierPayload createFromParcel(Parcel source) {
            return new IdentifierPayload(source);
        }

        @Override
        public IdentifierPayload[] newArray(int size) {
            return new IdentifierPayload[size];
        }
    };
}
