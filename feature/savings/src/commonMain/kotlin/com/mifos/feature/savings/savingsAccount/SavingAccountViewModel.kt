/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.savings.savingsAccount

import androidclient.feature.savings.generated.resources.Res
import androidclient.feature.savings.generated.resources.feature_savings_failed_to_load_savings_products_and_template
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.domain.useCases.CreateSavingsAccountUseCase
import com.mifos.core.domain.useCases.GetClientSavingsAccountTemplateByProductUseCase
import com.mifos.core.domain.useCases.GetGroupSavingsAccountTemplateByProductUseCase
import com.mifos.core.domain.useCases.LoadSavingsAccountsAndTemplateUseCase
import com.mifos.core.model.objects.payloads.SavingsPayload
import com.mifos.feature.savings.navigation.SavingsAccountRoute
import com.mifos.room.entities.templates.savings.SavingProductsTemplate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Created by Aditya Gupta on 08/08/23.
 */
class SavingAccountViewModel(
    private val loadSavingsAccountsAndTemplateUseCase: LoadSavingsAccountsAndTemplateUseCase,
    private val createSavingsAccountUseCase: CreateSavingsAccountUseCase,
    private val getGroupSavingsAccountTemplateByProductUseCase: GetGroupSavingsAccountTemplateByProductUseCase,
    private val getClientSavingsAccountTemplateByProductUseCase: GetClientSavingsAccountTemplateByProductUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val route = savedStateHandle.toRoute<SavingsAccountRoute>()

    private val _savingAccountUiState =
        MutableStateFlow<SavingAccountUiState>(SavingAccountUiState.ShowProgress)
    val savingAccountUiState: StateFlow<SavingAccountUiState> get() = _savingAccountUiState.asStateFlow()

    private val _savingProductsTemplate = MutableStateFlow(SavingProductsTemplate())
    val savingProductsTemplate = _savingProductsTemplate.asStateFlow()

    fun loadSavingsAccountsAndTemplate() =
        viewModelScope.launch {
            loadSavingsAccountsAndTemplateUseCase()
                .collect { dataState ->
                    when (dataState) {
                        is DataState.Error ->
                            _savingAccountUiState.value =
                                SavingAccountUiState.ShowFetchingError(
                                    Res.string.feature_savings_failed_to_load_savings_products_and_template,
                                )

                        DataState.Loading ->
                            _savingAccountUiState.value =
                                SavingAccountUiState.ShowProgress

                        is DataState.Success -> {
                            _savingAccountUiState.value =
                                SavingAccountUiState.LoadAllSavings(dataState.data)
                        }
                    }
                }
        }

    fun loadClientSavingAccountTemplateByProduct(productId: Int) =
        viewModelScope.launch {
            getClientSavingsAccountTemplateByProductUseCase(
                route.clientId,
                productId,
            ).collect { dataState ->
                when (dataState) {
                    is DataState.Error -> {
                        _savingAccountUiState.value =
                            SavingAccountUiState.ShowFetchingError(Res.string.feature_savings_failed_to_load_savings_products_and_template)
                    }

                    DataState.Loading -> Unit

                    is DataState.Success -> {
                        _savingProductsTemplate.value =
                            dataState.data ?: SavingProductsTemplate()
                    }
                }
            }
        }

    fun loadGroupSavingAccountTemplateByProduct(productId: Int) =
        viewModelScope.launch {
            getGroupSavingsAccountTemplateByProductUseCase(
                route.groupId,
                productId,
            ).collect { dataState ->
                when (dataState) {
                    is DataState.Error ->
                        _savingAccountUiState.value =
                            SavingAccountUiState.ShowFetchingError(
                                Res.string.feature_savings_failed_to_load_savings_products_and_template,
                            )

                    is DataState.Loading -> Unit

                    is DataState.Success ->
                        _savingProductsTemplate.value =
                            dataState.data ?: SavingProductsTemplate()
                }
            }
        }

    fun createSavingsAccount(savingsPayload: SavingsPayload?) =
        viewModelScope.launch {
            createSavingsAccountUseCase(savingsPayload)
                .collect { dataState ->
                    when (dataState) {
                        is DataState.Error ->
                            _savingAccountUiState.value =
                                SavingAccountUiState.ShowFetchingErrorString(dataState.message.toString())

                        is DataState.Loading ->
                            _savingAccountUiState.value =
                                SavingAccountUiState.ShowProgress

                        is DataState.Success ->
                            _savingAccountUiState.value =
                                SavingAccountUiState.ShowSavingsAccountCreatedSuccessfully(
                                    dataState.data,
                                )
                    }
                }
        }
}
