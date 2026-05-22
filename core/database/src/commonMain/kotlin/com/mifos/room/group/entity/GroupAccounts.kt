/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room.group.entity

import com.mifos.room.loan.entity.LoanAccountEntity
import com.mifos.room.savings.entity.SavingsAccountEntity
import kotlinx.serialization.Serializable

@Serializable
data class GroupAccounts(
    var loanAccounts: List<LoanAccountEntity> = emptyList(),

    var savingsAccounts: List<SavingsAccountEntity> = emptyList(),
) {

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
