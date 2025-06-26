/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanChargeDialog

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_charge_failed_to_load_charge
import androidclient.feature.loan.generated.resources.feature_loan_failed_to_create_loan_charge
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.DataState
import com.mifos.core.domain.useCases.CreateLoanChargesUseCase
import com.mifos.core.domain.useCases.GetAllChargesV3UseCase
import com.mifos.core.model.objects.payloads.ChargesPayload
import com.mifos.room.entities.client.ChargesEntity
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.utils.io.InternalAPI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class LoanChargeDialogViewModel(
    private val getAllChargesV3UseCase: GetAllChargesV3UseCase,
    private val createLoanChargesUseCase: CreateLoanChargesUseCase,
) : ViewModel() {

    private val _loanChargeDialogUiState =
        MutableStateFlow<LoanChargeDialogUiState>(LoanChargeDialogUiState.Loading)
    val loanChargeDialogUiState = _loanChargeDialogUiState.asStateFlow()

    fun loanAllChargesV3(loanId: Int) = viewModelScope.launch {
        getAllChargesV3UseCase(loanId).collect { result ->
            when (result) {
                is DataState.Error ->
                    _loanChargeDialogUiState.value =
                        LoanChargeDialogUiState.Error(Res.string.feature_loan_charge_failed_to_load_charge)

                is DataState.Loading ->
                    _loanChargeDialogUiState.value =
                        LoanChargeDialogUiState.Loading

                is DataState.Success -> {
                    mapResourceBodyToChargeList(result.data)
                }
            }
        }
    }

    fun createLoanCharges(loanId: Int, chargesPayload: ChargesPayload) =
        viewModelScope.launch {
            createLoanChargesUseCase(loanId, chargesPayload).collect { result ->
                when (result) {
                    is DataState.Error ->
                        _loanChargeDialogUiState.value =
                            LoanChargeDialogUiState.Error(Res.string.feature_loan_failed_to_create_loan_charge)

                    is DataState.Loading ->
                        _loanChargeDialogUiState.value =
                            LoanChargeDialogUiState.Loading

                    is DataState.Success ->
                        _loanChargeDialogUiState.value =
                            LoanChargeDialogUiState.LoanChargesCreatedSuccessfully
                }
            }
        }

    @OptIn(InternalAPI::class)
    private suspend fun mapResourceBodyToChargeList(result: HttpResponse) {
        val charges = mutableListOf<ChargesEntity>()

        try {
            val json = Json.parseToJsonElement(result.bodyAsText()).jsonObject

            val chargeOptions = json["chargeOptions"]?.jsonArray
            if (chargeOptions != null) {
                for (item in chargeOptions) {
                    val obj = item.jsonObject
                    val charge = ChargesEntity(
                        id = obj["id"]?.jsonPrimitive?.int ?: 0,
                        name = obj["name"]?.jsonPrimitive?.content ?: "",
                    )
                    charges.add(charge)
                }
            }

            _loanChargeDialogUiState.value = LoanChargeDialogUiState.AllChargesV3(charges)
        } catch (e: Exception) {
            _loanChargeDialogUiState.value =
                LoanChargeDialogUiState.Error(Res.string.feature_loan_charge_failed_to_load_charge)
        }
    }
}
