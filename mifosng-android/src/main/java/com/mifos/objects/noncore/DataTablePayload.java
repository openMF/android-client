package com.mifos.objects.noncore;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.mifos.api.local.MifosBaseModel;
import com.mifos.api.local.MifosDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import java.util.HashMap;

/**
 * Created by Tarun on 1/28/2017.
 */
@Table(database = MifosDatabase.class)
@ModelContainer
public class DataTablePayload extends MifosBaseModel implements Parcelable {

    @PrimaryKey(autoincrement = true)
    transient Integer id;

    @Column
    transient Long clientCreationTime;

    // this field belongs to database table only for saving the
    // data table string;
    @Column
    transient String dataTableString;

    @SerializedName("registeredTableName")
    @Column
    String registeredTableName;

    @SerializedName("data")
    HashMap<String, Object> data;

    public Long getClientCreationTime() {
        return clientCreationTime;
    }

    public void setClientCreationTime(Long clientCreationTime) {
        this.clientCreationTime = clientCreationTime;
    }

    public String getDataTableString() {
        return dataTableString;
    }

    public void setDataTableString(String dataTableString) {
        this.dataTableString = dataTableString;
    }

    public String getRegisteredTableName() {
        return registeredTableName;
    }

    public void setRegisteredTableName(String registeredTableName) {
        this.registeredTableName = registeredTableName;
    }

    public HashMap<String, Object> getData() {
        return data;
    }

    public void setData(HashMap<String, Object> data) {
        this.data = data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.registeredTableName);
        dest.writeSerializable(this.data);
    }

    public DataTablePayload() {
    }

    protected DataTablePayload(Parcel in) {
        this.registeredTableName = in.readString();
        this.data = (HashMap<String, Object>) in.readSerializable();
    }

    public static final Parcelable.Creator<DataTablePayload> CREATOR = new
            Parcelable.Creator<DataTablePayload>() {
        @Override
        public DataTablePayload createFromParcel(Parcel source) {
            return new DataTablePayload(source);
        }

        @Override
        public DataTablePayload[] newArray(int size) {
            return new DataTablePayload[size];
        }
    };
}
