package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.GenerateCollectionSheetRepository
import com.mifos.core.objects.group.CenterWithAssociations
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FetchGroupsAssociatedWithCenterUseCase @Inject constructor(
    private val repository: GenerateCollectionSheetRepository
) {

    suspend operator fun invoke(centerId: Int): Flow<Resource<CenterWithAssociations>> = flow {

        try {
            emit(Resource.Loading())
            val response = repository.fetchGroupsAssociatedWithCenter(centerId)
            emit(Resource.Success(response))

        } catch (exception: Exception) {
            emit(Resource.Error(exception.message.toString()))
        }
    }
}