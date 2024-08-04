package com.mifos.core.data.repository_imp

import com.mifos.core.data.repository.SavingsAccountActivateRepository
import com.mifos.core.network.GenericResponse
import com.mifos.core.network.datamanager.DataManagerSavings
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 13/08/23.
 */
class SavingsAccountActivateRepositoryImp @Inject constructor(private val dataManagerSavings: DataManagerSavings) :
    SavingsAccountActivateRepository {

    override fun activateSavings(
        savingsAccountId: Int,
        request: HashMap<String, String>
    ): Observable<GenericResponse> {
        return dataManagerSavings.activateSavings(savingsAccountId, request)
    }
}