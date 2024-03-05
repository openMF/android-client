package com.mifos.mifosxdroid.online.collectionsheet

import com.mifos.core.network.DataManager
import com.mifos.core.network.model.CollectionSheetPayload
import com.mifos.core.network.model.Payload
import com.mifos.core.objects.db.CollectionSheet
import com.mifos.core.objects.response.SaveResponse
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 10/08/23.
 */
class CollectionSheetRepositoryImp @Inject constructor(private val dataManager: DataManager) :
    CollectionSheetRepository {

    override fun getCollectionSheet(id: Long, payload: Payload?): Observable<CollectionSheet> {
        return dataManager.getCollectionSheet(id, payload)
    }

    override fun saveCollectionSheetAsync(
        id: Int,
        payload: CollectionSheetPayload?
    ): Observable<SaveResponse> {
        return dataManager.saveCollectionSheetAsync(
            id, payload
        )
    }
}