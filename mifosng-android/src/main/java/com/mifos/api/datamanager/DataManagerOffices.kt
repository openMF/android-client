package com.mifos.api.datamanager

import com.mifos.api.BaseApiManager
import com.mifos.api.local.databasehelper.DatabaseHelperOffices
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
    val mDatabaseHelperOffices: DatabaseHelperOffices
) {
    /**
     * return all List of Offices from DatabaseHelperOffices
     */
    val offices: Observable<List<Office>>
        get() = when (userStatus) {
            0 -> mBaseApiManager.officeApi.allOffices
                .concatMap { offices ->
                    mDatabaseHelperOffices.saveAllOffices(offices)
                    Observable.just(offices)
                }

            1 ->
                /**
                 * return all List of Offices from DatabaseHelperOffices
                 */
                /**
                 * return all List of Offices from DatabaseHelperOffices
                 */
                mDatabaseHelperOffices.readAllOffices()

            else -> {
                val offices: List<Office> = ArrayList()
                Observable.just(offices)
            }
        }
}