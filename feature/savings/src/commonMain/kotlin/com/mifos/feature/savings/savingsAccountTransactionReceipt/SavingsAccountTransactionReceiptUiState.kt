/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.savings.savingsAccountTransactionReceipt

/**
 * Created by Arin Yadav on 20/08/2025
 */
sealed class SavingsAccountTransactionReceiptUiState {

    data object Idle : SavingsAccountTransactionReceiptUiState()
    data object ShowProgressbar : SavingsAccountTransactionReceiptUiState()
    data class ShowError(val message: String) : SavingsAccountTransactionReceiptUiState()
    data class ShowReceipt(val receipt: ByteArray) : SavingsAccountTransactionReceiptUiState()
}
