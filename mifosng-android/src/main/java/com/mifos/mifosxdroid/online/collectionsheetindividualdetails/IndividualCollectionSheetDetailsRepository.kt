package com.mifos.mifosxdroid.online.collectionsheetindividualdetails

import com.mifos.api.GenericResponse
import com.mifos.api.model.IndividualCollectionSheetPayload
import rx.Observable

/**
 * Created by Aditya Gupta on 10/08/23.
 */
interface IndividualCollectionSheetDetailsRepository {

    fun saveIndividualCollectionSheet(
        payload: IndividualCollectionSheetPayload?
    ): Observable<GenericResponse>

}