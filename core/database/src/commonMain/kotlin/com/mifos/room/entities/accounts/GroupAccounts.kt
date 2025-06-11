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

import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import com.mifos.room.entities.accounts.loans.LoanAccountEntity
import com.mifos.room.entities.accounts.savings.SavingsAccountEntity
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class GroupAccounts(
    var loanAccounts: List<LoanAccountEntity> = emptyList(),

    var savingsAccounts: List<SavingsAccountEntity> = emptyList(),
) : Parcelable {

    private fun getSavingsAccounts(wantRecurring: Boolean): List<SavingsAccountEntity> {
        val result: MutableList<SavingsAccountEntity> = ArrayList()
        for (account in savingsAccounts) {
            if (account.depositType?.isRecurring == wantRecurring) {
                result.add(account)
            }
        }
        return result
    }

    fun getRecurringSavingsAccounts(): List<SavingsAccountEntity> {
        return getSavingsAccounts(true)
    }

    fun getNonRecurringSavingsAccounts(): List<SavingsAccountEntity> {
        return getSavingsAccounts(false)
    }
}
