/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientProfile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.ClientDetailsRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.domain.useCases.GetClientDetailsUseCase
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.core.ui.util.imageToByteArray
import com.mifos.feature.client.clientProfile.components.ClientProfileActionItem
import com.mifos.room.entities.client.ClientEntity
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource

/**
 * ViewModel responsible for managing client profile UI state, fetching details,
 * profile images, observing network connectivity, and handling user actions.
 *
 * @param savedStateHandle Provides navigation arguments, such as clientId.
 * @param getClientDetailsUseCase Use case to fetch client details from repository.
 * @param clientDetailsRepo Repository to fetch client details and profile image.
 * @param networkMonitor Observes network connectivity status.
 */
internal class ClientProfileViewModel(
    savedStateHandle: SavedStateHandle,
    private val getClientDetailsUseCase: GetClientDetailsUseCase,
    private val clientDetailsRepo: ClientDetailsRepository,
    private val networkMonitor: NetworkMonitor,
) : BaseViewModel<ClientProfileState, ClientProfileEvent, ClientProfileAction>(
    initialState = ClientProfileState(),
) {

    private val route = savedStateHandle.toRoute<ClientProfileRoute>()

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
                                dialogState = null,
                            )
                        }
                    }

                    is DataState.Error -> {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = ClientProfileState.DialogState.Error(result.message),
                            )
                        }
                    }

                    DataState.Loading -> {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = ClientProfileState.DialogState.Loading,
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

    override fun handleAction(action: ClientProfileAction) {
        when (action) {
            ClientProfileAction.NavigateBack -> sendEvent(ClientProfileEvent.NavigateBack)

            is ClientProfileAction.OnActionClick -> sendEvent(
                ClientProfileEvent.OnActionClick(
                    action.action,
                ),
            )

            ClientProfileAction.OnRetry -> getClientAndObserveNetwork()

            ClientProfileAction.NavigateToClientDetailsScreen -> sendEvent(
                ClientProfileEvent.NavigateToClientDetailsScreen,
            )
        }
    }
}

/**
 * State holder for the Client Profile screen.
 * Contains all values needed to render the UI and manage logic.
 */
data class ClientProfileState(
    val profileImage: ByteArray? = null,
    val client: ClientEntity? = null,
    val dialogState: DialogState? = null,
    val details: Map<StringResource, String> = emptyMap(),
    val networkConnection: Boolean = false,
) {
    /**
     * Sealed class representing possible dialog states.
     */
    sealed interface DialogState {
        data class Error(val message: String) : DialogState
        data object Loading : DialogState
    }
}

/**
 * One-time UI events for the Client Profile screen.
 */
sealed interface ClientProfileEvent {
    /** Navigates back to the previous screen */
    data object NavigateBack : ClientProfileEvent

    /** Triggered when an action item is clicked */
    data class OnActionClick(val action: ClientProfileActionItem) : ClientProfileEvent

    data object NavigateToClientDetailsScreen : ClientProfileEvent
}

/**
 * Represents user or system actions for the Client Profile screen.
 */
sealed interface ClientProfileAction {
    /** Navigate back from the screen */
    data object NavigateBack : ClientProfileAction

    /** User clicks on an action item */
    data class OnActionClick(val action: ClientProfileActionItem) : ClientProfileAction

    /** User clicks on Retry */
    data object OnRetry : ClientProfileAction

    data object NavigateToClientDetailsScreen : ClientProfileAction
}
