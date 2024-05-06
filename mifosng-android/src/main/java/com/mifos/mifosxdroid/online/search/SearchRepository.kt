package com.mifos.mifosxdroid.online.search

import com.mifos.core.objects.SearchedEntity
import rx.Observable

/**
 * Created by Aditya Gupta on 06/08/23.
 */
interface SearchRepository {

    fun searchResources(
        query: String?,
        resources: String?,
        exactMatch: Boolean?
    ): Observable<List<SearchedEntity>>

}