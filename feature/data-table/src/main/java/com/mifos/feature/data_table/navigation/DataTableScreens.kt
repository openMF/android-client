package com.mifos.feature.data_table.navigation

import com.mifos.core.common.utils.Constants

sealed class DataTableScreens(val route: String) {

    data object DataTableScreenRoute : DataTableScreens("data_table_screen_route")

    data object DataTableScreen :
        DataTableScreens("data_table_screen/{${Constants.DATA_TABLE_NAV_DATA}}") {
        fun argument(data: String) = "data_table_screen/${data}"
    }

    data object DataTableListScreen :
        DataTableScreens("data_table_list_screen/{${Constants.DATA_TABLE_LIST_NAV_DATA}}") {
        fun argument(data: String) = "data_table_list_screen/${data}"
    }

    data object DataTableDataScreen :
        DataTableScreens("data_table_data_screen/{${Constants.DATA_TABLE_DATA_NAV_DATA}}") {
        fun argument(data: String) = "data_table_data_screen/${data}"
    }
}