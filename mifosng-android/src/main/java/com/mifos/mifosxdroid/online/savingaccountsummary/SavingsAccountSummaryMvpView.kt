package com.mifos.mifosxdroid.online.savingaccountsummary

import com.mifos.api.GenericResponse
import com.mifos.mifosxdroid.base.MvpView
import com.mifos.objects.accounts.savings.SavingsAccountWithAssociations

/**
 * Created by Rajan Maurya on 07/06/16.
 */
interface SavingsAccountSummaryMvpView : MvpView {
    fun showSavingAccount(savingsAccountWithAssociations: SavingsAccountWithAssociations?)
    fun showSavingsActivatedSuccessfully(genericResponse: GenericResponse?)
    fun showFetchingError(errorMessage: Int)
    fun showFetchingError(errorMessage: String?)
}