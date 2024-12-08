/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repositoryImp

import com.mifos.core.data.repository.DataTableRepository
import com.mifos.core.network.datamanager.DataManagerDataTable
import com.mifos.core.objects.noncore.DataTable
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
