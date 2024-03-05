package com.mifos.mifosxdroid.online.collectionsheetindividualdetails

import com.mifos.core.network.GenericResponse
import com.mifos.core.network.model.IndividualCollectionSheetPayload
import rx.Observable

/**
 * Created by Aditya Gupta on 10/08/23.
 */
interface IndividualCollectionSheetDetailsRepository {

    fun saveIndividualCollectionSheet(
        payload: IndividualCollectionSheetPayload?
    ): Observable<GenericResponse>

}