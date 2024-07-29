/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repositoryImp

import com.mifos.core.data.repository.CheckerInboxRepository
import com.mifos.core.network.GenericResponse
import com.mifos.core.network.datamanager.DataManagerCheckerInbox
import com.mifos.core.objects.checkerinboxandtasks.CheckerInboxSearchTemplate
import com.mifos.core.objects.checkerinboxandtasks.CheckerTask
import javax.inject.Inject

class CheckerInboxRepositoryImp @Inject constructor(
    private val dataManagerCheckerInbox: DataManagerCheckerInbox,
) : CheckerInboxRepository {

    override suspend fun loadCheckerTasks(
        actionName: String?,
        entityName: String?,
        resourceId: Int?,
    ): List<CheckerTask> {
        return dataManagerCheckerInbox.getCheckerTaskList()
    }

    override suspend fun approveCheckerEntry(auditId: Int): GenericResponse {
        return dataManagerCheckerInbox.approveCheckerEntry(auditId)
    }

    override suspend fun rejectCheckerEntry(auditId: Int): GenericResponse {
        return dataManagerCheckerInbox.rejectCheckerEntry(auditId)
    }

    override suspend fun deleteCheckerEntry(auditId: Int): GenericResponse {
        return dataManagerCheckerInbox.deleteCheckerEntry(auditId)
    }

    override suspend fun loadSearchTemplate(): CheckerInboxSearchTemplate {
        TODO("Not yet implemented")
    }
}
