package com.mifos.api.datamanager

import com.mifos.api.BaseApiManager
import com.mifos.api.local.databasehelper.DatabaseHelperOffices
import com.mifos.mappers.offices.GetOfficeResponseMapper
import com.mifos.objects.organisation.Office
import com.mifos.utils.PrefManager.userStatus
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * This DataManager is for Managing Offices API, In which Request is going to Server
 * and In Response, We are getting Offices API Observable Response using Retrofit2.
 * DataManagerOffices saving response in Database and response to Presenter as accordingly.
 *
 * Created by Rajan Maurya on 7/7/16.
 */
@Singleton
class DataManagerOffices @Inject constructor(
    val mBaseApiManager: BaseApiManager,
    private val mDatabaseHelperOffices: DatabaseHelperOffices,
    private val baseApiManager: org.mifos.core.apimanager.BaseApiManager
) {
    /**
     * return all List of Offices from DatabaseHelperOffices
     */
    val offices: Observable<List<Office>>
        get() = when (userStatus) {
            false -> baseApiManager.getOfficeApi().retrieveOffices(null, null, null)
                .map(GetOfficeResponseMapper::mapFromEntityList)

            true ->
                /**
                 * return all List of Offices from DatabaseHelperOffices
                 */
                /**
                 * return all List of Offices from DatabaseHelperOffices
                 */
                mDatabaseHelperOffices.readAllOffices()
        }
}