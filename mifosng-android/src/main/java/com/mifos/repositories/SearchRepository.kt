package com.mifos.repositories

import com.mifos.objects.SearchedEntity
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