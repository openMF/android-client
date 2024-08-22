package com.mifos.feature.data_table.dataTableList

import com.mifos.core.objects.noncore.DataTable

data class DataTableListNavArgs(

    val dataTableList: List<DataTable>,

    val requestType: Int,

    val payload: Any?,

    val formWidget: MutableList<List<FormWidget>>
)
