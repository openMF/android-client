package com.mifos.core.domain.use_cases

import com.google.gson.JsonArray
import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.DataTableDataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetDataTableInfoUseCase @Inject constructor(private val repository: DataTableDataRepository) {

    suspend operator fun invoke(table: String, entityId: Int): Flow<Resource<JsonArray>> = flow {
        try {
            emit(Resource.Loading())
            val data = repository.getDataTableInfo(table, entityId)
            emit(Resource.Success(data))

        } catch (exception: Exception) {
            emit(Resource.Error(exception.message.toString()))
        }
    }
}