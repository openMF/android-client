/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.dataTable.navigation

import com.mifos.core.common.utils.Constants

sealed class DataTableScreens(val route: String) {

    data object DataTableScreenRoute : DataTableScreens("data_table_screen_route")

    data object DataTableScreen :
        DataTableScreens("data_table_screen/{${Constants.DATA_TABLE_NAV_DATA}}") {
        fun argument(data: String) = "data_table_screen/$data"
    }

    data object DataTableListScreen :
        DataTableScreens("data_table_list_screen/{${Constants.DATA_TABLE_LIST_NAV_DATA}}") {
        fun argument(data: String) = "data_table_list_screen/$data"
    }

    data object DataTableDataScreen :
        DataTableScreens("data_table_data_screen/{${Constants.DATA_TABLE_DATA_NAV_DATA}}") {
        fun argument(data: String) = "data_table_data_screen/$data"
    }
}
