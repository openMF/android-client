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

import com.mifos.core.data.repository.DataTableRowDialogRepository
import com.mifos.core.network.GenericResponse
import com.mifos.core.network.datamanager.DataManagerDataTable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 13/08/23.
 */
class DataTableRowDialogRepositoryImp @Inject constructor(private val dataManagerDataTable: DataManagerDataTable) :
    DataTableRowDialogRepository {

    override suspend fun addDataTableEntry(
        table: String,
        entityId: Int,
        payload: Map<String, String>,
    ): GenericResponse {
        return dataManagerDataTable.addDataTableEntry(table, entityId, payload)
    }
}
