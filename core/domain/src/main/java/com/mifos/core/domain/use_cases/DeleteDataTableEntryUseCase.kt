package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.DataTableDataRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.apache.fineract.client.models.DeleteDataTablesDatatableAppTableIdDatatableIdResponse
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

class DeleteDataTableEntryUseCase @Inject constructor(private val repository: DataTableDataRepository) {

    suspend operator fun invoke(
        table: String?,
        entity: Int,
        rowId: Int
    ): Flow<Resource<DeleteDataTablesDatatableAppTableIdDatatableIdResponse>> = callbackFlow {
        try {
            trySend(Resource.Loading())
            repository.deleteDataTableEntry(table, entity, rowId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object :
                    Subscriber<DeleteDataTablesDatatableAppTableIdDatatableIdResponse>() {
                    override fun onCompleted() {}

                    override fun onError(exception: Throwable) {
                        trySend(Resource.Error(exception.message.toString()))
                    }

                    override fun onNext(response: DeleteDataTablesDatatableAppTableIdDatatableIdResponse) {
                        trySend(Resource.Success(response))
                    }
                })
            awaitClose { channel.close() }

        } catch (exception: Exception) {
            trySend(Resource.Error(exception.message.toString()))
        }
    }

}