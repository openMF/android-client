/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.accounts

import android.os.Parcelable
import com.mifos.core.objects.accounts.loan.LoanAccount
import com.mifos.core.objects.accounts.savings.SavingsAccount
import kotlinx.parcelize.Parcelize

@Parcelize
data class GroupAccounts(
    var loanAccounts: List<LoanAccount> = ArrayList(),

    var savingsAccounts: List<SavingsAccount> = ArrayList()
) : Parcelable {

    private fun getSavingsAccounts(wantRecurring: Boolean): List<SavingsAccount> {
        val result: MutableList<SavingsAccount> = ArrayList()
        for (account in savingsAccounts) {
            if (account.depositType?.isRecurring == wantRecurring) {
                result.add(account)
            }
        }
        return result
    }

    fun getRecurringSavingsAccounts(): List<SavingsAccount> {
        return getSavingsAccounts(true)
    }

    fun getNonRecurringSavingsAccounts(): List<SavingsAccount> {
        return getSavingsAccounts(false)
    }

}