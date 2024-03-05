package com.mifos.mifosxdroid.online.search

import com.mifos.core.network.datamanager.DataManagerSearch
import com.mifos.core.objects.SearchedEntity
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 06/08/23.
 */
class SearchRepositoryImp @Inject constructor(private val dataManagerSearch: DataManagerSearch) :
    SearchRepository {
    override fun searchResources(
        query: String?,
        resources: String?,
        exactMatch: Boolean?
    ): Observable<List<SearchedEntity>> {
        return dataManagerSearch.searchResources(query, resources, exactMatch)
    }

}