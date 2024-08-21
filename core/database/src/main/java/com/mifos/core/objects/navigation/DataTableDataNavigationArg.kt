package com.mifos.core.objects.navigation

import com.mifos.core.objects.noncore.DataTable

data class DataTableDataNavigationArg(

    val tableName: String,

    val entityId: Int,

    val dataTable: DataTable
)