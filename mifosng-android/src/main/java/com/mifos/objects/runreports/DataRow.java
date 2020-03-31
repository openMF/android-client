package com.mifos.objects.runreports;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Tarun on 03-08-17.
 */

public class DataRow implements Parcelable {

    private List<String> row;

    public List<String> getRow() {
        return row;
    }

    public void setRow(List<String> row) {
        this.row = row;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(this.row);
    }

    public DataRow() {
    }

    protected DataRow(Parcel in) {
        this.row = in.createStringArrayList();
    }

    public static final Parcelable.Creator<DataRow> CREATOR = new Parcelable.Creator<DataRow>() {
        @Override
        public DataRow createFromParcel(Parcel source) {
            return new DataRow(source);
        }

        @Override
        public DataRow[] newArray(int size) {
            return new DataRow[size];
        }
    };
}
