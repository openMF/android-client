/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientDetailsProfile

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.account_no
import androidclient.feature.client.generated.resources.activation_date
import androidclient.feature.client.generated.resources.client_classification
import androidclient.feature.client.generated.resources.client_type
import androidclient.feature.client.generated.resources.date_of_birth
import androidclient.feature.client.generated.resources.external_id
import androidclient.feature.client.generated.resources.gender
import androidclient.feature.client.generated.resources.legal_form
import androidclient.feature.client.generated.resources.office
import androidclient.feature.client.generated.resources.staff
import androidclient.feature.client.generated.resources.string_not_available
import androidclient.feature.client.generated.resources.submission_date
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.ClientDetailsRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.domain.useCases.GetClientDetailsUseCase
import com.mifos.core.ui.components.ResultStatus
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.core.ui.util.imageToByteArray
import com.mifos.core.ui.util.toDateString
import com.mifos.feature.client.clientDetailsProfile.components.ClientProfileDetailsActionItem
import com.mifos.room.entities.client.ClientEntity
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString

/**
 * ViewModel responsible for managing client profile UI state, fetching details,
 * profile images, observing network connectivity, and handling user actions.
 *
 * @param savedStateHandle Provides navigation arguments, such as clientId.
 * @param getClientDetailsUseCase Use case to fetch client details from repository.
 * @param clientDetailsRepo Repository to fetch client details and profile image.
 * @param networkMonitor Observes network connectivity status.
 */
internal class ClientProfileDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val getClientDetailsUseCase: GetClientDetailsUseCase,
    private val clientDetailsRepo: ClientDetailsRepository,
    private val networkMonitor: NetworkMonitor,
) : BaseViewModel<ClientProfileDetailsState, ClientProfileDetailsEvent, ClientProfileDetailsAction>(
    initialState = ClientProfileDetailsState(),
) {

    private val route = savedStateHandle.toRoute<ClientProfileDetailsRoute>()

    init {
        getClientAndObserveNetwork()
    }

    private fun getClientAndObserveNetwork() {
        observeNetwork()
        loadClientDetailsAndImage(route.id)
    }

    /**
     * Fetches both client details and profile image.
     *
     * @param clientId ID of the client whose details need to be fetched.
     */
    private fun loadClientDetailsAndImage(clientId: Int) {
        // Fetch client details
        viewModelScope.launch {
            getClientDetailsUseCase(clientId).collect { result ->
                when (result) {
                    is DataState.Success -> {
                        mutableStateFlow.update {
                            it.copy(
                                client = result.data.client,
                                details = buildClientDetails(result.data.client),
                                dialogState = null,
                            )
                        }
                    }

                    is DataState.Error -> {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = ClientProfileDetailsState.DialogState.Error(result.message),
                            )
                        }
                    }

                    DataState.Loading -> {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = ClientProfileDetailsState.DialogState.Loading,
                            )
                        }
                    }
                }
            }
        }

        // Fetch profile image
        viewModelScope.launch {
            clientDetailsRepo.getImage(clientId).collect { result ->
                when (result) {
                    is DataState.Success -> mutableStateFlow.update {
                        it.copy(profileImage = imageToByteArray(result.data))
                    }
                    else -> Unit
                }
            }
        }
    }

    /**
     * Observes the network connectivity status and updates state accordingly.
     */
    private fun observeNetwork() {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { isConnected ->
                mutableStateFlow.update {
                    it.copy(networkConnection = isConnected)
                }
            }
        }
    }

    private suspend fun buildClientDetails(client: ClientEntity?): List<Map<StringResource, String>> {
        if (client == null) return emptyList()

        val personalInfo = mapOf(
            Res.string.gender to getString(Res.string.string_not_available),
            Res.string.date_of_birth to
                (
                    client.dateOfBirth.toDateString().takeIf { it.isNotBlank() }
                        ?: getString(Res.string.string_not_available)
                    ),
        )

        val accountInfo = mapOf(
            Res.string.account_no to (
                client.accountNo?.takeIf { it.isNotBlank() }
                    ?: getString(Res.string.string_not_available)
                ),
            Res.string.office to (
                client.officeName?.takeIf { it.isNotBlank() }
                    ?: getString(Res.string.string_not_available)
                ),
            Res.string.external_id to (
                client.externalId?.takeIf { it.isNotBlank() }
                    ?: getString(Res.string.string_not_available)
                ),
        )

        val otherInfo = mapOf(
            Res.string.legal_form to (
                client.legalForm?.value?.takeIf { it.isNotBlank() }
                    ?: getString(Res.string.string_not_available)
                ),
            Res.string.client_type to getString(Res.string.string_not_available),
            Res.string.client_classification to getString(Res.string.string_not_available),
            Res.string.submission_date to (
                client.timeline?.submittedOnDate?.toDateString()?.takeIf { it.isNotBlank() }
                    ?: getString(Res.string.string_not_available)
                ),
            Res.string.activation_date to (
                client.activationDate.toDateString().takeIf { it.isNotBlank() }
                    ?: getString(Res.string.string_not_available)
                ),
            Res.string.staff to (
                client.staffName?.takeIf { it.isNotBlank() }
                    ?: getString(Res.string.string_not_available)
                ),
        )

        return listOf(
            personalInfo,
            accountInfo,
            otherInfo,
        )
    }

    override fun handleAction(action: ClientProfileDetailsAction) {
        when (action) {
            ClientProfileDetailsAction.NavigateBack -> sendEvent(ClientProfileDetailsEvent.NavigateBack)
            is ClientProfileDetailsAction.OnActionClick -> {
                if (action.action == ClientProfileDetailsActionItem.AssignStaff && state.client?.staffName != null) {
                    mutableStateFlow.update {
                        it.copy(
                            dialogState = ClientProfileDetailsState.DialogState.UnAssignStaff,
                        )
                    }
                } else {
                    sendEvent(ClientProfileDetailsEvent.OnActionClick(action.action))
                }
            }
            ClientProfileDetailsAction.OnRetry -> getClientAndObserveNetwork()
            ClientProfileDetailsAction.OnUpdateDetailsClick -> sendEvent(ClientProfileDetailsEvent.NavigateToUpdateDetails)
            ClientProfileDetailsAction.OnUpdatePhotoClick -> sendEvent(ClientProfileDetailsEvent.NavigateToUpdatePhoto)
            ClientProfileDetailsAction.OnUpdateSignatureClick -> {}
            ClientProfileDetailsAction.ConfirmUnAssignStaff -> {
                viewModelScope.launch {
                    unAssignStaff()
                }
            }
            ClientProfileDetailsAction.DismissDialog -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = null,
                    )
                }
            }

            ClientProfileDetailsAction.OnNext -> getClientAndObserveNetwork()
        }
    }

    suspend fun unAssignStaff() {
        if (state.client == null || state.client?.staffId == null) {
            mutableStateFlow.update {
                it.copy(
                    dialogState = null,
                )
            }
            return
        }
        mutableStateFlow.update {
            it.copy(
                dialogState = ClientProfileDetailsState.DialogState.Loading,
            )
        }
        val result = clientDetailsRepo.unassignStaff(route.id, state.client!!.staffId)
        when {
            result is DataState.Success -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = ClientProfileDetailsState.DialogState
                            .ShowStatusDialog(ResultStatus.SUCCESS),
                    )
                }
            }
            result is DataState.Error -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = ClientProfileDetailsState.DialogState
                            .ShowStatusDialog(ResultStatus.FAILURE, result.message),
                    )
                }
            }
        }
    }
}

