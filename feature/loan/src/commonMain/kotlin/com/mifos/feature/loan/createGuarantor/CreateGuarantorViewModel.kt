/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.createGuarantor

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_create_guarantor_submit_success
import androidclient.feature.loan.generated.resources.feature_loan_message_field_required
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.data.repository.SearchRepository
import com.mifos.core.domain.useCases.CreateGuarantorUseCase
import com.mifos.core.domain.useCases.GetGuarantorTemplateUseCase
import com.mifos.core.model.entity.accounts.loan.GuarantorClientOption
import com.mifos.core.model.objects.account.loan.CreateGuarantorInput
import com.mifos.core.model.utils.DateConstants
import com.mifos.core.ui.util.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

internal class CreateGuarantorViewModel(
    savedStateHandle: SavedStateHandle,
    private val getGuarantorTemplateUseCase: GetGuarantorTemplateUseCase,
    private val createGuarantorUseCase: CreateGuarantorUseCase,
    private val searchRepository: SearchRepository,
) : BaseViewModel<CreateGuarantorUiState, CreateGuarantorEffect, CreateGuarantorAction>(
    initialState = CreateGuarantorUiState.Loading,
) {
    private val route = savedStateHandle.toRoute<CreateGuarantorRoute>()
    private var searchClientJob: Job? = null

    init {
        trySendAction(CreateGuarantorAction.Load)
    }

    override fun handleAction(action: CreateGuarantorAction) {
        when (action) {
            CreateGuarantorAction.Load,
            CreateGuarantorAction.Retry,
            -> load()

            is CreateGuarantorAction.ToggleExistingClient -> {
                updateContent {
                    it.copy(
                        existingClient = action.checked,
                        clientQuery = "",
                        selectedClientId = null,
                        searchedClientOptions = it.clientOptions,
                        clientError = null,
                        relationshipError = null,
                        firstNameError = null,
                        lastNameError = null,
                    )
                }
            }

            is CreateGuarantorAction.SelectClient ->
                updateContent {
                    it.copy(
                        selectedClientId = action.clientId,
                        clientQuery = action.label,
                        clientError = null,
                    )
                }

            is CreateGuarantorAction.UpdateClientQuery ->
                updateContent {
                    it.copy(
                        clientQuery = action.value,
                        selectedClientId = null,
                        clientError = null,
                    )
                }.also {
                    searchClients(action.value)
                }

            is CreateGuarantorAction.SelectRelationship ->
                updateContent { it.copy(selectedRelationshipIndex = action.index, relationshipError = null) }

            is CreateGuarantorAction.UpdateFirstName ->
                updateContent { it.copy(firstName = action.value, firstNameError = null) }

            is CreateGuarantorAction.UpdateLastName ->
                updateContent { it.copy(lastName = action.value, lastNameError = null) }

            is CreateGuarantorAction.UpdateDateOfBirth ->
                updateContent { it.copy(dateOfBirthMillis = action.millis) }

            is CreateGuarantorAction.UpdateAddressLine1 ->
                updateContent { it.copy(addressLine1 = action.value) }

            is CreateGuarantorAction.UpdateAddressLine2 ->
                updateContent { it.copy(addressLine2 = action.value) }

            is CreateGuarantorAction.UpdateCity ->
                updateContent { it.copy(city = action.value) }

            is CreateGuarantorAction.UpdateZip ->
                updateContent { it.copy(zip = action.value) }

            is CreateGuarantorAction.UpdateMobile ->
                updateContent { it.copy(mobile = action.value) }

            is CreateGuarantorAction.UpdateResidencePhone ->
                updateContent { it.copy(residencePhone = action.value) }

            CreateGuarantorAction.Submit -> submit()
        }
    }

    private fun load() {
        mutableStateFlow.value = CreateGuarantorUiState.Loading
        viewModelScope.launch {
            when (val templateState = getGuarantorTemplateUseCase(route.loanId)) {
                is DataState.Success -> {
                    val template = templateState.data
                    mutableStateFlow.value = CreateGuarantorUiState.Content(
                        loanId = route.loanId,
                        existingClient = template.clientOptions.isNotEmpty(),
                        clientOptions = template.clientOptions,
                        searchedClientOptions = template.clientOptions,
                        relationshipOptions = template.relationshipOptions,
                    )
                }

                is DataState.Error -> {
                    mutableStateFlow.value = CreateGuarantorUiState.Error(templateState.message)
                }

                DataState.Loading -> Unit
            }
        }
    }

    private fun submit() {
        val content = mutableStateFlow.value as? CreateGuarantorUiState.Content ?: return
        if (content.submitInProgress) return
        viewModelScope.launch {
            val requiredMessage = getString(Res.string.feature_loan_message_field_required)
            var nextState = content.copy(
                clientError = null,
                relationshipError = null,
                firstNameError = null,
                lastNameError = null,
            )

            if (content.selectedRelationshipId == null) {
                nextState = nextState.copy(relationshipError = requiredMessage)
            }
            if (content.existingClient) {
                if (content.selectedClientId == null) {
                    nextState = nextState.copy(clientError = requiredMessage)
                }
            } else {
                if (content.firstName.isBlank()) {
                    nextState = nextState.copy(firstNameError = requiredMessage)
                }
                if (content.lastName.isBlank()) {
                    nextState = nextState.copy(lastNameError = requiredMessage)
                }
            }

            if (
                nextState.clientError != null ||
                nextState.relationshipError != null ||
                nextState.firstNameError != null ||
                nextState.lastNameError != null
            ) {
                mutableStateFlow.value = nextState
                return@launch
            }

            mutableStateFlow.value = nextState.copy(submitInProgress = true)
            val request = CreateGuarantorInput(
                existingClientId = nextState.selectedClientId.takeIf { nextState.existingClient },
                clientRelationshipTypeId = nextState.selectedRelationshipId ?: return@launch,
                firstname = nextState.firstName.takeIf { !nextState.existingClient },
                lastname = nextState.lastName.takeIf { !nextState.existingClient },
                dateOfBirth = nextState.dateOfBirthMillis?.let(DateHelper::getDateAsStringFromLong),
                addressLine1 = nextState.addressLine1.takeIf { it.isNotBlank() },
                addressLine2 = nextState.addressLine2.takeIf { it.isNotBlank() },
                city = nextState.city.takeIf { it.isNotBlank() },
                zip = nextState.zip.takeIf { it.isNotBlank() },
                mobileNumber = nextState.mobile.takeIf { it.isNotBlank() },
                housePhoneNumber = nextState.residencePhone.takeIf { it.isNotBlank() },
                dateFormat = DateHelper.SHORT_MONTH,
                locale = DateConstants.LOCALE,
            )

            when (val result = createGuarantorUseCase(route.loanId, request)) {
                is DataState.Success -> {
                    updateContent { it.copy(submitInProgress = false) }
                    sendEvent(
                        CreateGuarantorEffect.ShowMessage(
                            getString(Res.string.feature_loan_create_guarantor_submit_success),
                        ),
                    )
                    sendEvent(CreateGuarantorEffect.NavigateBack)
                }

                is DataState.Error -> {
                    updateContent { it.copy(submitInProgress = false) }
                    sendEvent(CreateGuarantorEffect.ShowMessage(result.message))
                }

                DataState.Loading -> Unit
            }
        }
    }

    private fun updateContent(
        block: (CreateGuarantorUiState.Content) -> CreateGuarantorUiState.Content,
    ) {
        val current = mutableStateFlow.value as? CreateGuarantorUiState.Content ?: return
        mutableStateFlow.value = block(current)
    }

    private fun searchClients(query: String) {
        val state = mutableStateFlow.value as? CreateGuarantorUiState.Content ?: return
        if (!state.existingClient) return

        searchClientJob?.cancel()

        if (query.isBlank()) {
            updateContent { it.copy(searchedClientOptions = it.clientOptions) }
            return
        }

        searchClientJob = viewModelScope.launch {
            searchRepository.searchResources(
                query = query,
                resources = "clients",
                exactMatch = false,
            ).collect { dataState ->
                when (dataState) {
                    is DataState.Success -> {
                        val clients = dataState.data.map { searchedEntity ->
                            GuarantorClientOption(
                                id = searchedEntity.entityId,
                                displayName = searchedEntity.entityName.orEmpty().ifBlank { searchedEntity.description },
                            )
                        }
                        updateContent { it.copy(searchedClientOptions = clients) }
                    }

                    is DataState.Error -> {
                        updateContent { it.copy(searchedClientOptions = emptyList()) }
                    }

                    DataState.Loading -> Unit
                }
            }
        }
    }
}
