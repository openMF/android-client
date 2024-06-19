package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.PathTrackingRepository
import com.mifos.core.objects.user.UserLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetUserPathTrackingUseCase @Inject constructor(private val repository: PathTrackingRepository) {

    suspend operator fun invoke(userId: Int): Flow<Resource<List<UserLocation>>> = flow {
        try {
            emit(Resource.Loading())
            val response = repository.getUserPathTracking(userId)
            emit(Resource.Success(response))
        } catch (exception: Exception) {
            emit(Resource.Error(exception.message.toString()))
        }
    }
}