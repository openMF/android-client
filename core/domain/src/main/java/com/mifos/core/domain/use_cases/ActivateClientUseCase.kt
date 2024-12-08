package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.ActivateRepository
import com.mifos.core.objects.client.ActivatePayload
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.openapitools.client.models.PostClientsClientIdResponse
import javax.inject.Inject

class ActivateClientUseCase @Inject constructor(private val activateRepository: ActivateRepository) {

    suspend operator fun invoke(
        clientId: Int,
        clientPayload: ActivatePayload
    ): Flow<Resource<PostClientsClientIdResponse>> = flow {
        try {
            emit(Resource.Loading())
            val response = activateRepository.activateClient(clientId, clientPayload)
            emit(Resource.Success(response))
        } catch (exception: Exception) {
            emit(Resource.Error(exception.message.toString()))
        }
    }
}