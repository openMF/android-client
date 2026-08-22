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

import kpt.feature.loan.generated.resources.Res
import kpt.feature.loan.generated.resources.feature_loan_create_guarantor_missing_configuration
import kpt.feature.loan.generated.resources.feature_loan_create_guarantor_submit_failure
import kpt.feature.loan.generated.resources.feature_loan_create_guarantor_submit_success
import kpt.feature.loan.generated.resources.feature_loan_get_guarantor_account_template_failure
import kpt.feature.loan.generated.resources.feature_loan_get_guarantor_template_failure
import kpt.feature.loan.generated.resources.feature_loan_load_clients_failure
import kpt.feature.loan.generated.resources.feature_loan_message_field_required
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.data.repository.SearchRepository
import com.mifos.core.domain.useCases.createGuarantor.CreateGuarantorUseCase
import com.mifos.core.domain.useCases.createGuarantor.GetGuarantorAccountTemplateUseCase
import com.mifos.core.domain.useCases.createGuarantor.GetGuarantorTemplateUseCase
import com.mifos.core.model.objects.account.loan.guarantor.CreateGuarantor
import com.mifos.core.model.objects.account.loan.guarantor.CreateGuarantorInput
import com.mifos.core.model.objects.account.loan.guarantor.GuarantorAccountTemplate
import com.mifos.core.model.objects.account.loan.guarantor.GuarantorRelationshipOption
import com.mifos.core.model.objects.account.loan.guarantor.GuarantorTemplate
import kpt.core.base.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource

