/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
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

    var memberLoanAccounts: List<LoanAccount> = ArrayList(),
) : Parcelable
