package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.GenerateCollectionSheetRepository
import com.mifos.core.objects.group.Center
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetCentersInOfficeUseCase @Inject constructor(
    private val repository: GenerateCollectionSheetRepository
) {

    suspend operator fun invoke(
        id: Int,
        params: Map<String, String>
    ): Flow<Resource<List<Center>>> = flow {
        try {
            emit(Resource.Loading())
            val centers = repository.getCentersInOffice(id, params)
            emit(Resource.Success(centers))

        } catch (exception: Exception) {
            emit(Resource.Error(exception.message.toString()))
        }
    }
}