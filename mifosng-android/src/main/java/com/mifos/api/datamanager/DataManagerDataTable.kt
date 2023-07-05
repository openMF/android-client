package com.mifos.api.datamanager

import com.google.gson.JsonArray
import com.mifos.api.BaseApiManager
import com.mifos.api.GenericResponse
import com.mifos.api.local.databasehelper.DatabaseHelperDataTable
import com.mifos.objects.noncore.DataTable
import com.mifos.objects.user.UserLocation
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * This DataManager is for Managing DataTable API, In which Request is going to Server
 * and In Response, We are getting DataTable API Observable Response using Retrofit2
 *
 * Created by Rajan Maurya on 3/7/16.
 */
@Singleton
class DataManagerDataTable @Inject constructor(
    val mBaseApiManager: BaseApiManager,
    val mDatabaseHelperDataTable: DatabaseHelperDataTable
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
    fun getDataTable(tableName: String?): Observable<List<DataTable>> {
        return mBaseApiManager.dataTableApi.getTableOf(tableName)
    }

    fun getDataTableInfo(table: String?, entityId: Int): Observable<JsonArray> {
        return mBaseApiManager.dataTableApi.getDataOfDataTable(table, entityId)
    }

    fun addDataTableEntry(
        table: String?, entityId: Int, payload: Map<String, String>
    ): Observable<GenericResponse> {
        return mBaseApiManager.dataTableApi
            .createEntryInDataTable(table, entityId, payload)
    }

    fun deleteDataTableEntry(table: String?, entity: Int, rowId: Int): Observable<GenericResponse> {
        return mBaseApiManager.dataTableApi.deleteEntryOfDataTableManyToMany(
            table, entity, rowId
        )
    }

    /**
     * This Method is adding the User Tracking Data in the data Table "user_tracking"
     *
     * @param userId UserId Id
     * @param userLocation  UserLocation
     * @return GenericResponse
     */
    fun addUserPathTracking(
        userId: Int,
        userLocation: UserLocation?
    ): Observable<GenericResponse> {
        return mBaseApiManager.dataTableApi.addUserPathTracking(userId, userLocation)
    }

    /**
     * This Method is fetching the User Path Tracking from the DataTable "user_tracking"
     *
     * @param userId UserId Id
     * @return List<UserLocation>
    </UserLocation> */
    fun getUserPathTracking(userId: Int): Observable<List<UserLocation>> {
        return mBaseApiManager.dataTableApi.getUserPathTracking(userId)
    }
}