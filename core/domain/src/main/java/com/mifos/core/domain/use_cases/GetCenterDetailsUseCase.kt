package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.CenterDetailsRepository
import com.mifos.core.objects.group.CenterInfo
import com.mifos.core.objects.group.CenterWithAssociations
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.zip
import javax.inject.Inject

class GetCenterDetailsUseCase @Inject constructor(private val repository: CenterDetailsRepository) {

    suspend operator fun invoke(
        centerId: Int,
        genericResultSet: Boolean
    ): Flow<Resource<Pair<CenterWithAssociations, List<CenterInfo>>>> = flow {
        try {
            emit(Resource.Loading())
            repository.getCentersGroupAndMeeting(centerId)
                .zip(
                    repository.getCenterSummaryInfo(
                        centerId,
                        genericResultSet
                    )
                ) { centerGroup, centerInfo ->
                    Pair(centerGroup, centerInfo)
                }.collect {
                    emit(Resource.Success(it))
                }
        } catch (exception: Exception) {
            emit(Resource.Error(exception.message.toString()))
        }
    }
}