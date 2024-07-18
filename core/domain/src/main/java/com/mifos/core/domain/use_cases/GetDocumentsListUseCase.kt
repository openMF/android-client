package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.DocumentListRepository
import com.mifos.core.objects.noncore.Document
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetDocumentsListUseCase @Inject constructor(private val repository: DocumentListRepository) {

    suspend operator fun invoke(
        entityType: String,
        entityId: Int
    ): Flow<Resource<List<Document>>> = flow {
        try {
            emit(Resource.Loading())
            val documents = repository.getDocumentsList(entityType, entityId)
            emit(Resource.Success(documents))
        } catch (exception: Exception) {
            emit(Resource.Error(exception.message.toString()))
        }
    }

}