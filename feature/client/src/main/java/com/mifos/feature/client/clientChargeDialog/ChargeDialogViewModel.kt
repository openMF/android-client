/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientChargeDialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Resource
import com.mifos.core.domain.useCases.CreateChargesUseCase
import com.mifos.core.domain.useCases.GetAllChargesV2UseCase
import com.mifos.core.`object`.template.client.ChargeTemplate
import com.mifos.core.payloads.ChargesPayload
import com.mifos.feature.client.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChargeDialogViewModel @Inject constructor(
    private val getAllChargesV2UseCase: GetAllChargesV2UseCase,
    private val createChargesUseCase: CreateChargesUseCase,
) : ViewModel() {

    private val _chargeDialogUiState =
        MutableStateFlow<ChargeDialogUiState>(ChargeDialogUiState.Loading)
    val chargeDialogUiState = _chargeDialogUiState.asStateFlow()

    fun loadAllChargesV2(clientId: Int) = viewModelScope.launch(Dispatchers.IO) {
        getAllChargesV2UseCase(clientId).collect { result ->
            when (result) {
                is Resource.Error ->
                    _chargeDialogUiState.value =
                        ChargeDialogUiState.Error(R.string.feature_client_failed_to_load_client_charges)

                is Resource.Loading -> _chargeDialogUiState.value = ChargeDialogUiState.Loading

                is Resource.Success ->
                    _chargeDialogUiState.value =
                        ChargeDialogUiState.AllChargesV2(
                            result.data ?: ChargeTemplate(
                                false,
                                emptyList(),
                            ),
                        )
            }
        }
    }

    fun createCharges(clientId: Int, payload: ChargesPayload) =
        viewModelScope.launch(Dispatchers.IO) {
            createChargesUseCase(clientId, payload).collect { result ->
                when (result) {
                    is Resource.Error ->
                        _chargeDialogUiState.value =
                            ChargeDialogUiState.Error(R.string.feature_client_failed_to_create_charge)

                    is Resource.Loading -> _chargeDialogUiState.value = ChargeDialogUiState.Loading

                    is Resource.Success ->
                        _chargeDialogUiState.value =
                            ChargeDialogUiState.ChargesCreatedSuccessfully
                }
            }
        }
}
