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
import com.mifos.core.data.repository.CheckerInboxTasksRepository
import com.mifos.core.model.objects.checkerinboxtask.CheckerTask
import com.mifos.core.model.objects.checkerinboxtask.RescheduleLoansTask
import com.mifos.core.network.datamanager.DataManagerCheckerInbox
import kotlinx.coroutines.flow.Flow

/**
 * Created by Aditya Gupta on 21/03/24.
 */

class CheckerInboxTasksRepositoryImp(
    private val dataManagerCheckerInbox: DataManagerCheckerInbox,
) : CheckerInboxTasksRepository {

    override fun getRescheduleLoansTaskList(): Flow<DataState<List<RescheduleLoansTask>>> {
        return dataManagerCheckerInbox.getRescheduleLoansTaskList()
            .asDataStateFlow()
    }

    override fun getCheckerTaskList(
        actionName: String?,
        entityName: String?,
        resourceId: Int?,
    ): Flow<DataState<List<CheckerTask>>> {
        return dataManagerCheckerInbox.getCheckerTaskList().asDataStateFlow()
    }
}
