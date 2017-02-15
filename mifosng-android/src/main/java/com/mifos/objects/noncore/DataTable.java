/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.noncore;

import android.os.Parcel;
import android.os.Parcelable;

import com.mifos.api.local.MifosBaseModel;
import com.mifos.api.local.MifosDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ishankhanna on 16/06/14.
 */
@Table(database = MifosDatabase.class)
@ModelContainer
public class DataTable extends MifosBaseModel implements Parcelable {

    @Column
    String applicationTableName;

    List<ColumnHeader> columnHeaderData = new ArrayList<>();

    @PrimaryKey
    String registeredTableName;

    public List<ColumnHeader> getColumnHeaderData() {
        return columnHeaderData;
    }

    public void setColumnHeaderData(List<ColumnHeader> columnHeaderData) {
        this.columnHeaderData = columnHeaderData;
    }

    public String getApplicationTableName() {
        return applicationTableName;
    }

    public void setApplicationTableName(String applicationTableName) {
        this.applicationTableName = applicationTableName;
    }

    public String getRegisteredTableName() {
        return registeredTableName;
    }

    public void setRegisteredTableName(String registeredTableName) {
        this.registeredTableName = registeredTableName;
    }

    @Override
    public String toString() {
        return "DataTable{" +
                "applicationTableName='" + applicationTableName + '\'' +
                ", columnHeaderData=" + columnHeaderData +
                ", registeredTableName='" + registeredTableName + '\'' +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.applicationTableName);
        dest.writeList(this.columnHeaderData);
        dest.writeString(this.registeredTableName);
    }

    public DataTable() {
    }

    protected DataTable(Parcel in) {
        this.applicationTableName = in.readString();
        this.columnHeaderData = new ArrayList<ColumnHeader>();
        in.readList(this.columnHeaderData, ColumnHeader.class.getClassLoader());
        this.registeredTableName = in.readString();
    }

    public static final Parcelable.Creator<DataTable> CREATOR =
            new Parcelable.Creator<DataTable>() {
        @Override
        public DataTable createFromParcel(Parcel source) {
            return new DataTable(source);
        }

        @Override
        public DataTable[] newArray(int size) {
            return new DataTable[size];
        }
    };
}
