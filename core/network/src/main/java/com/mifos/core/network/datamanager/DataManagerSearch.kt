package com.mifos.core.network.datamanager

import com.mifos.core.network.BaseApiManager
import com.mifos.core.objects.SearchedEntity
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Rajan Maurya on 12/12/16.
 */
@Singleton
class DataManagerSearch @Inject constructor(
    private val baseApiManager: BaseApiManager
) {
    suspend fun searchResources(
        query: String,
        resource: String?,
        exactMatch: Boolean?
    ): List<SearchedEntity> {
        return baseApiManager.searchApi.searchResources(query, resource, exactMatch)
    }
}