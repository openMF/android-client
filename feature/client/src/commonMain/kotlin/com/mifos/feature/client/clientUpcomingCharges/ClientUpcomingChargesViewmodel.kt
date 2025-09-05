/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientUpcomingCharges

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.feature_client_error_not_connected_internet
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.PagingData
import com.mifos.core.data.repository.ClientChargeRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.room.entities.client.ChargesEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

class ClientUpcomingChargesViewmodel(
    private val networkMonitor: NetworkMonitor,
    savedStateHandle: SavedStateHandle,
    private val repository: ClientChargeRepository,
) : BaseViewModel<ClientUpcomingChargesState, ClientUpcomingChargesEvent, ClientUpcomingChargesAction>(
    initialState = ClientUpcomingChargesState(),
) {
    private val route = savedStateHandle.toRoute<ClientUpcomingChargesRoute>()

    override fun handleAction(action: ClientUpcomingChargesAction) {
        when (action) {
            is ClientUpcomingChargesAction.CardClicked -> handleCardClick(action.index)
            ClientUpcomingChargesAction.PayOutstandingAmount -> sendEvent(ClientUpcomingChargesEvent.PayOutstandingAmount)

            ClientUpcomingChargesAction.ToggleFilter -> {
                mutableStateFlow.update {
                    it.copy(isFilterOpen = !it.isFilterOpen)
                }
            }

            ClientUpcomingChargesAction.ToggleSearch -> {
                mutableStateFlow.update {
                    it.copy(isSearchBarOpen = !it.isSearchBarOpen)
                }
            }

            ClientUpcomingChargesAction.OnRefresh -> checkNetworkAndGetCharges()

            ClientUpcomingChargesAction.DismissDialog -> mutableStateFlow.update {
                it.copy(
                    dialogState = null,
                )
            }
        }
    }

    init {
        checkNetworkAndGetCharges()
    }

    fun checkNetworkAndGetCharges() {
        viewModelScope.launch {
            val isOnline = networkMonitor.isOnline.first()
            when (isOnline) {
                true -> {
                    mutableStateFlow.update { it.copy(dialogState = null) }
                    getClientCharges()
                }

                false -> {
                    mutableStateFlow.update {
                        it.copy(
                            dialogState = ClientUpcomingChargesState.DialogState.Error(
                                getString(Res.string.feature_client_error_not_connected_internet),
                            ),
                        )
                    }
                }
            }
        }
    }

    private fun getClientCharges() {
        mutableStateFlow.update {
            it.copy(isLoading = true)
        }

        runCatching {
            repository.getClientCharges(route.clientId)
        }.onSuccess { result ->
            mutableStateFlow.update {
                it.copy(
                    chargesFlow = result,
                    dialogState = null,
                    isLoading = false,
                )
            }
        }.onFailure { e ->
            mutableStateFlow.update {
                it.copy(
                    isLoading = false,
                    dialogState = ClientUpcomingChargesState.DialogState.Error(
                        e.message ?: "Unknown Error",
                    ),
                )
            }
        }
    }

    private fun handleCardClick(index: Int) {
        mutableStateFlow.update {
            it.copy(
                expandedItemIndex = index,
                isExpanded = !it.isExpanded,
            )
        }
    }
}

data class ClientUpcomingChargesState(
    val isLoading: Boolean = true,
    val isFilterOpen: Boolean = false,
    val chargesFlow: Flow<PagingData<ChargesEntity>>? = null,
    val isExpanded: Boolean = false,
    val expandedItemIndex: Int = -1,
    val isSearchBarOpen: Boolean = false,
    val dialogState: DialogState? = null,
) {
    sealed interface DialogState {
        data class Error(val message: String) : DialogState
    }
}

sealed interface ClientUpcomingChargesEvent {
    data object PayOutstandingAmount : ClientUpcomingChargesEvent
}

sealed interface ClientUpcomingChargesAction {
    data object PayOutstandingAmount : ClientUpcomingChargesAction
    data object DismissDialog : ClientUpcomingChargesAction
    data object ToggleFilter : ClientUpcomingChargesAction
    data object ToggleSearch : ClientUpcomingChargesAction
    data object OnRefresh : ClientUpcomingChargesAction
    data class CardClicked(val index: Int) : ClientUpcomingChargesAction
}
