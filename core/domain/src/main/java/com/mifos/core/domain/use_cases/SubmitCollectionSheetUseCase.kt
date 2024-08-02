package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.GenerateCollectionSheetRepository
import com.mifos.core.network.GenericResponse
import com.mifos.core.objects.collectionsheet.CollectionSheetPayload
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SubmitCollectionSheetUseCase @Inject constructor(private val repository: GenerateCollectionSheetRepository) {

    suspend operator fun invoke(
        groupId: Int,
        payload: CollectionSheetPayload?
    ): Flow<Resource<GenericResponse>> = flow {
        try {
            emit(Resource.Loading())
            val response = repository.submitCollectionSheet(groupId, payload)
            emit(Resource.Success(response))

        } catch (exception: Exception) {
            emit(Resource.Error(exception.message.toString()))
        }
    }
}