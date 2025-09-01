/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.savings.savingsAccountApproval

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.domain.useCases.ApproveSavingsApplicationUseCase
import com.mifos.core.model.objects.account.loan.SavingsApproval
import com.mifos.feature.savings.navigation.SavingsAccountApproval
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Created by Aditya Gupta on 13/08/23.
 */
class SavingsAccountApprovalViewModel(
    private val approveSavingsApplicationUseCase: ApproveSavingsApplicationUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val savingsAccountId =
        savedStateHandle.toRoute<SavingsAccountApproval>().savingsAccountId

    private val _savingsAccountApprovalUiState =
        MutableStateFlow<SavingsAccountApprovalUiState>(SavingsAccountApprovalUiState.Initial)
    val savingsAccountApprovalUiState: StateFlow<SavingsAccountApprovalUiState>
        get() = _savingsAccountApprovalUiState.asStateFlow()

    fun approveSavingsApplication(savingsApproval: SavingsApproval?) =
        viewModelScope.launch {
            approveSavingsApplicationUseCase(savingsAccountId, savingsApproval)
                .collect { dataState ->
                    when (dataState) {
                        is DataState.Error -> {
                            _savingsAccountApprovalUiState.value =
                                SavingsAccountApprovalUiState.ShowError(
                                    dataState.message,
                                )
                        }

                        DataState.Loading -> {
                            _savingsAccountApprovalUiState.value =
                                SavingsAccountApprovalUiState.ShowProgressbar
                        }

                        is DataState.Success -> {
                            _savingsAccountApprovalUiState.value =
                                SavingsAccountApprovalUiState.ShowSavingAccountApprovedSuccessfully(
                                    dataState.data,
                                )
                        }
                    }
                }
        }
}
