package com.mifos.mifosxdroid.online.collectionsheetindividual

import com.mifos.api.DataManager
import com.mifos.api.datamanager.DataManagerCollectionSheet
import com.mifos.api.model.RequestCollectionSheetPayload
import com.mifos.objects.collectionsheet.IndividualCollectionSheet
import com.mifos.objects.organisation.Office
import com.mifos.objects.organisation.Staff
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 10/08/23.
 */
class NewIndividualCollectionSheetRepositoryImp @Inject internal constructor(
    private val dataManager: DataManager,
    private val dataManagerCollection: DataManagerCollectionSheet
) : NewIndividualCollectionSheetRepository {

    override fun getIndividualCollectionSheet(payload: RequestCollectionSheetPayload?): Observable<IndividualCollectionSheet> {
        return dataManagerCollection.getIndividualCollectionSheet(payload)
    }

    override fun offices(): Observable<List<Office>> {
        return dataManager.offices
    }

    override fun getStaffInOffice(officeId: Int): Observable<List<Staff>> {
        return dataManager.getStaffInOffice(officeId)
    }

}