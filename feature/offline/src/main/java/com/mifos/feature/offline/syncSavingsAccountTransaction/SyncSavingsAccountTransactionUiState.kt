/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.offline.syncSavingsAccountTransaction

import com.mifos.core.objects.accounts.savings.SavingsAccountTransactionRequest

/**
 * Created by Aditya Gupta on 16/08/23.
 */
sealed class SyncSavingsAccountTransactionUiState {
    data object Loading : SyncSavingsAccountTransactionUiState()

    data class ShowError(val message: Int) : SyncSavingsAccountTransactionUiState()

    data class ShowSavingsAccountTransactions(
        val savingsList: MutableList<SavingsAccountTransactionRequest>,
        val paymentTypeOptions: List<com.mifos.core.objects.PaymentTypeOption>,
    ) :
        SyncSavingsAccountTransactionUiState()

    data class ShowEmptySavingsAccountTransactions(val message: Int) :
        SyncSavingsAccountTransactionUiState()
}
