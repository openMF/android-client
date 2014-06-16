package com.mifos.objects.noncore;

import java.util.List;

/**
 * Created by ishankhanna on 16/06/14.
 */
public class DataTable {

    String applicationTableName;
    List<ColumnHeader> columnHeaderData;
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

//    menu.clear();
//    menu.add(MENU_ITEM_SAVE_LOCATION,100,Menu.NONE, "Save Location");
//    menu.addSubMenu(MENU_LEVEL_2,1,Menu.NONE,"DataTables");
//
//    SubMenu dataTableSubMenu = menu.getItem(1).getSubMenu();
//    dataTableSubMenu.add(MENU_LEVEL_2,100,Menu.NONE,"Table1");
//    dataTableSubMenu.add(MENU_LEVEL_2,100,Menu.NONE,"Table2");
//    dataTableSubMenu.add(MENU_LEVEL_2,100,Menu.NONE,"Table3");
}
