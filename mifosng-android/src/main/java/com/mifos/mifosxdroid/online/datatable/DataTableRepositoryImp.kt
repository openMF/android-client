package com.mifos.mifosxdroid.online.datatable

import com.mifos.api.datamanager.DataManagerDataTable
import com.mifos.objects.noncore.DataTable
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 08/08/23.
 */
class DataTableRepositoryImp @Inject constructor(private val dataManagerDataTable: DataManagerDataTable) :
    DataTableRepository {

    override fun getDataTable(tableName: String?): Observable<List<DataTable>> {
        return dataManagerDataTable.getDataTable(tableName)
    }
}