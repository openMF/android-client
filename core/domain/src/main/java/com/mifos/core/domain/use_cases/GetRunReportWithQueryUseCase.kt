package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.ReportDetailRepository
import com.mifos.core.objects.runreports.FullParameterListResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetRunReportWithQueryUseCase @Inject constructor(private val repository: ReportDetailRepository) {

    suspend operator fun invoke(
        reportName: String,
        options: Map<String, String>
    ): Flow<Resource<FullParameterListResponse>> = flow {
        try {
            emit(Resource.Loading())
            val response = repository.getRunReportWithQuery(reportName, options)
            emit(Resource.Success(response))
        } catch (exception: Exception) {
            emit(Resource.Error(exception.message.toString()))
        }
    }
}