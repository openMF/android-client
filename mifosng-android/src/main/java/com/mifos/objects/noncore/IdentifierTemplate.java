package com.mifos.objects.noncore;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rajan Maurya on 01/10/16.
 */

public class IdentifierTemplate implements Parcelable {

    @SerializedName("allowedDocumentTypes")
    List<DocumentType> allowedDocumentTypes = new ArrayList<>();

    public List<DocumentType> getAllowedDocumentTypes() {
        return allowedDocumentTypes;
    }

    public void setAllowedDocumentTypes(List<DocumentType> allowedDocumentTypes) {
        this.allowedDocumentTypes = allowedDocumentTypes;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.allowedDocumentTypes);
    }

    public IdentifierTemplate() {
    }

    protected IdentifierTemplate(Parcel in) {
        this.allowedDocumentTypes = in.createTypedArrayList(DocumentType.CREATOR);
    }

    public static final Parcelable.Creator<IdentifierTemplate> CREATOR = new Parcelable
            .Creator<IdentifierTemplate>() {
        @Override
        public IdentifierTemplate createFromParcel(Parcel source) {
            return new IdentifierTemplate(source);
        }

        @Override
        public IdentifierTemplate[] newArray(int size) {
            return new IdentifierTemplate[size];
        }
    };
}
