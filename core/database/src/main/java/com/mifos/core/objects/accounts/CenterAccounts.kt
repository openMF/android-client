package com.mifos.core.objects.accounts

import android.os.Parcelable
import com.mifos.core.objects.accounts.loan.LoanAccount
import com.mifos.core.objects.accounts.savings.SavingsAccount
import kotlinx.parcelize.Parcelize

/**
 * Created by mayankjindal on 11/07/17.
 */
@Parcelize
data class CenterAccounts(
    var loanAccounts: List<LoanAccount> = ArrayList(),

    var savingsAccounts: List<SavingsAccount> = ArrayList(),

    var memberLoanAccounts: List<LoanAccount> = ArrayList()
) : Parcelable