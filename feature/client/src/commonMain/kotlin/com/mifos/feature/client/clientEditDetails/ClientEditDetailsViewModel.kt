/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientEditDetails

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.client_details_update_failure_title
import androidclient.feature.client.generated.resources.feature_client_failed_to_fetch_client_template
import androidclient.feature.client.generated.resources.feature_client_failed_to_fetch_offices
import androidclient.feature.client.generated.resources.feature_client_failed_to_fetch_staffs
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.ClientDetailsEditRepository
import com.mifos.core.data.repository.CreateNewClientRepository
import com.mifos.core.domain.useCases.GetClientDetailsUseCase
import com.mifos.core.ui.components.ResultStatus
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.room.entities.client.ClientEntity
import com.mifos.room.entities.client.ClientPayloadEntity
import com.mifos.room.entities.organisation.OfficeEntity
import com.mifos.room.entities.organisation.StaffEntity
import com.mifos.room.entities.templates.clients.ClientsTemplateEntity
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

internal class ClientEditDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: ClientDetailsEditRepository,
    private val newClientRepository: CreateNewClientRepository,
    private val getClientDetailsUseCase: GetClientDetailsUseCase,
) : BaseViewModel<ClientEditDetailsState, ClientEditDetailsEvent, ClientEditDetailsAction>(
    initialState = ClientEditDetailsState(),
) {
    val route = savedStateHandle.toRoute<ClientEditDetailsRoute>()

    init {
        loadClientDetails(route.id)
    }

    fun loadClientDetails(clientId: Int = route.id) {
        viewModelScope.launch {
            getClientDetailsUseCase(clientId).collect { result ->
                when (result) {
                    is DataState.Success -> {
                        mutableStateFlow.update {
                            it.copy(
                                client = result.data.client,
                                dialogState = ClientEditDetailsState.DialogState.ShowUpdateDetailsContent,
                            )
                        }
                    }

                    is DataState.Error -> {}

                    DataState.Loading -> {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = ClientEditDetailsState.DialogState.Loading,
                            )
                        }
                    }
                }
            }
        }
    }

    fun loadOfficeAndClientTemplate() {
        mutableStateFlow.update {
            it.copy(
                dialogState = ClientEditDetailsState.DialogState.Loading,
            )
        }
        loadClientTemplate()
        loadOffices()
    }

    private fun loadClientTemplate() {
        viewModelScope.launch {
            newClientRepository.clientTemplate().catch {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = ClientEditDetailsState.DialogState.Error(getString(Res.string.feature_client_failed_to_fetch_client_template)),
                    )
                }
            }.collect {
                mutableStateFlow.update { currState ->
                    currState.copy(
                        clientsTemplate = it.data ?: ClientsTemplateEntity(),
                    )
                }
            }
        }
    }

    private fun loadOffices() {
        viewModelScope.launch {
            newClientRepository.offices()
                .catch {
                    mutableStateFlow.update {
                        it.copy(
                            dialogState = ClientEditDetailsState.DialogState.Error(getString(Res.string.feature_client_failed_to_fetch_offices)),
                        )
                    }
                }.collect { offices ->
                    mutableStateFlow.update {
                        it.copy(
                            showOffices = offices.data ?: emptyList(),
                        )
                    }
                }
        }
    }

    fun loadStaffInOffices(officeId: Int) {
        viewModelScope.launch {
            newClientRepository.getStaffInOffice(officeId).collect { result ->
                when (result) {
                    is DataState.Error ->
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = ClientEditDetailsState.DialogState.Error(getString(Res.string.feature_client_failed_to_fetch_staffs)),
                            )
                        }

                    DataState.Loading -> Unit
                    is DataState.Success -> {
                        mutableStateFlow.update {
                            it.copy(
                                staffInOffices = result.data,
                            )
                        }
                    }
                }
            }
        }
    }

    fun updateClient(clientPayload: ClientPayloadEntity) {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(
                    dialogState = ClientEditDetailsState.DialogState.Loading,
                )
            }
            try {
                val clientId = repository.updateClient(clientId = route.id, clientPayload = clientPayload)
                mutableStateFlow.update {
                    it.copy(
                        id = clientId ?: -1,
                        dialogState = ClientEditDetailsState.DialogState.ShowStatusDialog(ResultStatus.SUCCESS),
                    )
                }
            } catch (e: Exception) {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = ClientEditDetailsState.DialogState.ShowStatusDialog(
                            ResultStatus.FAILURE,
                            e.message ?: getString(Res.string.client_details_update_failure_title),
                        ),
                    )
                }
            }
        }
    }

    override fun handleAction(action: ClientEditDetailsAction) {
        when (action) {
            ClientEditDetailsAction.NavigateBack -> sendEvent(ClientEditDetailsEvent.NavigateBack)
            ClientEditDetailsAction.OnNext -> sendEvent(ClientEditDetailsEvent.NavigateNext)
        }
    }
}

data class ClientEditDetailsState(
    val id: Int = -1,
    val client: ClientEntity? = null,
    val staffInOffices: List<StaffEntity> = emptyList(),
    val showOffices: List<OfficeEntity> = emptyList(),
    val clientsTemplate: ClientsTemplateEntity = ClientsTemplateEntity(),
    val dialogState: DialogState? = null,
) {
    sealed interface DialogState {
        data class Error(val message: String) : DialogState
        data object Loading : DialogState
        data object ShowUpdateDetailsContent : DialogState
        data class ShowStatusDialog(val status: ResultStatus, val msg: String = "") : DialogState
    }
}

sealed interface ClientEditDetailsEvent {
    data object NavigateBack : ClientEditDetailsEvent
    data object NavigateNext : ClientEditDetailsEvent
    data object OnUpdateSuccess : ClientEditDetailsEvent
}

sealed interface ClientEditDetailsAction {
    data object NavigateBack : ClientEditDetailsAction
    data object OnNext : ClientEditDetailsAction
}
