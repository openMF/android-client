package com.mifos.mifosxdroid.online.grouploanaccount

import com.mifos.mifosxdroid.base.MvpView
import com.mifos.objects.accounts.loan.Loans
import com.mifos.objects.organisation.LoanProducts
import com.mifos.objects.templates.loans.GroupLoanTemplate

/**
 * Created by Rajan Maurya on 08/06/16.
 */
interface GroupLoanAccountMvpView : MvpView {
    fun showAllLoans(productLoans: List<LoanProducts?>?)
    fun showGroupLoansAccountCreatedSuccessfully(loans: Loans?)
    fun showGroupLoanTemplate(groupLoanTemplate: GroupLoanTemplate?)
    fun showFetchingError(s: String?)
}