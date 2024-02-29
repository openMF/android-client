package com.mifos.mifosxdroid.online.search

import com.mifos.core.common.utils.Resource
import com.mifos.objects.SearchedEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SearchUseCase @Inject constructor(private val repository: SearchRepository) {
    operator fun invoke(query: String?, resources: String?, exactMatch: Boolean?): Flow<Resource<List<SearchedEntity>>> = flow{
        emit(Resource.Loading())
        try {
            val searchedEntities = repository.searchResources(query, resources, exactMatch)
            if (searchedEntities.isEmpty()) {
                emit(Resource.Error("No Search Result found"))
            } else {
                emit(Resource.Success(searchedEntities))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An unexpected error occurred"))
        }
    }
}