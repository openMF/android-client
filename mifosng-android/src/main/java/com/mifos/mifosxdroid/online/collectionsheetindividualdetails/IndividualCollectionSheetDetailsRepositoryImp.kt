package com.mifos.mifosxdroid.online.collectionsheetindividualdetails

import com.mifos.api.GenericResponse
import com.mifos.api.datamanager.DataManagerCollectionSheet
import com.mifos.api.model.IndividualCollectionSheetPayload
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 10/08/23.
 */
class IndividualCollectionSheetDetailsRepositoryImp @Inject constructor(private val dataManagerCollection: DataManagerCollectionSheet) :
    IndividualCollectionSheetDetailsRepository {

    override fun saveIndividualCollectionSheet(payload: IndividualCollectionSheetPayload?): Observable<GenericResponse> {
        return dataManagerCollection.saveIndividualCollectionSheet(payload)
    }
}