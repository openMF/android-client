package com.mifos.objects.noncore;

/**
 * Created by ishankhanna on 16/06/14.
 */
public class DataTable {

    String applicationTableName;
    ColumnHeaderData columnHeaderData;
    String registeredTableName;

    public String getApplicationTableName() {
        return applicationTableName;
    }

    public void setApplicationTableName(String applicationTableName) {
        this.applicationTableName = applicationTableName;
    }

    public ColumnHeaderData getColumnHeaderData() {
        return columnHeaderData;
    }

    public void setColumnHeaderData(ColumnHeaderData columnHeaderData) {
        this.columnHeaderData = columnHeaderData;
    }

    public String getRegisteredTableName() {
        return registeredTableName;
    }

    public void setRegisteredTableName(String registeredTableName) {
        this.registeredTableName = registeredTableName;
    }
}
