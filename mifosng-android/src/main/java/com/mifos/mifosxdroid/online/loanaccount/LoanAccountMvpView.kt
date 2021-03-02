package com.mifos.mifosxdroid.online.loanaccount

import com.mifos.mifosxdroid.base.MvpView
import com.mifos.objects.accounts.loan.Loans
import com.mifos.objects.organisation.LoanProducts
import com.mifos.objects.templates.loans.LoanTemplate

/**
 * Created by Rajan Maurya on 08/06/16.
 */
interface LoanAccountMvpView : MvpView {
    fun showAllLoan(productLoanses: List<LoanProducts>)
    fun showLoanAccountTemplate(loanTemplate: LoanTemplate)
    fun showLoanAccountCreatedSuccessfully(loans: Loans?)
    fun showMessage(messageId: Int)
    fun showFetchingError(s: String?)
}