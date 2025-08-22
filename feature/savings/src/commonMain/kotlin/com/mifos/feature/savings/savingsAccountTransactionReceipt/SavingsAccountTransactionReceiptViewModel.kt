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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.SavingsAccountTransactionReceiptRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Created by Arin Yadav on 20/08/2025
 */
class SavingsAccountTransactionReceiptViewModel(
    private val repository: SavingsAccountTransactionReceiptRepository,
) : ViewModel() {

    private val _savingsAccountTransactionReceiptUiState =
        MutableStateFlow<SavingsAccountTransactionReceiptUiState>(
            SavingsAccountTransactionReceiptUiState.Idle,
        )
    val savingsAccountTransactionReceiptUiState =
        _savingsAccountTransactionReceiptUiState.asStateFlow()

    fun loadReceipt(transactionId: Int) {
        viewModelScope.launch {
            repository.getSavingsAccountTransactionReceipt(transactionId)
                .collect { dataState ->
                    when (dataState) {
                        is DataState.Loading -> {
                            _savingsAccountTransactionReceiptUiState.value =
                                SavingsAccountTransactionReceiptUiState.ShowProgressbar
                        }

                        is DataState.Error -> {
                            _savingsAccountTransactionReceiptUiState.value =
                                SavingsAccountTransactionReceiptUiState.ShowError(
                                    "Failed to Fetch Receipt",
                                )
                        }

                        is DataState.Success -> {
                            _savingsAccountTransactionReceiptUiState.value =
                                SavingsAccountTransactionReceiptUiState.ShowReceipt(dataState.data)
                        }
                    }
                }
        }
    }

    fun makeIdle() {
        _savingsAccountTransactionReceiptUiState.value = SavingsAccountTransactionReceiptUiState.Idle
    }
}
