/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.individualCollectionSheet.individualCollectionSheetDetail

import com.mifos.room.entities.collectionsheet.IndividualCollectionSheet

data class IndividualCollectionSheetDetailUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val collectionSheet: IndividualCollectionSheet? = null,
    val repaymentDate: String = "",
)

data class LoanCollectionItem(
    val loanId: Int,
    val accountId: String,
    val clientName: String,
    val productName: String,
    val totalDue: Double,
    val charges: Double,
    val principalDue: Double,
    val interestDue: Double,
    val feeDue: Double,
    val currencySymbol: String,
)

data class SavingsCollectionItem(
    val clientName: String,
    val depositAccountType: String,
    val savingsAccountId: String = "",
    val productName: String = "",
    val totalDue: Double = 0.0,
    val currencySymbol: String = "$",
)
