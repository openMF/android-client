package com.mifos.core.data.repository_imp

import com.mifos.core.data.repository.DataTableRepository
import com.mifos.core.network.datamanager.DataManagerDataTable
import com.mifos.core.objects.noncore.DataTable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 08/08/23.
 */
class DataTableRepositoryImp @Inject constructor(private val dataManagerDataTable: DataManagerDataTable) :
    DataTableRepository {

    override suspend fun getDataTable(tableName: String?): List<DataTable> {
        return dataManagerDataTable.getDataTable(tableName)
    }
}