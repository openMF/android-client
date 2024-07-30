package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.GenerateCollectionSheetRepository
import com.mifos.core.objects.group.Group
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetGroupsByOfficeUseCase @Inject constructor(
    private val repository: GenerateCollectionSheetRepository
) {

    suspend operator fun invoke(
        officeId: Int,
        params: Map<String, String>
    ): Flow<Resource<List<Group>>> = flow {
        try {
            emit(Resource.Loading())
            val response = repository.getGroupsByOffice(officeId, params)
            emit(Resource.Success(response))
        } catch (exception: Exception) {
            emit(Resource.Error(exception.message.toString()))
        }
    }
}