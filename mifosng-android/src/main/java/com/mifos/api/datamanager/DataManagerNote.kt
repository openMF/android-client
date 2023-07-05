package com.mifos.api.datamanager

import com.mifos.api.BaseApiManager
import com.mifos.api.local.databasehelper.DatabaseHelperNote
import com.mifos.objects.noncore.Note
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * This DataManager is for Managing Notes API, In which Request is going to Server
 * and In Response, We are getting Notes API Observable Response using Retrofit2
 * Created by rahul on 4/3/17.
 */
@Singleton
class DataManagerNote @Inject constructor(
    val mBaseApiManager: BaseApiManager,
    val mDatabaseHelperNote: DatabaseHelperNote
) {
    /**
     * This Method Request the REST API of Note and In response give the List of Notes
     */
    fun getNotes(entityType: String?, entityId: Int): Observable<List<Note>> {
        return mBaseApiManager.noteApi.getNotes(entityType, entityId)
    }
}