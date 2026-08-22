/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.savings.savingsAccountActivate

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.domain.useCases.ActivateSavingsUseCase
import com.mifos.feature.savings.navigation.SavingsAccountActivate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * Created by Aditya Gupta on 13/08/23.
 */
class SavingsAccountActivateViewModel(
    private val activateSavingsUseCase: ActivateSavingsUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val savingsAccountId = savedStateHandle.toRoute<SavingsAccountActivate>().savingsAccountId

    private val _savingsAccountActivateUiState =
        MutableStateFlow<SavingsAccountActivateUiState>(SavingsAccountActivateUiState.Initial)
    val savingsAccountActivateUiState: StateFlow<SavingsAccountActivateUiState>
        get() = _savingsAccountActivateUiState.asStateFlow()

    fun activateSavings(request: HashMap<String, String>) =
        viewModelScope.launch {
            _savingsAccountActivateUiState.value =
                SavingsAccountActivateUiState.ShowProgressbar
            activateSavingsUseCase(savingsAccountId, request)
                .catch { error ->
                    _savingsAccountActivateUiState.value =
                        SavingsAccountActivateUiState.ShowError(error.message ?: "")
                }
                .collect { response ->
                    _savingsAccountActivateUiState.value =
                        SavingsAccountActivateUiState.ShowSavingAccountActivatedSuccessfully(
                            response,
                        )
                }
        }
}
