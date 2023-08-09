package com.mifos.repositories

import com.mifos.api.model.CollectionSheetPayload
import com.mifos.api.model.Payload
import com.mifos.objects.db.CollectionSheet
import com.mifos.objects.response.SaveResponse
import rx.Observable

/**
 * Created by Aditya Gupta on 10/08/23.
 */
interface CollectionSheetRepository {

    fun getCollectionSheet(id: Long, payload: Payload?): Observable<CollectionSheet>

    fun saveCollectionSheetAsync(
        id: Int,
        payload: CollectionSheetPayload?
    ): Observable<SaveResponse>
}