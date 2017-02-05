/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ishankhanna on 16/06/14.
 */
@Table(database = MifosDatabase.class)
@ModelContainer
public class ColumnHeader extends MifosBaseModel implements Parcelable {

    @PrimaryKey(autoincrement = true)
    Integer id;

    /**
     * columnCode will only be returned if columnDisplayType = "CODELOOKUP"
     * and null otherwise
     */
    String columnCode;

    @SerializedName("columnDisplayType")
    @Column
    String columnDisplayType;

    @SerializedName("columnLength")
    @Column
    Integer columnLength;

    @SerializedName("columnName")
    @Column
    String dataTableColumnName;

    @SerializedName("columnType")
    @Column
    String columnType;

    @SerializedName("isColumnNullable")
    @Column
    Boolean isColumnNullable;

    @SerializedName("isColumnPrimaryKey")
    @Column
    Boolean isColumnPrimaryKey;

    @Column
    String registeredTableName;

    /**
     * columnValues are actually Code Values that are either created by
     * system or defined manually by users
     */

    List<ColumnValue> columnValues = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRegisteredTableName() {
        return registeredTableName;
    }

    public void setRegisteredTableName(String registeredTableName) {
        this.registeredTableName = registeredTableName;
    }

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
        return dataTableColumnName;
    }

    public void setColumnName(String columnName) {
        this.dataTableColumnName = columnName;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public Boolean getColumnNullable() {
        return isColumnNullable;
    }

    public void setColumnNullable(Boolean columnNullable) {
        isColumnNullable = columnNullable;
    }

    public Boolean getColumnPrimaryKey() {
        return isColumnPrimaryKey;
    }

    public void setColumnPrimaryKey(Boolean columnPrimaryKey) {
        isColumnPrimaryKey = columnPrimaryKey;
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
                ", dataTableColumnName='" + dataTableColumnName + '\'' +
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
        dest.writeString(this.dataTableColumnName);
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
        this.dataTableColumnName = in.readString();
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
