package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.IndividualCollectionSheetDetailsRepository
import com.mifos.core.network.GenericResponse
import com.mifos.core.network.model.IndividualCollectionSheetPayload
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SaveIndividualCollectionSheetUseCase @Inject constructor(private val repository: IndividualCollectionSheetDetailsRepository) {

    suspend operator fun invoke(payload: IndividualCollectionSheetPayload): Flow<Resource<GenericResponse>> =
        flow {
            try {
                emit(Resource.Loading())
                val response = repository.saveIndividualCollectionSheet(payload)
                emit(Resource.Success(response))
            } catch (exception: Exception) {
                emit(Resource.Error(exception.message.toString()))
            }
        }

}