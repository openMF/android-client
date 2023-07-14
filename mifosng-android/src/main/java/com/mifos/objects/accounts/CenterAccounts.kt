package com.mifos.objects.accounts

import android.os.Parcelable
import com.mifos.objects.accounts.loan.LoanAccount
import com.mifos.objects.accounts.savings.SavingsAccount
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