package com.mifos.objects.runreports;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Tarun on 03-08-17.
 */

public class FullParameterListResponse implements Parcelable {

    private List<ColumnHeader> columnHeaders;

    private List<DataRow> data;

    public List<ColumnHeader> getColumnHeaders() {
        return columnHeaders;
    }

    public void setColumnHeaders(List<ColumnHeader> columnHeaders) {
        this.columnHeaders = columnHeaders;
    }

    public List<DataRow> getData() {
        return data;
    }

    public void setData(List<DataRow> data) {
        this.data = data;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.columnHeaders);
        dest.writeTypedList(this.data);
    }

    public FullParameterListResponse() {
    }

    protected FullParameterListResponse(Parcel in) {
        this.columnHeaders = in.createTypedArrayList(ColumnHeader.CREATOR);
        this.data = in.createTypedArrayList(DataRow.CREATOR);
    }

    public static final Parcelable.Creator<FullParameterListResponse> CREATOR = new
            Parcelable.Creator<FullParameterListResponse>() {
        @Override
        public FullParameterListResponse createFromParcel(Parcel source) {
            return new FullParameterListResponse(source);
        }

        @Override
        public FullParameterListResponse[] newArray(int size) {
            return new FullParameterListResponse[size];
        }
    };
}
