package com.mifos.core.data.repository

import com.mifos.core.network.GenericResponse
import com.mifos.core.objects.checkerinboxandtasks.CheckerInboxSearchTemplate
import com.mifos.core.objects.checkerinboxandtasks.CheckerTask
import kotlinx.coroutines.flow.Flow

interface CheckerInboxRepository {

    suspend fun loadCheckerTasks(
        actionName: String? = null, entityName: String? = null,
        resourceId: Int? = null
    ): List<CheckerTask>

    suspend fun approveCheckerEntry(auditId: Int): GenericResponse

    suspend fun rejectCheckerEntry(auditId: Int): GenericResponse

    suspend fun deleteCheckerEntry(auditId: Int): GenericResponse

    suspend fun loadSearchTemplate(): CheckerInboxSearchTemplate

}