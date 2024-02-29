package com.mifos.api.datamanager

import com.mifos.api.BaseApiManager
import com.mifos.objects.SearchedEntity
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Rajan Maurya on 12/12/16.
 */
@Singleton
class DataManagerSearch @Inject constructor(private val baseApiManager: BaseApiManager) {
    suspend fun searchResources(
        query: String?, resources: String?,
        exactMatch: Boolean?
    ): List<SearchedEntity> {
        return baseApiManager.searchApi.searchResources(query, resources, exactMatch)
    }
}