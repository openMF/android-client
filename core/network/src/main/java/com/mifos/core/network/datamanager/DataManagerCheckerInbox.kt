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

import com.mifos.core.`object`.checkerinboxtask.CheckerInboxSearchTemplate
import com.mifos.core.`object`.checkerinboxtask.CheckerTask
import com.mifos.core.`object`.checkerinboxtask.RescheduleLoansTask
import com.mifos.core.network.BaseApiManager
import com.mifos.core.network.GenericResponse
import rx.Observable
import javax.inject.Inject

class DataManagerCheckerInbox @Inject constructor(
    private val mBaseApiManager: BaseApiManager,
) {

    suspend fun getCheckerTaskList(
        actionName: String? = null,
        entityName: String? = null,
        resourceId: Int? = null,
    ): List<CheckerTask> {
        return mBaseApiManager.checkerInboxApi.getCheckerList(
            actionName,
            entityName,
            resourceId,
        )
    }

    suspend fun approveCheckerEntry(auditId: Int): GenericResponse {
        return mBaseApiManager.checkerInboxApi.approveCheckerEntry(auditId)
    }

    suspend fun rejectCheckerEntry(auditId: Int): GenericResponse {
        return mBaseApiManager.checkerInboxApi.rejectCheckerEntry(auditId)
    }

    suspend fun deleteCheckerEntry(auditId: Int): GenericResponse {
        return mBaseApiManager.checkerInboxApi.deleteCheckerEntry(auditId)
    }

    suspend fun getRechdeduleLoansTaskList(): List<RescheduleLoansTask> {
        return mBaseApiManager.checkerInboxApi.getRescheduleLoansTaskList()
    }

    fun getCheckerInboxSearchTemplate(): Observable<CheckerInboxSearchTemplate> {
        return mBaseApiManager.checkerInboxApi.getCheckerInboxSearchTempalate()
    }

    fun getCheckerTaskFromResourceId(
        actionName: String? = null,
        entityName: String? = null,
        resourceId: Int? = null,
    ): Observable<List<CheckerTask>> {
        return mBaseApiManager.checkerInboxApi.getCheckerTasksFromResourceId(
            actionName,
            entityName,
            resourceId,
        )
    }
}
