package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.ReportDetailRepository
import com.mifos.core.objects.runreports.FullParameterListResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetRunReportProductUseCase @Inject constructor(private val repository: ReportDetailRepository) {

    suspend operator fun invoke(
        parameterName: String,
        currency: String,
        parameterType: Boolean
    ): Flow<Resource<FullParameterListResponse>> = flow {
        try {
            emit(Resource.Loading())
            val response = repository.getRunReportProduct(parameterName, currency, parameterType)
            emit(Resource.Success(response))
        } catch (exception: Exception) {
            emit(Resource.Error(exception.message.toString()))
        }
    }
}