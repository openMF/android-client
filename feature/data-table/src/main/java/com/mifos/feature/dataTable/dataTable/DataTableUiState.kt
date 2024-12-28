/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.dataTable.dataTable

import com.mifos.core.dbobjects.noncore.DataTable

/**
 * Created by Aditya Gupta on 08/08/23.
 */
sealed class DataTableUiState {

    data object ShowProgressbar : DataTableUiState()

    data class ShowError(val message: Int) : DataTableUiState()

    data class ShowDataTables(val dataTables: List<DataTable>) : DataTableUiState()

    data object ShowEmptyDataTables : DataTableUiState()
}
