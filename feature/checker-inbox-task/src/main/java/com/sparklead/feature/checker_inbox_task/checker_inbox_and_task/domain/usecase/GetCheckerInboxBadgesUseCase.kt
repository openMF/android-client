package com.sparklead.feature.checker_inbox_task.checker_inbox_and_task.domain.usecase

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.CheckerInboxTasksRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 21/03/24.
 */

class GetCheckerInboxBadgesUseCase @Inject constructor(private val repository: CheckerInboxTasksRepository) {

    operator fun invoke(): Flow<Resource<Pair<Int, Int>>> = callbackFlow {
        try {
            trySend(Resource.Loading())
            Observable.zip(
                repository.getCheckerTaskList(),
                repository.getRescheduleLoansTaskList()
            ) { checkerTasks, rescheduleLoanTasks ->
                Pair(checkerTasks.size, rescheduleLoanTasks.size)
            }.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<Pair<Int, Int>>() {
                    override fun onCompleted() {

                    }

                    override fun onError(error: Throwable) {
                        trySend(Resource.Error(error.message.toString()))
                    }

                    override fun onNext(badges: Pair<Int, Int>) {
                        trySend(Resource.Success(badges))
                    }
                })

            awaitClose { channel.close() }
        } catch (e: Exception) {
            trySend(Resource.Error(e.message.toString()))
        }
    }
}