package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.DataTableDataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.openapitools.client.models.DeleteDataTablesDatatableAppTableIdDatatableIdResponse
import javax.inject.Inject

class DeleteDataTableEntryUseCase @Inject constructor(private val repository: DataTableDataRepository) {

    suspend operator fun invoke(
        table: String,
        entity: Int,
        rowId: Int
    ): Flow<Resource<DeleteDataTablesDatatableAppTableIdDatatableIdResponse>> = flow {
        try {
            emit(Resource.Loading())
            val response = repository.deleteDataTableEntry(table, entity, rowId)
            emit(Resource.Success(response))
        } catch (exception: Exception) {
            emit(Resource.Error(exception.message.toString()))
        }
    }

}