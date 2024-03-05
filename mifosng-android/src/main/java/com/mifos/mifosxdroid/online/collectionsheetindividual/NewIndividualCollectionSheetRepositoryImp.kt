package com.mifos.mifosxdroid.online.collectionsheetindividual

import com.mifos.core.network.DataManager
import com.mifos.core.network.datamanager.DataManagerCollectionSheet
import com.mifos.core.network.model.RequestCollectionSheetPayload
import com.mifos.core.objects.collectionsheet.IndividualCollectionSheet
import com.mifos.core.objects.organisation.Office
import com.mifos.core.objects.organisation.Staff
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