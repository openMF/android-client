package com.mifos.core.data.repository_imp

import com.mifos.core.data.repository.IndividualCollectionSheetDetailsRepository
import com.mifos.core.network.GenericResponse
import com.mifos.core.network.datamanager.DataManagerCollectionSheet
import com.mifos.core.network.model.IndividualCollectionSheetPayload
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 10/08/23.
 */
class IndividualCollectionSheetDetailsRepositoryImp @Inject constructor(private val dataManagerCollection: DataManagerCollectionSheet) :
    IndividualCollectionSheetDetailsRepository {

    override suspend fun saveIndividualCollectionSheet(payload: IndividualCollectionSheetPayload): GenericResponse {
        return dataManagerCollection.saveIndividualCollectionSheet(payload)
    }
}