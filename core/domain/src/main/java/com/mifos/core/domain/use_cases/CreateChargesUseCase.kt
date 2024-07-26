package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.ChargesPayload
import com.mifos.core.data.repository.ChargeDialogRepository
import com.mifos.core.objects.client.ChargeCreationResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CreateChargesUseCase @Inject constructor(private val repository: ChargeDialogRepository) {

    suspend operator fun invoke(
        clientId: Int,
        payload: ChargesPayload
    ): Flow<Resource<ChargeCreationResponse>> = flow {
        try {
            emit(Resource.Loading())
            val response = repository.createCharges(clientId, payload)
            emit(Resource.Success(response))
        } catch (exception: Exception) {
            emit(Resource.Error(exception.message.toString()))
        }
    }
}