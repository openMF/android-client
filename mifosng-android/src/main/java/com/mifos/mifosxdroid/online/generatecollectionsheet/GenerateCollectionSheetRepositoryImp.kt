package com.mifos.mifosxdroid.online.generatecollectionsheet

import com.mifos.core.network.DataManager
import com.mifos.core.network.GenericResponse
import com.mifos.core.network.datamanager.DataManagerCollectionSheet
import com.mifos.core.objects.collectionsheet.CenterDetail
import com.mifos.core.objects.collectionsheet.CollectionSheetPayload
import com.mifos.core.objects.collectionsheet.CollectionSheetRequestPayload
import com.mifos.core.objects.collectionsheet.CollectionSheetResponse
import com.mifos.core.objects.collectionsheet.ProductiveCollectionSheetPayload
import com.mifos.core.objects.group.Center
import com.mifos.core.objects.group.CenterWithAssociations
import com.mifos.core.objects.group.Group
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 12/08/23.
 */
class GenerateCollectionSheetRepositoryImp @Inject constructor(
    private val dataManager: DataManager,
    private val collectionDataManager: DataManagerCollectionSheet
) : GenerateCollectionSheetRepository {

    override fun getCentersInOffice(
        id: Int,
        params: Map<String, String>
    ): Observable<List<Center>> {
        return dataManager.getCentersInOffice(id, params)
    }

    override fun getGroupsByOffice(
        office: Int,
        params: Map<String, String>
    ): Observable<List<Group>> {
        return dataManager.getGroupsByOffice(office, params)
    }

    override fun fetchGroupsAssociatedWithCenter(centerId: Int): Observable<CenterWithAssociations> {
        return collectionDataManager.fetchGroupsAssociatedWithCenter(centerId)
    }

    override fun fetchCenterDetails(
        format: String?,
        locale: String?,
        meetingDate: String?,
        officeId: Int,
        staffId: Int
    ): Observable<List<CenterDetail>> {
        return collectionDataManager.fetchCenterDetails(
            format,
            locale,
            meetingDate,
            officeId,
            staffId
        )
    }

    override fun fetchProductiveCollectionSheet(
        centerId: Int,
        payload: CollectionSheetRequestPayload?
    ): Observable<CollectionSheetResponse> {
        return collectionDataManager.fetchProductiveCollectionSheet(centerId, payload)
    }

    override fun fetchCollectionSheet(
        groupId: Int,
        payload: CollectionSheetRequestPayload?
    ): Observable<CollectionSheetResponse> {
        return collectionDataManager.fetchCollectionSheet(groupId, payload)
    }

    override fun submitProductiveSheet(
        centerId: Int,
        payload: ProductiveCollectionSheetPayload?
    ): Observable<GenericResponse> {
        return collectionDataManager.submitProductiveSheet(centerId, payload)
    }

    override fun submitCollectionSheet(
        groupId: Int,
        payload: CollectionSheetPayload?
    ): Observable<GenericResponse> {
        return collectionDataManager.submitCollectionSheet(groupId, payload)
    }
}