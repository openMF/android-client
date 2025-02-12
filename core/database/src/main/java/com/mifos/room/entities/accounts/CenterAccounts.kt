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

/**
 * Created by mayankjindal on 11/07/17.
 */
@Parcelize
data class CenterAccounts(
    val loanAccounts: List<LoanAccount> = emptyList(),

    val savingsAccounts: List<SavingsAccount> = emptyList(),

    val memberLoanAccounts: List<LoanAccount> = emptyList(),
) : Parcelable
