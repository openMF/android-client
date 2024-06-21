package com.mifos.core.data.repository

import com.mifos.core.objects.checkerinboxandtasks.CheckerTask
import com.mifos.core.objects.checkerinboxandtasks.RescheduleLoansTask
import kotlinx.coroutines.flow.Flow
import rx.Observable


/**
 * Created by Aditya Gupta on 21/03/24.
 */

interface CheckerInboxTasksRepository {

    suspend fun getRescheduleLoansTaskList(): Flow<List<RescheduleLoansTask>>

    suspend fun getCheckerTaskList(
        actionName: String? = null, entityName: String? = null,
        resourceId: Int? = null
    ): Flow<List<CheckerTask>>

}