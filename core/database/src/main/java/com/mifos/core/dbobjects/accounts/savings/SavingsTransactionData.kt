/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.dbobjects.accounts.savings

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Pronay Sarker on 15/08/2024 (11:12 PM)
 */
@Parcelize
data class SavingsTransactionData(
    val savingsAccountWithAssociations: SavingsAccountWithAssociations,
    val depositType: DepositType?,
    val transactionType: String,
) : Parcelable
