package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.ActivateRepository
import com.mifos.core.objects.client.ActivatePayload
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.openapitools.client.models.PostCentersCenterIdResponse
import javax.inject.Inject

class ActivateCenterUseCase @Inject constructor(private val activateRepository: ActivateRepository) {

    suspend operator fun invoke(
        centerId: Int,
        centerPayload: ActivatePayload
    ): Flow<Resource<PostCentersCenterIdResponse>> = flow {
        try {
            emit(Resource.Loading())
            val response = activateRepository.activateCenter(centerId, centerPayload)
            emit(Resource.Success(response))
        } catch (exception: Exception) {
            emit(Resource.Error(exception.message.toString()))
        }
    }
}