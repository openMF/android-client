package com.mifos.repositories

import com.mifos.api.GenericResponse
import com.mifos.api.datamanager.DataManagerSavings
import rx.Observable
import javax.inject.Inject

class SavingsAccountActivateRepositoryImp @Inject constructor (private val dataManagerSavings: DataManagerSavings): SavingsAccountActivateRepository {

    override fun activateSavings(
        savingsAccountId: Int,
        request: HashMap<String, String>
    ): Observable<GenericResponse> {
        return dataManagerSavings.activateSavings(savingsAccountId, request)
    }
}