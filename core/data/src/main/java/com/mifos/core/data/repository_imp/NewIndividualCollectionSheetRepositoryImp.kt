package com.mifos.core.data.repository_imp

import com.mifos.core.data.repository.NewIndividualCollectionSheetRepository
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
class NewIndividualCollectionSheetRepositoryImp @Inject constructor(
    private val dataManager: DataManager,
    private val dataManagerCollection: DataManagerCollectionSheet
) : NewIndividualCollectionSheetRepository {

    override suspend fun getIndividualCollectionSheet(payload: RequestCollectionSheetPayload?): IndividualCollectionSheet {
        return dataManagerCollection.getIndividualCollectionSheet(payload)
    }

    override suspend fun offices(): List<Office> {
        return dataManager.offices()
    }

    override suspend fun getStaffInOffice(officeId: Int): List<Staff> {
        return dataManager.getStaffInOffice(officeId)
    }

}