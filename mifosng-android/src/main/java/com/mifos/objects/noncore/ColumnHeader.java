/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.noncore;

import java.util.List;

/**
 * Created by ishankhanna on 16/06/14.
 */
public class ColumnHeader {

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
}
