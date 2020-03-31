/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.api.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

public class ClientsAttendance implements Parcelable {
    private int attendanceType;
    private int clientId;

    public ClientsAttendance(int clientId, int attendanceType) {
        this.attendanceType = attendanceType;
        this.clientId = clientId;
    }

    public int getAttendanceType() {
        return attendanceType;
    }

    public void setAttendanceType(int attendanceType) {
        this.attendanceType = attendanceType;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.attendanceType);
        dest.writeInt(this.clientId);
    }

    public ClientsAttendance() {
    }

    protected ClientsAttendance(Parcel in) {
        this.attendanceType = in.readInt();
        this.clientId = in.readInt();
    }

    public static final Parcelable.Creator<ClientsAttendance> CREATOR = new
            Parcelable.Creator<ClientsAttendance>() {
        @Override
        public ClientsAttendance createFromParcel(Parcel source) {
            return new ClientsAttendance(source);
        }

        @Override
        public ClientsAttendance[] newArray(int size) {
            return new ClientsAttendance[size];
        }
    };
}
