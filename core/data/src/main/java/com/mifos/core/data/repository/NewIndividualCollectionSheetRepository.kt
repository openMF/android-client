package com.mifos.core.data.repository

import com.mifos.core.network.model.RequestCollectionSheetPayload
import com.mifos.core.objects.collectionsheet.IndividualCollectionSheet
import com.mifos.core.objects.organisation.Office
import com.mifos.core.objects.organisation.Staff
import rx.Observable

/**
 * Created by Aditya Gupta on 10/08/23.
 */
interface NewIndividualCollectionSheetRepository {

    suspend fun getIndividualCollectionSheet(
        payload: RequestCollectionSheetPayload?
    ): IndividualCollectionSheet

    suspend fun offices(): List<Office>

    suspend fun getStaffInOffice(officeId: Int): List<Staff>

}