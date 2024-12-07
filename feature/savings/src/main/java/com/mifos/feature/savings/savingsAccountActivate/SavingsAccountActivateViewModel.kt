/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.savings.savingsAccountActivate

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.Resource
import com.mifos.core.domain.useCases.ActivateSavingsUseCase
import com.mifos.core.network.GenericResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 13/08/23.
 */
@HiltViewModel
class SavingsAccountActivateViewModel @Inject constructor(
    private val activateSavingsUseCase: ActivateSavingsUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val savingsAccountId = savedStateHandle.getStateFlow(Constants.SAVINGS_ACCOUNT_ID, 0)

    private val _savingsAccountActivateUiState =
        MutableStateFlow<SavingsAccountActivateUiState>(SavingsAccountActivateUiState.Initial)

    val savingsAccountActivateUiState: StateFlow<SavingsAccountActivateUiState>
        get() = _savingsAccountActivateUiState

    fun activateSavings(savingsAccountId: Int, request: HashMap<String, String>) =
        viewModelScope.launch(Dispatchers.IO) {
            activateSavingsUseCase(savingsAccountId, request).collect { result ->
                when (result) {
                    is Resource.Error ->
                        _savingsAccountActivateUiState.value =
                            SavingsAccountActivateUiState.ShowError(result.message.toString())

                    is Resource.Loading ->
                        _savingsAccountActivateUiState.value =
                            SavingsAccountActivateUiState.ShowProgressbar

                    is Resource.Success ->
                        _savingsAccountActivateUiState.value =
                            SavingsAccountActivateUiState.ShowSavingAccountActivatedSuccessfully(
                                result.data ?: GenericResponse(),
                            )
                }
            }
        }
}
