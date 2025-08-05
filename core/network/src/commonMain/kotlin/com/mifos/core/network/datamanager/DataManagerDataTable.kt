/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.datamanager

import com.mifos.core.model.objects.users.UserLocation
import com.mifos.core.network.BaseApiManager
import com.mifos.core.network.GenericResponse
import com.mifos.core.network.mappers.dataTable.GetDataTablesResponseMapper
import com.mifos.core.network.model.DeleteDataTablesDatatableAppTableIdDatatableIdResponse
import com.mifos.room.entities.noncore.DataTableEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.JsonArray

/**
 * This DataManager is for Managing DataTable API, In which Request is going to Server
 * and In Response, We are getting DataTable API Observable Response using Retrofit2
 *
 * Created by Rajan Maurya on 3/7/16.
 */
class DataManagerDataTable(
    val mBaseApiManager: BaseApiManager,
) {
    /**
     * This Method Request the REST API of Datatable and In response give the List of DataTable
     * Type of DataTable is
     * 1. m_client
     * 2. m_savings_account
     * 3. m_loan
     * @param tableName DataTable Name
     * @return List<DataTable>
     </DataTable> */
    fun getDataTable(tableName: String?): Flow<List<DataTableEntity>> {
        return mBaseApiManager.dataTableApi.getDatatables(tableName)
            .map { responseList ->
                responseList.map(GetDataTablesResponseMapper::mapFromEntity)
            }
    }

    suspend fun getDataTableInfo(table: String, entityId: Int): JsonArray {
        return mBaseApiManager.dataTableService.getDataOfDataTable(table, entityId)
    }

    suspend fun addDataTableEntry(
        table: String,
        entityId: Int,
        payload: Map<String, String>,
    ): GenericResponse {
        return mBaseApiManager.dataTableService
            .createEntryInDataTable(table, entityId, payload)
    }

    suspend fun deleteDataTableEntry(
        table: String,
        entity: Int,
        rowId: Int,
    ): DeleteDataTablesDatatableAppTableIdDatatableIdResponse {
        return mBaseApiManager.dataTableApi.deleteDatatableEntry(
            datatable = table,
            apptableId = entity.toLong(),
            datatableId = rowId.toLong(),
        )
    }

    /**
     * This Method is adding the User Tracking Data in the data Table "m_staff_path_tracking"
     *
     * @param userId UserId Id
     * @param userLocation  UserLocation
     * @return GenericResponse
     */
    fun addUserPathTracking(
        userId: Int,
        userLocation: UserLocation?,
    ): Flow<GenericResponse> {
        return mBaseApiManager.dataTableService.addUserPathTracking(userId, userLocation)
    }

    /**
     * This Method is fetching the User Path Tracking from the DataTable "m_staff_path_tracking"
     *
     * @param userId UserId Id
     * @return List<UserLocation>
     </UserLocation> */
    fun getUserPathTracking(userId: Int): Flow<List<UserLocation>> {
        return mBaseApiManager.dataTableService.getUserPathTracking(userId)
    }
}
