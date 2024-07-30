package com.mifos.core.data.repository_imp

import com.mifos.core.data.repository.GenerateCollectionSheetRepository
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
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 12/08/23.
 */
class GenerateCollectionSheetRepositoryImp @Inject constructor(
    private val dataManager: DataManager,
    private val collectionDataManager: DataManagerCollectionSheet
) : GenerateCollectionSheetRepository {

    override suspend fun getCentersInOffice(
        id: Int,
        params: Map<String, String>
    ): List<Center> {
        return dataManager.getCentersInOffice(id, params)
    }

    override suspend fun getGroupsByOffice(
        office: Int,
        params: Map<String, String>
    ): List<Group> {
        return dataManager.getGroupsByOffice(office, params)
    }

    override suspend fun fetchGroupsAssociatedWithCenter(centerId: Int): CenterWithAssociations {
        return collectionDataManager.fetchGroupsAssociatedWithCenter(centerId)
    }

    override suspend fun fetchCenterDetails(
        format: String?,
        locale: String?,
        meetingDate: String?,
        officeId: Int,
        staffId: Int
    ): List<CenterDetail> {
        return collectionDataManager.fetchCenterDetails(
            format,
            locale,
            meetingDate,
            officeId,
            staffId
        )
    }

    override suspend fun fetchProductiveCollectionSheet(
        centerId: Int,
        payload: CollectionSheetRequestPayload?
    ): CollectionSheetResponse {
        return collectionDataManager.fetchProductiveCollectionSheet(centerId, payload)
    }

    override suspend fun fetchCollectionSheet(
        groupId: Int,
        payload: CollectionSheetRequestPayload?
    ): CollectionSheetResponse {
        return collectionDataManager.fetchCollectionSheet(groupId, payload)
    }

    override suspend fun submitProductiveSheet(
        centerId: Int,
        payload: ProductiveCollectionSheetPayload?
    ): GenericResponse {
        return collectionDataManager.submitProductiveSheet(centerId, payload)
    }

    override suspend fun submitCollectionSheet(
        groupId: Int,
        payload: CollectionSheetPayload?
    ): GenericResponse {
        return collectionDataManager.submitCollectionSheet(groupId, payload)
    }
}