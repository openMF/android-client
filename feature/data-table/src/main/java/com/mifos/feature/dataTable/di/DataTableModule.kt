package com.mifos.feature.dataTable.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import com.mifos.feature.dataTable.dataTable.DataTableViewModel
import com.mifos.feature.dataTable.dataTableData.DataTableDataViewModel
import com.mifos.feature.dataTable.dataTableList.DataTableListViewModel
import com.mifos.feature.dataTable.dataTableRowDialog.DataTableRowDialogViewModel

val DataTableModule = module {
    viewModelOf(::DataTableViewModel)
    viewModelOf(::DataTableDataViewModel)
    viewModelOf(::DataTableListViewModel)
    viewModelOf(::DataTableRowDialogViewModel)
}