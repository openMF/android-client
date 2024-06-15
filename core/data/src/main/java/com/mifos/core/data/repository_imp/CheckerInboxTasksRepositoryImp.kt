package com.mifos.core.data.repository_imp

import com.mifos.core.data.repository.CheckerInboxTasksRepository
import com.mifos.core.network.datamanager.DataManagerCheckerInbox
import com.mifos.core.objects.checkerinboxandtasks.CheckerTask
import com.mifos.core.objects.checkerinboxandtasks.RescheduleLoansTask
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
        resourceId: Int?
    ): Flow<List<CheckerTask>> {
        return flow { emit(dataManagerCheckerInbox.getCheckerTaskList()) }
    }
}