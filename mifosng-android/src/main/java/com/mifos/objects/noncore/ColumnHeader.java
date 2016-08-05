/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.noncore;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by ishankhanna on 16/06/14.
 */
public class ColumnHeader implements Parcelable {

    /**
     * columnCode will only be returned if columnDisplayType = "CODELOOKUP"
     * and null otherwise
     */
    String columnCode;
    String columnDisplayType;
    Integer columnLength;
    String columnName;
    String columnType;
    Boolean isColumnNullable;
    Boolean isColumnPrimaryKey;

    /**
     * columnValues are actually Code Values that are either created by
     * system or defined manually by users
     */

    List<ColumnValue> columnValues;

    public String getColumnCode() {
        return columnCode;
    }

    public void setColumnCode(String columnCode) {
        this.columnCode = columnCode;
    }

    public String getColumnDisplayType() {
        return columnDisplayType;
    }

    public void setColumnDisplayType(String columnDisplayType) {
        this.columnDisplayType = columnDisplayType;
    }

    public Integer getColumnLength() {
        return columnLength;
    }

    public void setColumnLength(Integer columnLength) {
        this.columnLength = columnLength;
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

    public Boolean getIsColumnNullable() {
        return isColumnNullable;
    }

    public void setIsColumnNullable(Boolean isColumnNullable) {
        this.isColumnNullable = isColumnNullable;
    }

    public Boolean getIsColumnPrimaryKey() {
        return isColumnPrimaryKey;
    }

    public void setIsColumnPrimaryKey(Boolean isColumnPrimaryKey) {
        this.isColumnPrimaryKey = isColumnPrimaryKey;
    }

    public List<ColumnValue> getColumnValues() {
        return columnValues;
    }

    public void setColumnValues(List<ColumnValue> columnValues) {
        this.columnValues = columnValues;
    }

    @Override
    public String toString() {
        return "ColumnHeader{" +
                "columnCode='" + columnCode + '\'' +
                ", columnDisplayType='" + columnDisplayType + '\'' +
                ", columnLength=" + columnLength +
                ", columnName='" + columnName + '\'' +
                ", columnType='" + columnType + '\'' +
                ", isColumnNullable=" + isColumnNullable +
                ", isColumnPrimaryKey=" + isColumnPrimaryKey +
                ", columnValues=" + columnValues +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.columnCode);
        dest.writeString(this.columnDisplayType);
        dest.writeValue(this.columnLength);
        dest.writeString(this.columnName);
        dest.writeString(this.columnType);
        dest.writeValue(this.isColumnNullable);
        dest.writeValue(this.isColumnPrimaryKey);
        dest.writeTypedList(this.columnValues);
    }

    public ColumnHeader() {
    }

    protected ColumnHeader(Parcel in) {
        this.columnCode = in.readString();
        this.columnDisplayType = in.readString();
        this.columnLength = (Integer) in.readValue(Integer.class.getClassLoader());
        this.columnName = in.readString();
        this.columnType = in.readString();
        this.isColumnNullable = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.isColumnPrimaryKey = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.columnValues = in.createTypedArrayList(ColumnValue.CREATOR);
    }

    public static final Parcelable.Creator<ColumnHeader> CREATOR =
            new Parcelable.Creator<ColumnHeader>() {
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
