package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.PinPointClientRepository
import com.mifos.core.network.GenericResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteClientAddressPinpointUseCase @Inject constructor(private val pinPointClientRepository: PinPointClientRepository) {

    suspend operator fun invoke(clientId: Int, addressId: Int): Flow<Resource<GenericResponse>> =
        flow {
            try {
                emit(Resource.Loading())
                val response = pinPointClientRepository.deleteClientAddressPinpointLocation(
                    clientId,
                    addressId
                )
                emit(Resource.Success(response))

            } catch (exception: Exception) {
                emit(Resource.Error(exception.message.toString()))
            }
        }
}