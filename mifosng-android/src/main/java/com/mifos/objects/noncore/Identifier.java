/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.noncore;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ishankhanna on 03/07/14.
 */
public class Identifier implements Parcelable {

    @SerializedName("id")
    Integer id;

    @SerializedName("clientId")
    Integer clientId;

    @SerializedName("documentKey")
    String documentKey;

    @SerializedName("documentType")
    DocumentType documentType;

    @SerializedName("description")
    String description;

    @SerializedName("status")
    String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public String getDocumentKey() {
        return documentKey;
    }

    public void setDocumentKey(String documentKey) {
        this.documentKey = documentKey;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
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
        dest.writeValue(this.id);
        dest.writeValue(this.clientId);
        dest.writeString(this.documentKey);
        dest.writeParcelable(this.documentType, flags);
        dest.writeString(this.description);
        dest.writeString(this.status);
    }

    public Identifier() {
    }

    protected Identifier(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.clientId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.documentKey = in.readString();
        this.documentType = in.readParcelable(DocumentType.class.getClassLoader());
        this.description = in.readString();
        this.status = in.readString();
    }

    public static final Parcelable.Creator<Identifier> CREATOR = new Parcelable
            .Creator<Identifier>() {
        @Override
        public Identifier createFromParcel(Parcel source) {
            return new Identifier(source);
        }

        @Override
        public Identifier[] newArray(int size) {
            return new Identifier[size];
        }
    };
}
