package com.mifos.objects.client;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Tarun on 13-08-17.
 */

public class ChargeCreationResponse implements Parcelable {
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

    public ChargeCreationResponse() {
    }

    protected ChargeCreationResponse(Parcel in) {
        this.clientId = in.readInt();
        this.officeId = in.readInt();
        this.resourceId = in.readInt();
    }

    public static final Parcelable.Creator<ChargeCreationResponse> CREATOR = new
            Parcelable.Creator<ChargeCreationResponse>() {
                @Override
                public ChargeCreationResponse createFromParcel(Parcel source) {
                    return new ChargeCreationResponse(source);
                }

                @Override
                public ChargeCreationResponse[] newArray(int size) {
                    return new ChargeCreationResponse[size];
                }
            };
}
