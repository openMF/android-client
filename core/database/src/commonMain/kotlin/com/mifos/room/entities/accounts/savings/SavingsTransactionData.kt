/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.accounts.savings

import com.mifos.core.model.utils.IgnoredOnParcel
import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import kotlinx.serialization.Serializable

/**
 * Created by Pronay Sarker on 15/08/2024 (11:12 PM)
 */
@Parcelize
@Serializable
data class SavingsTransactionData(
    @IgnoredOnParcel
    val savingsAccountWithAssociations: SavingsAccountWithAssociationsEntity = SavingsAccountWithAssociationsEntity(),
    val depositType: SavingAccountDepositTypeEntity?,
    val transactionType: String,
) : Parcelable
