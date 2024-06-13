package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.CenterListRepository
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

class GetCenterListDbUseCase @Inject constructor(private val repository: CenterListRepository) {

    suspend operator fun invoke(): Flow<Resource<List<Center>>> = flow {
        try {
            emit(Resource.Loading())
            emit(Resource.Success(getCenterList()))
        } catch (exception: Exception) {
            emit(Resource.Error(exception.toString()))
        }
    }

    private suspend fun getCenterList(): List<Center> = suspendCoroutine { continuation ->
        try {
            repository.allDatabaseCenters()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<Page<Center>>() {
                    override fun onCompleted() {}
                    override fun onError(exception: Throwable) {
                        continuation.resumeWithException(exception)
                    }

                    override fun onNext(centerPage: Page<Center>) {
                        continuation.resume(centerPage.pageItems)
                    }
                })
        } catch (exception: Exception) {
            continuation.resumeWithException(exception)
        }
    }
}