package com.mifos.objects.noncore;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Tarun on 07-08-17.
 */

public class IdentifierCreationResponse implements Parcelable {

    private int clientId;

    private int officeId;

    private int resourceId;

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public int getOfficeId() {
        return officeId;
    }

    public void setOfficeId(int officeId) {
        this.officeId = officeId;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.clientId);
        dest.writeInt(this.officeId);
        dest.writeInt(this.resourceId);
    }

    public IdentifierCreationResponse() {
    }

    protected IdentifierCreationResponse(Parcel in) {
        this.clientId = in.readInt();
        this.officeId = in.readInt();
        this.resourceId = in.readInt();
    }

    public static final Parcelable.Creator<IdentifierCreationResponse> CREATOR = new
            Parcelable.Creator<IdentifierCreationResponse>() {
                @Override
                public IdentifierCreationResponse createFromParcel(Parcel source) {
                    return new IdentifierCreationResponse(source);
                }

                @Override
                public IdentifierCreationResponse[] newArray(int size) {
                    return new IdentifierCreationResponse[size];
                }
            };
}
