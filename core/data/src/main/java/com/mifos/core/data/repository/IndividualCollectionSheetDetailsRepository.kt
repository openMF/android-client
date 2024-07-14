package com.mifos.core.data.repository

import com.mifos.core.network.GenericResponse
import com.mifos.core.network.model.IndividualCollectionSheetPayload
import rx.Observable

/**
 * Created by Aditya Gupta on 10/08/23.
 */
interface IndividualCollectionSheetDetailsRepository {

    suspend fun saveIndividualCollectionSheet(
        payload: IndividualCollectionSheetPayload
    ): GenericResponse

}