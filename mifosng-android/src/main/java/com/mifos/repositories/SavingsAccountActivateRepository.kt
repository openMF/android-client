package com.mifos.repositories

import com.mifos.api.GenericResponse
import rx.Observable

interface SavingsAccountActivateRepository {

    fun activateSavings(
        savingsAccountId: Int,
        request: HashMap<String, String>
    ): Observable<GenericResponse>

}