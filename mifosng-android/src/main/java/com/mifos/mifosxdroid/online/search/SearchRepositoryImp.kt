package com.mifos.mifosxdroid.online.search

import com.mifos.api.datamanager.DataManagerSearch
import com.mifos.objects.SearchedEntity
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 06/08/23.
 */
class SearchRepositoryImp @Inject constructor(private val dataManagerSearch: DataManagerSearch) :
    SearchRepository {
    override suspend fun searchResources(
        query: String?,
        resources: String?,
        exactMatch: Boolean?
    ): List<SearchedEntity> {
        return dataManagerSearch.searchResources(query, resources, exactMatch)
    }

}