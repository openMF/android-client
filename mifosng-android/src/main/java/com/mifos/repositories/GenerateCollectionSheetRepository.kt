package com.mifos.repositories

import com.mifos.api.GenericResponse
import com.mifos.objects.collectionsheet.CenterDetail
import com.mifos.objects.collectionsheet.CollectionSheetPayload
import com.mifos.objects.collectionsheet.CollectionSheetRequestPayload
import com.mifos.objects.collectionsheet.CollectionSheetResponse
import com.mifos.objects.collectionsheet.ProductiveCollectionSheetPayload
import com.mifos.objects.group.Center
import com.mifos.objects.group.CenterWithAssociations
import com.mifos.objects.group.Group
import rx.Observable

/**
 * Created by Aditya Gupta on 12/08/23.
 */
interface GenerateCollectionSheetRepository {

    fun getCentersInOffice(id: Int, params: Map<String, String>): Observable<List<Center>>

    fun getGroupsByOffice(
        office: Int,
        params: Map<String, String>
    ): Observable<List<Group>>

    fun fetchGroupsAssociatedWithCenter(centerId: Int): Observable<CenterWithAssociations>

    fun fetchCenterDetails(
        format: String?, locale: String?, meetingDate: String?, officeId: Int, staffId: Int
    ): Observable<List<CenterDetail>>

    fun fetchProductiveCollectionSheet(
        centerId: Int, payload: CollectionSheetRequestPayload?
    ): Observable<CollectionSheetResponse>

    fun fetchCollectionSheet(
        groupId: Int, payload: CollectionSheetRequestPayload?
    ): Observable<CollectionSheetResponse>

    fun submitProductiveSheet(
        centerId: Int, payload: ProductiveCollectionSheetPayload?
    ): Observable<GenericResponse>

    fun submitCollectionSheet(
        groupId: Int, payload: CollectionSheetPayload?
    ): Observable<GenericResponse>
}