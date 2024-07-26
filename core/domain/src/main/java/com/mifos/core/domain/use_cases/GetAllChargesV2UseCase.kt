package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.ChargeDialogRepository
import com.mifos.core.objects.templates.clients.ChargeTemplate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetAllChargesV2UseCase @Inject constructor(private val repository: ChargeDialogRepository) {

    suspend operator fun invoke(clientId: Int): Flow<Resource<ChargeTemplate>> = flow {
        try {
            emit(Resource.Loading())
            val response = repository.getAllChargesV2(clientId)
            emit(Resource.Success(response))
        } catch (exception: Exception) {
            emit(Resource.Error(exception.message.toString()))
        }
    }
}