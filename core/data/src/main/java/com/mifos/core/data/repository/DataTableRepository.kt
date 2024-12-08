package com.mifos.core.data.repository

import com.mifos.core.objects.noncore.DataTable

/**
 * Created by Aditya Gupta on 08/08/23.
 */
interface DataTableRepository {

    suspend fun getDataTable(tableName: String?): List<DataTable>

}