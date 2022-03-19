package com.mifos.api.datamanager

import javax.inject.Singleton
import javax.inject.Inject
import com.mifos.api.local.databasehelper.DatabaseHelperDataTable
import org.apache.fineract.client.services.DataTablesApi
import com.mifos.objects.noncore.DataTable
import org.apache.fineract.client.models.GetDataTablesResponse
import com.google.gson.JsonArray
import com.mifos.api.GenericResponse
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.mifos.api.BaseApiManager
import com.mifos.api.mappers.GenericMapper
import com.mifos.api.mappers.datatable.GetDataTablesResponseMapper
import org.apache.fineract.client.models.PostDataTablesAppTableIdResponse
import com.mifos.objects.user.UserLocation
import rx.Observable
import java.util.HashMap

/**
 * This DataManager is for Managing DataTable API, In which Request is going to Server
 * and In Response, We are getting DataTable API Observable Response using Retrofit2
 *
 * Created by Rajan Maurya on 3/7/16.
 */
@Singleton
class DataManagerDataTable @Inject constructor(
    val mBaseApiManager: BaseApiManager,
    val mDatabaseHelperDataTable: DatabaseHelperDataTable,
    val sdkBaseApiManager: org.mifos.core.apimanager.BaseApiManager
) {
    private val USER_LOCATION_TABLE = "user_tracking"
    private val datatableApi: DataTablesApi
        private get() = sdkBaseApiManager.getDataTableApi()

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
        return datatableApi.getDatatables(tableName)
            .map { obj: List<GetDataTablesResponse> ->
                GetDataTablesResponseMapper.mapFromEntityList(obj) }
        //return mBaseApiManager.getDataTableApi().getTableOf(tableName);
    }

    fun getDataTableInfo(table: String?, entityId: Int): Observable<JsonArray> {
        return datatableApi.getDatatable1(table, entityId.toLong(), null).map {
            val elm = JsonParser().parse(it)
            elm.asJsonArray
        }
        //return mBaseApiManager.dataTableApi.getDataOfDataTable(table, entityId)
    }

    fun addDataTableEntry(
        table: String?, entityId: Int, payload: Map<String?, Any?>?
    ): Observable<GenericResponse> {
        val req = Gson().toJson(payload)
        datatableApi.createDatatableEntry(table, entityId.toLong(), req)
            .map {
                val response = GenericResponse()
                val map = HashMap<String, Any?>()
                map["resourceId"] = it.resourceId
                response.responseFields = map
                response
            }
        return mBaseApiManager.dataTableApi
            .createEntryInDataTable(table, entityId, payload)
    }

    fun deleteDataTableEntry(table: String?, entity: Int, rowId: Int): Observable<GenericResponse> {
        datatableApi.deleteDatatableEntries1(table, entity.toLong(), rowId.toLong())
            .map {
                val response = GenericResponse()
                val map = HashMap<String, Any?>()
                map["resourceId"] = it.resourceId
                response.responseFields = map
                response
            }
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
        val req = Gson().toJson(userLocation)
        return datatableApi.createDatatableEntry(USER_LOCATION_TABLE, userId.toLong(), req)
            .map { it: PostDataTablesAppTableIdResponse ->
                val response = GenericResponse()
                val map = HashMap<String, Any?>()
                map["resourceId"] = it.resourceId
                response.responseFields = map
                response
            }
        //return mBaseApiManager.getDataTableApi().addUserPathTracking(userId, userLocation);
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