/**
 * State holder for the Client Profile screen.
 * Contains all values needed to render the UI and manage logic.
 */
data class ClientProfileDetailsState(
    val profileImage: ByteArray? = null,
    val client: ClientEntity? = null,
    val dialogState: DialogState? = null,
    val details: List<Map<StringResource, String>> = emptyList(),
    val networkConnection: Boolean = false,
) {
    /**
     * Sealed class representing possible dialog states.
     */
    sealed interface DialogState {
        data class Error(val message: String) : DialogState
        data object Loading : DialogState
        data object UnAssignStaff : DialogState
        data class ShowStatusDialog(val status: ResultStatus, val msg: String = "") : DialogState
    }
}

/**
 * One-time UI events for the Client Profile screen.
 */
sealed interface ClientProfileDetailsEvent {
    /** Navigates back to the previous screen */
    data object NavigateBack : ClientProfileDetailsEvent

    /** Triggered when an action item is clicked */
    data class OnActionClick(val action: ClientProfileDetailsActionItem) : ClientProfileDetailsEvent

    data object NavigateToUpdatePhoto : ClientProfileDetailsEvent

    data object NavigateToUpdateDetails : ClientProfileDetailsEvent
}

/**
 * Represents user or system actions for the Client Profile screen.
 */
sealed interface ClientProfileDetailsAction {
    /** Navigate back from the screen */
    data object NavigateBack : ClientProfileDetailsAction

    /** User clicks on an action item */
    data class OnActionClick(val action: ClientProfileDetailsActionItem) : ClientProfileDetailsAction

    /** User clicks on Retry */
    data object OnRetry : ClientProfileDetailsAction

    data object OnUpdatePhotoClick : ClientProfileDetailsAction

    data object OnUpdateSignatureClick : ClientProfileDetailsAction

    data object OnUpdateDetailsClick : ClientProfileDetailsAction

    data object DismissDialog : ClientProfileDetailsAction

    data object ConfirmUnAssignStaff : ClientProfileDetailsAction

    data object OnNext : ClientProfileDetailsAction
}
