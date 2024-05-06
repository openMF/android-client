package com.mifos.mifosxdroid.online.collectionsheet

import com.mifos.core.network.model.CollectionSheetPayload
import com.mifos.core.network.model.Payload
import com.mifos.core.objects.db.CollectionSheet
import com.mifos.core.objects.response.SaveResponse
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