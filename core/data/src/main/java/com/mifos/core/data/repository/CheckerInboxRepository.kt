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

import com.mifos.core.network.GenericResponse
import com.mifos.core.objects.checkerinboxandtasks.CheckerInboxSearchTemplate
import com.mifos.core.objects.checkerinboxandtasks.CheckerTask

interface CheckerInboxRepository {

    suspend fun loadCheckerTasks(
        actionName: String? = null,
        entityName: String? = null,
        resourceId: Int? = null,
    ): List<CheckerTask>

    suspend fun approveCheckerEntry(auditId: Int): GenericResponse

    suspend fun rejectCheckerEntry(auditId: Int): GenericResponse

    suspend fun deleteCheckerEntry(auditId: Int): GenericResponse

    suspend fun loadSearchTemplate(): CheckerInboxSearchTemplate
}
