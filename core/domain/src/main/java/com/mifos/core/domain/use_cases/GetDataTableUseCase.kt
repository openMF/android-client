package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.DataTableRepository
import com.mifos.core.data.repository.GroupDetailsRepository
import com.mifos.core.objects.client.Client
import com.mifos.core.objects.group.GroupWithAssociations
import com.mifos.core.objects.noncore.DataTable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Pronay Sarker on 08/08/2024 (8:53 AM)
 */
class GetDataTableUseCase @Inject constructor(private val repository: DataTableRepository) {

    suspend operator fun invoke(tableName: String?): Flow<Resource<List<DataTable>>> = callbackFlow {
        try {
            trySend(Resource.Loading())

            repository.getDataTable(tableName)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<List<DataTable>>() {
                    override fun onCompleted() {}

                    override fun onError(exception: Throwable) {
                        trySend(Resource.Error(exception.message.toString()))
                    }

                    override fun onNext(dataTables: List<DataTable>) {
                        trySend(Resource.Success(dataTables))
                    }
                })

            awaitClose { channel.close() }
        } catch (exception: Exception) {
            trySend(Resource.Error(exception.message.toString()))
        }
    }
}