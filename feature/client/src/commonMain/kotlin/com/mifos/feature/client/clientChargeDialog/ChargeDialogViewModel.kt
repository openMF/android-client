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

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.feature_client_failed_to_create_charge
import androidclient.feature.client.generated.resources.feature_client_failed_to_load_client_charges
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.DataState
import com.mifos.core.domain.useCases.CreateChargesUseCase
import com.mifos.core.domain.useCases.GetAllChargesV2UseCase
import com.mifos.core.model.objects.payloads.ChargesPayload
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChargeDialogViewModel(
    private val getAllChargesV2UseCase: GetAllChargesV2UseCase,
    private val createChargesUseCase: CreateChargesUseCase,
) : ViewModel() {

    private val _chargeDialogUiState =
        MutableStateFlow<ChargeDialogUiState>(ChargeDialogUiState.Loading)
    val chargeDialogUiState = _chargeDialogUiState.asStateFlow()

    fun loadAllChargesV2(clientId: Int) = viewModelScope.launch {
        getAllChargesV2UseCase(clientId).collect { result ->
            when (result) {
                is DataState.Error ->
                    _chargeDialogUiState.value =
                        ChargeDialogUiState.Error(Res.string.feature_client_failed_to_load_client_charges)

                is DataState.Loading -> _chargeDialogUiState.value = ChargeDialogUiState.Loading

                is DataState.Success ->
                    _chargeDialogUiState.value =
                        ChargeDialogUiState.AllChargesV2(
                            result.data,
                        )
            }
        }
    }

    fun createCharges(clientId: Int, payload: ChargesPayload) =
        viewModelScope.launch {
            createChargesUseCase(clientId, payload).collect { result ->
                when (result) {
                    is DataState.Error ->
                        _chargeDialogUiState.value =
                            ChargeDialogUiState.Error(Res.string.feature_client_failed_to_create_charge)

                    is DataState.Loading -> _chargeDialogUiState.value = ChargeDialogUiState.Loading

                    is DataState.Success ->
                        _chargeDialogUiState.value =
                            ChargeDialogUiState.ChargesCreatedSuccessfully
                }
            }
        }
}
