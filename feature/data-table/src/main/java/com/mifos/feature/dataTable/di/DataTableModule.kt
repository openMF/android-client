/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.dataTable.di

import com.mifos.feature.dataTable.dataTable.DataTableViewModel
import com.mifos.feature.dataTable.dataTableData.DataTableDataViewModel
import com.mifos.feature.dataTable.dataTableList.DataTableListViewModel
import com.mifos.feature.dataTable.dataTableRowDialog.DataTableRowDialogViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val DataTableModule = module {
    viewModelOf(::DataTableViewModel)
    viewModelOf(::DataTableDataViewModel)
    viewModelOf(::DataTableListViewModel)
    viewModelOf(::DataTableRowDialogViewModel)
}
