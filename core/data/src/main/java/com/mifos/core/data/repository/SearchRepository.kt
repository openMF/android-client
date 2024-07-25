package com.mifos.core.data.repository

import com.mifos.core.objects.SearchedEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by Aditya Gupta on 06/08/23.
 */
interface SearchRepository {

    suspend fun searchResources(
        query: String,
        resources: String?,
        exactMatch: Boolean?
    ): Flow<List<SearchedEntity>>
}