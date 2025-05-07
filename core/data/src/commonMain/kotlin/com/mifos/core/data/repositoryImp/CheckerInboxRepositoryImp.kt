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

import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.asDataStateFlow
import com.mifos.core.data.repository.CheckerInboxRepository
import com.mifos.core.model.objects.checkerinboxtask.CheckerInboxSearchTemplate
import com.mifos.core.model.objects.checkerinboxtask.CheckerTask
import com.mifos.core.network.GenericResponse
import com.mifos.core.network.datamanager.DataManagerCheckerInbox
import kotlinx.coroutines.flow.Flow

class CheckerInboxRepositoryImp(
    private val dataManagerCheckerInbox: DataManagerCheckerInbox,
) : CheckerInboxRepository {

    override fun loadCheckerTasks(
        actionName: String?,
        entityName: String?,
        resourceId: Int?,
    ): Flow<DataState<List<CheckerTask>>> {
        return dataManagerCheckerInbox.getCheckerTaskList().asDataStateFlow()
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
