package com.mifos.core.data.repository

import com.mifos.core.network.GenericResponse
import rx.Observable

/**
 * Created by Aditya Gupta on 13/08/23.
 */
interface SavingsAccountActivateRepository {

    fun activateSavings(
        savingsAccountId: Int,
        request: HashMap<String, String>
    ): Observable<GenericResponse>

}