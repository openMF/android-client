package com.mifos.feature.individual_collection_sheet.new_individual_collection_sheet.domain.use_case

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.NewIndividualCollectionSheetRepository
import com.mifos.core.objects.organisation.Office
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetOfficeListUseCase @Inject constructor(private val repository: NewIndividualCollectionSheetRepository) {

    suspend operator fun invoke(): Flow<Resource<List<Office>>> = flow {
        try {
            emit(Resource.Loading())
            emit(Resource.Success(repository.offices()))
        } catch (exception: Exception) {
            emit(Resource.Error(exception.message.toString()))
        }
    }
}