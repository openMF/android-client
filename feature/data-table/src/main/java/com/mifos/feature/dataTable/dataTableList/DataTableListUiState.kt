/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.dataTable.dataTableList

import com.mifos.core.dbobjects.client.Client

/**
 * Created by Aditya Gupta on 10/08/23.
 */
sealed class DataTableListUiState {

    data object Loading : DataTableListUiState()

    data class ShowMessage(val messageResId: Int? = null, val message: String? = null) :
        DataTableListUiState()

    data class Success(val messageResId: Int? = null, val client: Client? = null) :
        DataTableListUiState()
}
