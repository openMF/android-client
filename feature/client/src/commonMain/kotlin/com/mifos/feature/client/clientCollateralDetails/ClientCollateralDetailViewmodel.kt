/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientCollateralDetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.ClientDetailsRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.network.model.CollateralItemResult
import com.mifos.core.ui.util.BaseViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class ClientCollateralDetailViewmodel(
    savedStateHandle: SavedStateHandle,
    private val repo: ClientDetailsRepository,
    private val networkMonitor: NetworkMonitor,
) : BaseViewModel<ClientCollateralDetailsState, ClientCollateralDetailsEvent, ClientCollateralDetailsAction>(
    initialState = run {
        ClientCollateralDetailsState(savedStateHandle.toRoute<ClientCollateralDetailRoute>().clientId)
    },
) {

    init {
        getCollaterals()
    }

    private fun getCollaterals() = viewModelScope.launch {
        val isNetworkAvailable = networkMonitor.isOnline.first()
        if (isNetworkAvailable) {
            when (val response = repo.getClientCollaterals(clientId = state.id)) {
                is DataState.Error -> {
                    mutableStateFlow.update {
                        it.copy(
                            state = ClientCollateralDetailsState.State.Error(
                                isNetworkAvailable,
                                response.message,
                            ),
                        )
                    }
                }

                DataState.Loading -> {
                    mutableStateFlow.update {
                        it.copy(
                            state = ClientCollateralDetailsState.State.Loading,
                        )
                    }
                }

                is DataState.Success -> {
                    if (response.data.isEmpty()) {
                        mutableStateFlow.update {
                            it.copy(
                                state = ClientCollateralDetailsState.State.Empty,
                            )
                        }
                    } else {
                        mutableStateFlow.update {
                            it.copy(
                                collaterals = response.data,
                                state = ClientCollateralDetailsState.State.Success,
                            )
                        }
                    }
                }
            }
        } else {
            mutableStateFlow.update {
                it.copy(
                    state = ClientCollateralDetailsState.State.Error(
                        isNetworkAvailable,
                        "",
                    ),
                )
            }
        }
    }

    override fun handleAction(action: ClientCollateralDetailsAction) {
        when (action) {
            ClientCollateralDetailsAction.OnRetry -> getCollaterals()
        }
    }
}

data class ClientCollateralDetailsState(
    val id: Int,
    val collaterals: List<CollateralItemResult> = emptyList(),
    val networkConnection: Boolean = false,
    val state: State = State.Loading,
) {
    sealed interface State {
        data class Error(val isNetworkAvailable: Boolean, val message: String) : State
        data object Loading : State
        data object Success : State
        data object Empty : State
    }
}

sealed interface ClientCollateralDetailsEvent

sealed interface ClientCollateralDetailsAction {
    data object OnRetry : ClientCollateralDetailsAction
}
