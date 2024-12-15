package com.mifos.core.domain.useCases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.ClientIdentifiersRepository
import com.mifos.core.objects.noncore.Identifier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetClientIdentifiersUseCase @Inject constructor(private val clientIdentifiersRepository: ClientIdentifiersRepository) {

    suspend operator fun invoke(clientId: Int): Flow<Resource<List<Identifier>>> = flow {
        try {
            emit(Resource.Loading())
            val response = clientIdentifiersRepository.getClientIdentifiers(clientId)
            emit(Resource.Success(response))
        } catch (exception: Exception) {
            emit(Resource.Error(exception.message.toString()))
        }
    }
}