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

import com.mifos.core.model.objects.checkerinboxtask.CheckerInboxSearchTemplate
import com.mifos.core.model.objects.checkerinboxtask.CheckerTask
import com.mifos.core.model.objects.checkerinboxtask.RescheduleLoansTask
import com.mifos.core.network.BaseApiManager
import com.mifos.core.network.GenericResponse
import kotlinx.coroutines.flow.Flow

class DataManagerCheckerInbox(
    private val mBaseApiManager: BaseApiManager,
) {

    fun getCheckerTaskList(
        actionName: String? = null,
        entityName: String? = null,
        resourceId: Int? = null,
    ): Flow<List<CheckerTask>> {
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

    fun getRescheduleLoansTaskList(): Flow<List<RescheduleLoansTask>> {
        return mBaseApiManager.checkerInboxApi.getRescheduleLoansTaskList()
    }

    fun getCheckerInboxSearchTemplate(): Flow<CheckerInboxSearchTemplate> {
        return mBaseApiManager.checkerInboxApi.getCheckerInboxSearchTemplate()
    }

    fun getCheckerTaskFromResourceId(
        actionName: String? = null,
        entityName: String? = null,
        resourceId: Int? = null,
    ): Flow<List<CheckerTask>> {
        return mBaseApiManager.checkerInboxApi.getCheckerTasksFromResourceId(
            actionName,
            entityName,
            resourceId,
        )
    }
}
