package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.GenerateCollectionSheetRepository
import com.mifos.core.objects.collectionsheet.CenterDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FetchCenterDetailsUseCase @Inject constructor(
    private val repository: GenerateCollectionSheetRepository
) {

    suspend operator fun invoke(
        format: String?,
        locale: String?,
        meetingDate: String?,
        officeId: Int,
        staffId: Int
    ): Flow<Resource<List<CenterDetail>>> = flow {
        try {
            emit(Resource.Loading())
            val response =
                repository.fetchCenterDetails(format, locale, meetingDate, officeId, staffId)
            emit(Resource.Success(response))

        } catch (exception: Exception) {
            emit(Resource.Error(exception.message.toString()))
        }
    }

}