package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.ReportCategoryRepository
import com.mifos.core.objects.runreports.client.ClientReportTypeItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetReportCategoryUseCase @Inject constructor(private val repository: ReportCategoryRepository) {

    suspend operator fun invoke(
        reportCategory: String,
        genericResultSet: Boolean,
        parameterType: Boolean
    ): Flow<Resource<List<ClientReportTypeItem>>> = flow {
        try {
            emit(Resource.Loading())
            val response =
                repository.getReportCategories(reportCategory, genericResultSet, parameterType)
            emit(Resource.Success(response))
        } catch (exception: Exception) {
            emit(Resource.Error(exception.message.toString()))
        }
    }
}