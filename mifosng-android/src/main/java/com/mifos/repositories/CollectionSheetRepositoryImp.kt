package com.mifos.repositories

import com.mifos.api.DataManager
import com.mifos.api.model.CollectionSheetPayload
import com.mifos.api.model.Payload
import com.mifos.objects.db.CollectionSheet
import com.mifos.objects.response.SaveResponse
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