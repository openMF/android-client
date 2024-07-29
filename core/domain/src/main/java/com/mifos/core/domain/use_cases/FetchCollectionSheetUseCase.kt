package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.GenerateCollectionSheetRepository
import com.mifos.core.objects.collectionsheet.CollectionSheetRequestPayload
import com.mifos.core.objects.collectionsheet.CollectionSheetResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FetchCollectionSheetUseCase @Inject constructor(private val repository: GenerateCollectionSheetRepository) {

    suspend operator fun invoke(
        groupId: Int,
        payload: CollectionSheetRequestPayload?
    ): Flow<Resource<CollectionSheetResponse>> = flow {
        try {
            emit(Resource.Loading())
            val response = repository.fetchCollectionSheet(groupId, payload)
            emit(Resource.Success(response))

        } catch (exception: Exception) {
            emit(Resource.Error(exception.message.toString()))
        }
    }

}