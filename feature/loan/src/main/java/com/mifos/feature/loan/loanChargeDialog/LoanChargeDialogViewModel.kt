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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Resource
import com.mifos.core.payloads.ChargesPayload
import com.mifos.core.domain.useCases.CreateLoanChargesUseCase
import com.mifos.core.domain.useCases.GetAllChargesV3UseCase
import com.mifos.core.objects.client.Charges
import com.mifos.feature.loan.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject

@HiltViewModel
class LoanChargeDialogViewModel @Inject constructor(
    private val getAllChargesV3UseCase: GetAllChargesV3UseCase,
    private val createLoanChargesUseCase: CreateLoanChargesUseCase,
) : ViewModel() {

    private val _loanChargeDialogUiState =
        MutableStateFlow<LoanChargeDialogUiState>(LoanChargeDialogUiState.Loading)
    val loanChargeDialogUiState = _loanChargeDialogUiState.asStateFlow()

    fun loanAllChargesV3(loanId: Int) = viewModelScope.launch(Dispatchers.IO) {
        getAllChargesV3UseCase(loanId).collect { result ->
            when (result) {
                is Resource.Error ->
                    _loanChargeDialogUiState.value =
                        LoanChargeDialogUiState.Error(R.string.feature_loan_charge_failed_to_load_charge)

                is Resource.Loading ->
                    _loanChargeDialogUiState.value =
                        LoanChargeDialogUiState.Loading

                is Resource.Success -> {
//                    result.data?.let {
//                        _loanChargeDialogUiState.value =
//                            LoanChargeDialogUiState.AllChargesV3(it)
//                    }
                    result.data?.let { mapResourceBodyToChargeList(it) }
                }
            }
        }
    }

    fun createLoanCharges(loanId: Int, chargesPayload: ChargesPayload) =
        viewModelScope.launch(Dispatchers.IO) {
            createLoanChargesUseCase(loanId, chargesPayload).collect { result ->
                when (result) {
                    is Resource.Error ->
                        _loanChargeDialogUiState.value =
                            LoanChargeDialogUiState.Error(R.string.feature_loan_failed_to_create_loan_charge)

                    is Resource.Loading ->
                        _loanChargeDialogUiState.value =
                            LoanChargeDialogUiState.Loading

                    is Resource.Success ->
                        _loanChargeDialogUiState.value =
                            LoanChargeDialogUiState.LoanChargesCreatedSuccessfully
                }
            }
        }

    private fun mapResourceBodyToChargeList(result: ResponseBody) {
        val charges: MutableList<Charges> = ArrayList()
        var reader: BufferedReader? = null
        val sb = StringBuilder()
        try {
            reader = BufferedReader(InputStreamReader(result.byteStream()))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                sb.append(line)
            }
            val obj = JSONObject(sb.toString())
            if (obj.has("chargeOptions")) {
                val chargesTypes = obj.getJSONArray("chargeOptions")
                for (i in 0 until chargesTypes.length()) {
                    val chargesObject = chargesTypes.getJSONObject(i)
                    val charge = Charges()
                    charge.id = chargesObject.optInt("id")
                    charge.name = chargesObject.optString("name")
                    charges.add(charge)
                }
            }
            _loanChargeDialogUiState.value = LoanChargeDialogUiState.AllChargesV3(charges)
        } catch (e: Exception) {
            _loanChargeDialogUiState.value =
                LoanChargeDialogUiState.Error(R.string.feature_loan_charge_failed_to_load_charge)
        }
    }
}