internal class CreateGuarantorViewModel(
    savedStateHandle: SavedStateHandle,
    private val getGuarantorTemplateUseCase: GetGuarantorTemplateUseCase,
    private val createGuarantorUseCase: CreateGuarantorUseCase,
    private val getGuarantorAccountTemplateUseCase: GetGuarantorAccountTemplateUseCase,
    private val searchRepository: SearchRepository,
) : BaseViewModel<CreateGuarantorState, CreateGuarantorEvent, CreateGuarantorAction>(
    initialState = CreateGuarantorState(
        loanId = savedStateHandle.toRoute<CreateGuarantorRoute>().loanId,
    ),
) {
    private val searchQueryFlow = MutableStateFlow("")

    init {
        trySendAction(CreateGuarantorAction.Load)
        observeClientSearch()
    }

    override fun handleAction(action: CreateGuarantorAction) {
        when (action) {
            CreateGuarantorAction.Load,
            CreateGuarantorAction.Retry,
            -> getGuarantorTemplate()

            is CreateGuarantorAction.ToggleExistingClient -> {
                mutableStateFlow.update { state ->
                    state.copy(
                        existingClient = action.checked,
                        clientSearchQuery = "",
                        selectedClientId = null,
                        searchedClientOptions = emptyList(),
                        clientError = null,
                        relationshipError = null,
                        firstNameError = null,
                        lastNameError = null,
                    )
                }
                searchQueryFlow.value = ""
            }

            is CreateGuarantorAction.SelectClient -> {
                mutableStateFlow.update {
                    it.copy(
                        selectedClientId = action.id,
                        clientSearchQuery = action.label,
                        clientError = null,
                    )
                }
                loadGuarantorAccountTemplate(action.id)
            }

            is CreateGuarantorAction.UpdateClientQuery -> {
                mutableStateFlow.update {
                    it.copy(
                        clientSearchQuery = action.value,
                        selectedClientId = null,
                        clientError = null,
                    )
                }
                searchQueryFlow.value = action.value
            }

            is CreateGuarantorAction.SelectRelationship ->
                mutableStateFlow.update {
                    it.copy(
                        selectedRelationshipIndex = action.index,
                        relationshipError = null,
                    )
                }

            is CreateGuarantorAction.UpdateFirstName ->
                mutableStateFlow.update {
                    it.copy(
                        firstName = action.value,
                        firstNameError = if (action.value.isBlank()) {
                            Res.string.feature_loan_message_field_required
                        } else {
                            null
                        },
                    )
                }

            is CreateGuarantorAction.UpdateLastName ->
                mutableStateFlow.update {
                    it.copy(
                        lastName = action.value,
                        lastNameError = if (action.value.isBlank()) {
                            Res.string.feature_loan_message_field_required
                        } else {
                            null
                        },
                    )
                }

            is CreateGuarantorAction.UpdateDateOfBirth ->
                mutableStateFlow.update { it.copy(dateOfBirthMillis = action.millis) }

            is CreateGuarantorAction.UpdateAddressLine1 ->
                mutableStateFlow.update { it.copy(addressLine1 = action.value) }

            is CreateGuarantorAction.UpdateAddressLine2 ->
                mutableStateFlow.update { it.copy(addressLine2 = action.value) }

            is CreateGuarantorAction.UpdateCity ->
                mutableStateFlow.update { it.copy(city = action.value) }

            is CreateGuarantorAction.UpdateZip ->
                mutableStateFlow.update { it.copy(zip = action.value) }

            is CreateGuarantorAction.UpdateMobile ->
                mutableStateFlow.update { it.copy(mobile = action.value) }

            is CreateGuarantorAction.UpdateResidencePhone ->
                mutableStateFlow.update { it.copy(residencePhone = action.value) }

            CreateGuarantorAction.Submit -> submitGuarantor()

            is CreateGuarantorAction.Internal.ReceiveTemplateResult -> handleGuarantorTemplateResult(
                action.result,
            )

            is CreateGuarantorAction.Internal.ReceiveAccountTemplateResult -> handleGuarantorAccountTemplateResult(
                action.result,
            )

            is CreateGuarantorAction.Internal.ReceiveSubmitResult -> handleSubmitResult(action.result)

            CreateGuarantorAction.NavigateBack -> sendEvent(CreateGuarantorEvent.NavigateBack)
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeClientSearch() {
        viewModelScope.launch {
            searchQueryFlow
                .debounce(500L)
                .distinctUntilChanged()
                .collectLatest { query ->
                    searchClients(query)
                }
        }
    }

    private fun getGuarantorTemplate() {
        viewModelScope.launch {
            mutableStateFlow.update { it.copy(viewState = CreateGuarantorState.ViewState.Loading) }
            val result = getGuarantorTemplateUseCase(state.loanId)
            sendAction(CreateGuarantorAction.Internal.ReceiveTemplateResult(result))
        }
    }

    private fun handleGuarantorTemplateResult(result: DataState<GuarantorTemplate>) {
        when (result) {
            is DataState.Success -> {
                mutableStateFlow.update {
                    it.copy(
                        viewState = CreateGuarantorState.ViewState.Success,
                        guarantorRelationshipOptions = result.data.allowedClientRelationshipTypes,
                        externalGuarantorTypeId = result.data.guarantorTypeOptions.firstOrNull { guarantorType ->
                            guarantorType.code == "guarantorType.external"
                        }?.id,
                        existingGuarantorTypeId = result.data.guarantorTypeOptions.firstOrNull { guarantorType ->
                            guarantorType.code == "guarantorType.existing.client"
                        }?.id,
                    )
                }
            }

            is DataState.Error -> {
                mutableStateFlow.update {
                    it.copy(
                        viewState = CreateGuarantorState.ViewState.Error(Res.string.feature_loan_get_guarantor_template_failure),
                    )
                }
            }

            DataState.Loading -> Unit
        }
    }

    private fun loadGuarantorAccountTemplate(clientId: Int) {
        viewModelScope.launch {
            mutableStateFlow.update { it.copy(fetchingGuarantorAccountTemplate = true) }
            val result = getGuarantorAccountTemplateUseCase(state.loanId, clientId)
            sendAction(CreateGuarantorAction.Internal.ReceiveAccountTemplateResult(result))
        }
    }

    private fun handleGuarantorAccountTemplateResult(result: DataState<GuarantorAccountTemplate>) {
        when (result) {
            is DataState.Success -> {
                val template = result.data
                mutableStateFlow.update {
                    it.copy(
                        fetchingGuarantorAccountTemplate = false,
                    )
                }
            }

            is DataState.Error -> {
                mutableStateFlow.update { it.copy(fetchingGuarantorAccountTemplate = false) }
                sendEvent(
                    CreateGuarantorEvent.ShowMessage(Res.string.feature_loan_get_guarantor_account_template_failure),
                )
            }

            DataState.Loading -> Unit
        }
    }

    private fun submitGuarantor() {
        if (state.submitInProgress) return

        val computedRelationshipId = state
            .guarantorRelationshipOptions
            .getOrNull(state.selectedRelationshipIndex)
            ?.id

        if (!validateInput(computedRelationshipId)) return

        // Safe extraction since validateInput enforces these are non-null
        val relationshipId = computedRelationshipId ?: return
        val guarantorTypeId =
            state.existingGuarantorTypeId ?: state.externalGuarantorTypeId ?: return

        mutableStateFlow.update {
            it.copy(
                clientError = null,
                relationshipError = null,
                firstNameError = null,
                lastNameError = null,
                submitInProgress = true,
            )
        }

        val request = CreateGuarantorInput(
            clientRelationshipTypeId = relationshipId,
            entityId = state.selectedClientId,
            guarantorTypeId = guarantorTypeId,
            firstname = state.firstName.takeIf { !state.existingClient },
            lastname = state.lastName.takeIf { !state.existingClient },
            dob = state.dateOfBirthMillis?.let(DateHelper::getDateAsStringFromLong),
            addressLine1 = state.addressLine1.takeIf { it.isNotBlank() },
            addressLine2 = state.addressLine2.takeIf { it.isNotBlank() },
            city = state.city.takeIf { it.isNotBlank() },
            zip = state.zip.takeIf { it.isNotBlank() },
            mobileNumber = state.mobile.takeIf { it.isNotBlank() },
            housePhoneNumber = state.residencePhone.takeIf { it.isNotBlank() },
        )

        viewModelScope.launch {
            val result = createGuarantorUseCase(state.loanId, request)
            sendAction(CreateGuarantorAction.Internal.ReceiveSubmitResult(result))
        }
    }

    private fun validateInput(computedRelationshipId: Long?): Boolean {
        val relationshipError = if (computedRelationshipId == null) {
            Res.string.feature_loan_message_field_required
        } else {
            null
        }

        val clientError = if (state.existingClient && state.selectedClientId == null) {
            Res.string.feature_loan_message_field_required
        } else {
            null
        }

        val firstNameError = if (!state.existingClient && state.firstName.isBlank()) {
            Res.string.feature_loan_message_field_required
        } else {
            null
        }

        val lastNameError = if (!state.existingClient && state.lastName.isBlank()) {
            Res.string.feature_loan_message_field_required
        } else {
            null
        }

        if (relationshipError != null || clientError != null || firstNameError != null || lastNameError != null) {
            mutableStateFlow.update {
                it.copy(
                    clientError = clientError,
                    relationshipError = relationshipError,
                    firstNameError = firstNameError,
                    lastNameError = lastNameError,
                )
            }
            return false
        }

        if ((state.existingGuarantorTypeId == null && state.existingClient) || state.externalGuarantorTypeId == null) {
            sendEvent(
                CreateGuarantorEvent.ShowMessage(
                    Res.string.feature_loan_create_guarantor_missing_configuration,
                ),
            )
            return false
        }

        return true
    }

    private fun handleSubmitResult(result: DataState<*>) {
        when (result) {
            is DataState.Success -> {
                mutableStateFlow.update {
                    it.copy(
                        submitInProgress = false,
                        isCreateSuccessful = true,
                    )
                }
                sendEvent(
                    CreateGuarantorEvent.ShowMessage(Res.string.feature_loan_create_guarantor_submit_success),
                )
                sendEvent(CreateGuarantorEvent.NavigateBack)
            }

            is DataState.Error -> {
                mutableStateFlow.update { it.copy(submitInProgress = false) }
                sendEvent(CreateGuarantorEvent.ShowMessage(Res.string.feature_loan_create_guarantor_submit_failure))
            }

            DataState.Loading -> Unit
        }
    }

    private suspend fun searchClients(query: String) {
        val state = mutableStateFlow.value
        if (!state.existingClient) return

        if (query.isBlank()) {
            mutableStateFlow.update { it.copy(searchedClientOptions = emptyList()) }
            return
        }

        searchRepository.searchResources(
            query = query,
            resources = "clients",
            exactMatch = false,
        ).collect { dataState ->
            when (dataState) {
                is DataState.Success -> {
                    val clients = dataState.data.map { searchedEntity ->
                        CreateGuarantorState.ClientOption(
                            id = searchedEntity.entityId,
                            name = searchedEntity.entityName ?: "",
                        )
                    }
                    mutableStateFlow.update { it.copy(searchedClientOptions = clients) }
                }

                is DataState.Error -> {
                    mutableStateFlow.update { it.copy(searchedClientOptions = emptyList()) }
                    sendEvent(CreateGuarantorEvent.ShowMessage(Res.string.feature_loan_load_clients_failure))
                }

                DataState.Loading -> Unit
            }
        }
    }
}

data class CreateGuarantorState(
    val loanId: Int,
    val viewState: ViewState = ViewState.Loading,

    val existingClient: Boolean = true,

    val searchedClientOptions: List<ClientOption> = emptyList(),
    val guarantorRelationshipOptions: List<GuarantorRelationshipOption> = emptyList(),

    val clientSearchQuery: String = "",
    val selectedClientId: Int? = null,
    val selectedRelationshipIndex: Int = -1,

    val firstName: String = "",
    val lastName: String = "",
    val dateOfBirthMillis: Long? = null,

    val addressLine1: String = "",
    val addressLine2: String = "",
    val city: String = "",
    val zip: String = "",
    val mobile: String = "",
    val residencePhone: String = "",

    val existingGuarantorTypeId: Long? = null,
    val externalGuarantorTypeId: Long? = null,

    val clientError: StringResource? = null,
    val relationshipError: StringResource? = null,
    val firstNameError: StringResource? = null,
    val lastNameError: StringResource? = null,

    val submitInProgress: Boolean = false,
    val fetchingGuarantorAccountTemplate: Boolean = false,
    val isCreateSuccessful: Boolean = false,
) {
    sealed interface ViewState {
        data object Loading : ViewState
        data object Success : ViewState
        data class Error(val message: StringResource) : ViewState
    }

    data class ClientOption(
        val id: Int,
        val name: String,
    )

    val canSubmit = selectedRelationshipIndex != -1 && if (existingClient) {
        selectedClientId != null
    } else {
        firstName.isNotBlank() && lastName.isNotBlank()
    }
}

internal sealed interface CreateGuarantorEvent {
    data class ShowMessage(val message: StringResource) : CreateGuarantorEvent
    data object NavigateBack : CreateGuarantorEvent
}

internal sealed interface CreateGuarantorAction {
    data object Load : CreateGuarantorAction
    data object Retry : CreateGuarantorAction
    data object NavigateBack : CreateGuarantorAction

    data class ToggleExistingClient(val checked: Boolean) : CreateGuarantorAction
    data class SelectClient(val id: Int, val label: String) : CreateGuarantorAction
    data class UpdateClientQuery(val value: String) : CreateGuarantorAction
    data class SelectRelationship(val index: Int) : CreateGuarantorAction
    data class UpdateFirstName(val value: String) : CreateGuarantorAction
    data class UpdateLastName(val value: String) : CreateGuarantorAction
    data class UpdateDateOfBirth(val millis: Long?) : CreateGuarantorAction
    data class UpdateAddressLine1(val value: String) : CreateGuarantorAction
    data class UpdateAddressLine2(val value: String) : CreateGuarantorAction
    data class UpdateCity(val value: String) : CreateGuarantorAction
    data class UpdateZip(val value: String) : CreateGuarantorAction
    data class UpdateMobile(val value: String) : CreateGuarantorAction
    data class UpdateResidencePhone(val value: String) : CreateGuarantorAction
    data object Submit : CreateGuarantorAction

    sealed interface Internal : CreateGuarantorAction {
        data class ReceiveTemplateResult(
            val result: DataState<GuarantorTemplate>,
        ) : Internal

        data class ReceiveAccountTemplateResult(
            val result: DataState<GuarantorAccountTemplate>,
        ) : Internal

        data class ReceiveSubmitResult(
            val result: DataState<CreateGuarantor>,
        ) : Internal
    }
}
