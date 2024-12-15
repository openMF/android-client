package com.mifos.core.domain.useCases

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.CreateNewGroupRepository
import com.mifos.core.objects.organisation.Office
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Created by Pronay Sarker on 01/08/2024 (8:23 AM)
 */

class GetGroupOfficesUseCase @Inject constructor(private val repository: CreateNewGroupRepository) {

    suspend operator fun invoke(): Flow<Resource<List<Office>>> = flow {
        try {
            emit(Resource.Loading())
            val response = repository.offices()
            emit(Resource.Success(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.message.toString()))
        }
    }
}