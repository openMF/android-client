package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.DocumentListRepository
import com.mifos.core.network.GenericResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RemoveDocumentUseCase @Inject constructor(private val repository: DocumentListRepository) {

    suspend operator fun invoke(
        entityType: String,
        entityId: Int,
        documentId: Int
    ): Flow<Resource<GenericResponse>> = flow {
        try {
            emit(Resource.Loading())
            val response = repository.removeDocument(entityType, entityId, documentId)
            emit(Resource.Success(response))

        } catch (exception: Exception) {
            emit(Resource.Error(exception.message.toString()))
        }
    }
}