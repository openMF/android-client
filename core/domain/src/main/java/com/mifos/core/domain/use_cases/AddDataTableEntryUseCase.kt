package com.mifos.core.domain.use_cases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.DataTableRowDialogRepository
import com.mifos.core.network.GenericResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AddDataTableEntryUseCase @Inject constructor(private val repository: DataTableRowDialogRepository) {

    suspend operator fun invoke(
        table: String,
        entityId: Int,
        payload: Map<String, String>
    ): Flow<Resource<GenericResponse>> = flow {
        try {
            emit(Resource.Loading())
            val response = repository.addDataTableEntry(table, entityId, payload)
            emit(Resource.Success(response))

        } catch (exception: Exception) {
            emit(Resource.Error(exception.message.toString()))
        }
    }

}