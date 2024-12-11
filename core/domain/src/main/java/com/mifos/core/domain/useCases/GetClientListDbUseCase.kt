package com.mifos.core.domain.useCases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.ClientListRepository
import com.mifos.core.objects.client.Client
import com.mifos.core.objects.client.Page
import com.mifos.core.objects.group.Center
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class GetClientListDbUseCase @Inject constructor(private val repository: ClientListRepository) {
    suspend operator fun invoke(): Flow<Resource<List<Client>>> = flow {
        try {
            emit(Resource.Loading())
            emit(Resource.Success(getClientList()))
        } catch (exception: Exception) {
            emit(Resource.Error(exception.toString()))


        }
    }
    private suspend fun getClientList(): List<Client> = suspendCoroutine { continuation ->
        try {
            val getClientListFormDb =  repository.allDatabaseClients()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<Page<Client>>() {
                    override fun onCompleted() {
                    }
                    override fun onError(exception: Throwable) {
                        continuation.resumeWithException(exception)
                    }
                    override fun onNext(centerPage: Page<Client>) {
                        continuation.resume(centerPage.pageItems)
                    }
                })
        } catch (exception: Exception) {
            continuation.resumeWithException(exception)
        }
    }
}