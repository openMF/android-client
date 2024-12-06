/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repository

import com.google.gson.JsonArray
import org.apache.fineract.client.models.DeleteDataTablesDatatableAppTableIdDatatableIdResponse
import rx.Observable

/**
 * Created by Aditya Gupta on 10/08/23.
 */
interface DataTableDataRepository {

    suspend fun getDataTableInfo(table: String, entityId: Int): JsonArray

    fun deleteDataTableEntry(
        table: String?,
        entity: Int,
        rowId: Int,
    ): Observable<DeleteDataTablesDatatableAppTableIdDatatableIdResponse>
}
