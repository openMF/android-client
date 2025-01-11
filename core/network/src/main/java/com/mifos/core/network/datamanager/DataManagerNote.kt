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

import com.mifos.core.databasehelper.DatabaseHelperNote
import com.mifos.core.entity.noncore.Note
import com.mifos.core.network.BaseApiManager
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
    val mDatabaseHelperNote: DatabaseHelperNote,
) {
    /**
     * This Method Request the REST API of Note and In response give the List of Notes
     */
    fun getNotes(entityType: String?, entityId: Int): List<Note> {
        return mBaseApiManager.noteApi.getNotes(entityType, entityId)
    }
}
