package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.GenerateCollectionSheetRepository
import com.mifos.core.network.GenericResponse
import com.mifos.core.objects.collectionsheet.ProductiveCollectionSheetPayload
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SubmitProductiveSheetUseCase @Inject constructor(private val repository: GenerateCollectionSheetRepository) {

    suspend operator fun invoke(
        centerId: Int,
        payload: ProductiveCollectionSheetPayload?
    ): Flow<Resource<GenericResponse>> = flow {
        try {
            emit(Resource.Loading())
            val response = repository.submitProductiveSheet(centerId, payload)
            emit(Resource.Success(response))

        } catch (exception: Exception) {
            emit(Resource.Error(exception.message.toString()))
        }
    }
}