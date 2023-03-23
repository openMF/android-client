package com.mifos.api.datamanager

import com.mifos.api.BaseApiManager
import com.mifos.objects.SearchedEntity
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Atharv Tare on 23/03/23.
 */
@Singleton
class DataManagerSearch @Inject constructor(val baseApiManager: BaseApiManager) {
    fun searchResources(
        query: String?, resources: String?,
        exactMatch: Boolean?
    ): Observable<List<SearchedEntity>> {
        return baseApiManager.searchApi.searchResources(query, resources, exactMatch)
    }
}
