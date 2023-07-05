package com.mifos.api.datamanager

import com.mifos.api.BaseApiManager
import com.mifos.api.GenericResponse
import com.mifos.api.model.IndividualCollectionSheetPayload
import com.mifos.api.model.RequestCollectionSheetPayload
import com.mifos.objects.collectionsheet.CenterDetail
import com.mifos.objects.collectionsheet.CollectionSheetPayload
import com.mifos.objects.collectionsheet.CollectionSheetRequestPayload
import com.mifos.objects.collectionsheet.CollectionSheetResponse
import com.mifos.objects.collectionsheet.IndividualCollectionSheet
import com.mifos.objects.collectionsheet.ProductiveCollectionSheetPayload
import com.mifos.objects.group.CenterWithAssociations
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Tarun on 22-07-2017.
 */
@Singleton
class DataManagerCollectionSheet @Inject constructor(private val mBaseApiManager: BaseApiManager) {
    /**
     * Individual CollectionSheet API
     */
    fun getIndividualCollectionSheet(
        payload: RequestCollectionSheetPayload?
    ): Observable<IndividualCollectionSheet> {
        return mBaseApiManager.collectionSheetApi.getIndividualCollectionSheet(payload)
    }

    fun saveIndividualCollectionSheet(
        payload: IndividualCollectionSheetPayload?
    ): Observable<GenericResponse> {
        return mBaseApiManager.collectionSheetApi.saveindividualCollectionSheet(payload)
    }

    /**
     * Productive CollectionSheet API
     */
    fun fetchCenterDetails(
        format: String?, locale: String?, meetingDate: String?, officeId: Int, staffId: Int
    ): Observable<List<CenterDetail>> {
        return mBaseApiManager.collectionSheetApi.fetchCenterDetails(
            format, locale, meetingDate, officeId, staffId
        )
    }

    fun fetchProductiveCollectionSheet(
        centerId: Int, payload: CollectionSheetRequestPayload?
    ): Observable<CollectionSheetResponse> {
        return mBaseApiManager.collectionSheetApi.fetchProductiveSheet(centerId, payload)
    }

    fun submitProductiveSheet(
        centerId: Int, payload: ProductiveCollectionSheetPayload?
    ): Observable<GenericResponse> {
        return mBaseApiManager.collectionSheetApi.submitProductiveSheet(centerId, payload)
    }

    /**
     * CollectionSheet API
     */
    fun fetchCollectionSheet(
        groupId: Int, payload: CollectionSheetRequestPayload?
    ): Observable<CollectionSheetResponse> {
        return mBaseApiManager.collectionSheetApi.fetchCollectionSheet(groupId, payload)
    }

    fun submitCollectionSheet(
        groupId: Int, payload: CollectionSheetPayload?
    ): Observable<GenericResponse> {
        return mBaseApiManager.collectionSheetApi.submitCollectionSheet(groupId, payload)
    }

    /**
     * Associated groups API
     */
    fun fetchGroupsAssociatedWithCenter(centerId: Int): Observable<CenterWithAssociations> {
        return mBaseApiManager.collectionSheetApi.fetchGroupsAssociatedWithCenter(centerId)
    }
}