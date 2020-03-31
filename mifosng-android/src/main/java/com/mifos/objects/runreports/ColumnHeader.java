package com.mifos.objects.runreports;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Tarun on 03-08-17.
 */

public class ColumnHeader implements Parcelable {

    private String columnDisplayType;

    private String columnName;

    private String columnType;

    //No resource to corroborate whether it's a List of string or some other type.
    private List<String> columnValues;

    private boolean isColumnNullable;

    private boolean isColumnPrimaryKey;

    public String getColumnDisplayType() {
        return columnDisplayType;
    }

    public void setColumnDisplayType(String columnDisplayType) {
        this.columnDisplayType = columnDisplayType;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public List<String> getColumnValues() {
        return columnValues;
    }

    public void setColumnValues(List<String> columnValues) {
        this.columnValues = columnValues;
    }

    public boolean isIsColumnNullable() {
        return isColumnNullable;
    }

    public void setColumnNullable(boolean columnNullable) {
        isColumnNullable = columnNullable;
    }

    public boolean isIsColumnPrimaryKey() {
        return isColumnPrimaryKey;
    }

    public void setColumnPrimaryKey(boolean columnPrimaryKey) {
        isColumnPrimaryKey = columnPrimaryKey;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.columnDisplayType);
        dest.writeString(this.columnName);
        dest.writeString(this.columnType);
        dest.writeStringList(this.columnValues);
        dest.writeByte(this.isColumnNullable ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isColumnPrimaryKey ? (byte) 1 : (byte) 0);
    }

    public ColumnHeader() {
    }

    protected ColumnHeader(Parcel in) {
        this.columnDisplayType = in.readString();
        this.columnName = in.readString();
        this.columnType = in.readString();
        this.columnValues = in.createStringArrayList();
        this.isColumnNullable = in.readByte() != 0;
        this.isColumnPrimaryKey = in.readByte() != 0;
    }

    public static final Parcelable.Creator<ColumnHeader> CREATOR = new
            Parcelable.Creator<ColumnHeader>() {
        @Override
        public ColumnHeader createFromParcel(Parcel source) {
            return new ColumnHeader(source);
        }

        @Override
        public ColumnHeader[] newArray(int size) {
            return new ColumnHeader[size];
        }
    };
}
