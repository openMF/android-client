package com.mifos.api.datamanager

import com.mifos.api.BaseApiManager
import com.mifos.api.mappers.SearchMapper
import javax.inject.Singleton
import javax.inject.Inject
import org.apache.fineract.client.services.SearchApiApi
import com.mifos.objects.SearchedEntity
import rx.Observable

/**
 * Created by Rajan Maurya on 12/12/16.
 */
@Singleton
class DataManagerSearch @Inject constructor(
    val baseApiManager: BaseApiManager,
    private val sdkBaseApiManager: org.mifos.core.apimanager.BaseApiManager
) {
    private val searchApi: SearchApiApi
        get() = sdkBaseApiManager.getSearchApi()

    fun searchResources(
        query: String?, resources: String?,
        exactMatch: Boolean?
    ): Observable<List<SearchedEntity>> {
        // todo: invalid return type of method 'searchData'. It should be 'List<GetSearchResponse>'
        return searchApi.searchData(query, resources, exactMatch)
            .map { SearchMapper.mapFromEntityList(it) }
    }
}