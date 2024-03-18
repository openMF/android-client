package com.mifos.mifosxdroid.online.generatecollectionsheet

import com.mifos.core.network.GenericResponse
import com.mifos.core.objects.collectionsheet.CenterDetail
import com.mifos.core.objects.collectionsheet.CollectionSheetPayload
import com.mifos.core.objects.collectionsheet.CollectionSheetRequestPayload
import com.mifos.core.objects.collectionsheet.CollectionSheetResponse
import com.mifos.core.objects.collectionsheet.ProductiveCollectionSheetPayload
import com.mifos.core.objects.group.Center
import com.mifos.core.objects.group.CenterWithAssociations
import com.mifos.core.objects.group.Group
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