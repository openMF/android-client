/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repository

import com.mifos.core.objects.checkerinboxtask.CheckerTask
import kotlinx.coroutines.flow.Flow

/**
 * Created by Aditya Gupta on 21/03/24.
 */

interface CheckerInboxTasksRepository {

    suspend fun getRescheduleLoansTaskList(): Flow<List<com.mifos.core.model.objects.checkerinboxtask.RescheduleLoansTask>>

    suspend fun getCheckerTaskList(
        actionName: String? = null,
        entityName: String? = null,
        resourceId: Int? = null,
    ): Flow<List<CheckerTask>>
}
