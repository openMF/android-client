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

import com.mifos.core.data.repository.CheckerInboxTasksRepository
import com.mifos.core.network.datamanager.DataManagerCheckerInbox
import com.mifos.core.modelobjects.checkerinboxtask.CheckerTask
import com.mifos.core.modelobjects.checkerinboxtask.RescheduleLoansTask
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 21/03/24.
 */

class CheckerInboxTasksRepositoryImp @Inject constructor(private val dataManagerCheckerInbox: DataManagerCheckerInbox) :
    CheckerInboxTasksRepository {

    override suspend fun getRescheduleLoansTaskList(): Flow<List<RescheduleLoansTask>> {
        return flow { emit(dataManagerCheckerInbox.getRechdeduleLoansTaskList()) }
    }

    override suspend fun getCheckerTaskList(
        actionName: String?,
        entityName: String?,
        resourceId: Int?,
    ): Flow<List<CheckerTask>> {
        return flow { emit(dataManagerCheckerInbox.getCheckerTaskList()) }
    }
}
