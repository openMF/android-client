package com.mifos.core.data.repository

import com.mifos.core.objects.noncore.DataTable
import rx.Observable

/**
 * Created by Aditya Gupta on 08/08/23.
 */
interface DataTableRepository {

    fun getDataTable(tableName: String?): Observable<List<DataTable>>

}