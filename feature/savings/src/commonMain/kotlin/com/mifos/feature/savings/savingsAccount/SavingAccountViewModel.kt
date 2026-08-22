/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.savings.savingsAccount

import kpt.feature.savings.generated.resources.Res
import kpt.feature.savings.generated.resources.feature_savings_failed_to_load_savings_products_and_template
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.domain.useCases.CreateSavingsAccountUseCase
import com.mifos.core.domain.useCases.GetClientSavingsAccountTemplateByProductUseCase
import com.mifos.core.domain.useCases.GetGroupSavingsAccountTemplateByProductUseCase
import com.mifos.core.domain.useCases.LoadSavingsAccountsAndTemplateUseCase
import com.mifos.core.model.objects.payloads.SavingsPayload
import com.mifos.feature.savings.navigation.SavingsAccountRoute
import com.mifos.room.entities.client.Savings
import com.mifos.room.entities.templates.savings.SavingProductsTemplate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
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

    init {
        loadSavingsAccountsAndTemplate()
    }

    fun loadSavingsAccountsAndTemplate() =
        viewModelScope.launch {
            _savingAccountUiState.value = SavingAccountUiState.ShowProgress
            loadSavingsAccountsAndTemplateUseCase()
                .catch {
                    _savingAccountUiState.value =
                        SavingAccountUiState.ShowFetchingError(
                            Res.string.feature_savings_failed_to_load_savings_products_and_template,
                        )
                }
                .collect { savingProductsAndTemplate ->
                    _savingAccountUiState.value =
                        SavingAccountUiState.LoadAllSavings(savingProductsAndTemplate)
                }
        }

    fun loadClientSavingAccountTemplateByProduct(productId: Int) =
        viewModelScope.launch {
            getClientSavingsAccountTemplateByProductUseCase(
                route.clientId,
                productId,
            ).catch {
                _savingAccountUiState.value =
                    SavingAccountUiState.ShowFetchingError(Res.string.feature_savings_failed_to_load_savings_products_and_template)
            }.collect { template ->
                _savingProductsTemplate.value =
                    template ?: SavingProductsTemplate()
            }
        }

    fun loadGroupSavingAccountTemplateByProduct(productId: Int) =
        viewModelScope.launch {
            getGroupSavingsAccountTemplateByProductUseCase(
                route.groupId,
                productId,
            ).catch {
                _savingAccountUiState.value =
                    SavingAccountUiState.ShowFetchingError(
                        Res.string.feature_savings_failed_to_load_savings_products_and_template,
                    )
            }.collect { template ->
                _savingProductsTemplate.value =
                    template ?: SavingProductsTemplate()
            }
        }

    fun createSavingsAccount(savingsPayload: SavingsPayload?) =
        viewModelScope.launch {
            _savingAccountUiState.value = SavingAccountUiState.ShowProgress
            createSavingsAccountUseCase(savingsPayload)
                .catch { error ->
                    _savingAccountUiState.value =
                        SavingAccountUiState.ShowFetchingErrorString(error.message.toString())
                }
                .collect {
                    _savingAccountUiState.value =
                        SavingAccountUiState.ShowSavingsAccountCreatedSuccessfully(
                            Savings(),
                        )
                }
        }
}
