package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.ClientIdentifiersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.openapitools.client.models.DeleteClientsClientIdIdentifiersIdentifierIdResponse
import javax.inject.Inject

class DeleteIdentifierUseCase @Inject constructor(private val repository: ClientIdentifiersRepository) {

    suspend operator fun invoke(
        clientId: Int,
        identifierId: Int
    ): Flow<Resource<DeleteClientsClientIdIdentifiersIdentifierIdResponse>> = flow {
        try {
            emit(Resource.Loading())
            val response =
                repository.deleteClientIdentifier(clientId = clientId, identifierId = identifierId)
            emit(Resource.Success(response))
        } catch (exception: Exception) {
            emit(Resource.Error(exception.message.toString()))
        }
    }
}