package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.ClientIdentifierDialogRepository
import com.mifos.core.objects.noncore.IdentifierCreationResponse
import com.mifos.core.objects.noncore.IdentifierPayload
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CreateClientIdentifierUseCase @Inject constructor(private val repository: ClientIdentifierDialogRepository) {

    suspend operator fun invoke(
        clientId: Int,
        identifierPayload: IdentifierPayload
    ): Flow<Resource<IdentifierCreationResponse>> = flow {
        try {
            emit(Resource.Loading())
            val response = repository.createClientIdentifier(clientId, identifierPayload)
            emit(Resource.Success(response))
        } catch (exception: Exception) {
            emit(Resource.Error(exception.message.toString()))
        }
    }
}