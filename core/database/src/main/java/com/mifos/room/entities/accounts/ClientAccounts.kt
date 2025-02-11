/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.accounts

import android.os.Parcelable
import com.mifos.room.entities.accounts.loans.LoanAccount
import com.mifos.room.entities.accounts.savings.SavingsAccount
import kotlinx.parcelize.Parcelize

@Parcelize
data class ClientAccounts(
    var loanAccounts: List<LoanAccount> = ArrayList(),

    var savingsAccounts: List<SavingsAccount> = ArrayList(),
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
