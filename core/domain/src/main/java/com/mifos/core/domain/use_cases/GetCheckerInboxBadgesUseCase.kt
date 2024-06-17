package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.CheckerInboxTasksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.zip
import javax.inject.Inject


/**
 * Created by Aditya Gupta on 21/03/24.
 */

class GetCheckerInboxBadgesUseCase @Inject constructor(
    private val repository: CheckerInboxTasksRepository
) {
    operator fun invoke(): Flow<Resource<Pair<Int, Int>>> = flow {
        try {
            emit(Resource.Loading())
            repository.getCheckerTaskList()
                .zip(repository.getRescheduleLoansTaskList()) { checkerTasks, rescheduleLoanTasks ->
                    Pair(checkerTasks.size, rescheduleLoanTasks.size)
                }.collect {
                    emit(Resource.Success(it))
                }
        } catch (e: Exception) {
            emit(Resource.Error(e.message.toString()))
        }
    }
}