package com.mifos.feature.client.clientEditDetails

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.feature_client_failed_to_fetch_client_template
import androidclient.feature.client.generated.resources.feature_client_failed_to_fetch_offices
import androidclient.feature.client.generated.resources.feature_client_failed_to_fetch_staffs
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.MFErrorParser
import com.mifos.core.data.repository.ClientDetailsEditRepository
import com.mifos.core.data.repository.CreateNewClientRepository
import com.mifos.core.domain.useCases.GetClientDetailsUseCase
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.feature.client.clientEditDetails.ClientEditDetailsViewModel.ClientEditDetailsEvent
import com.mifos.feature.client.clientEditDetails.ClientEditDetailsViewModel.ClientEditDetailsState
import com.mifos.feature.client.clientEditDetails.ClientEditDetailsViewModel.ClientEditDetailsAction
import com.mifos.room.entities.client.ClientEntity
import com.mifos.room.entities.client.ClientPayloadEntity
import com.mifos.room.entities.organisation.OfficeEntity
import com.mifos.room.entities.organisation.StaffEntity
import com.mifos.room.entities.templates.clients.ClientsTemplateEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource

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

    private val _editClientDetailsUiState =
        MutableStateFlow<EditClientDetailsUiState>(EditClientDetailsUiState.ShowProgressbar)
    val editClientDetailsUiState: StateFlow<EditClientDetailsUiState> get() = _editClientDetailsUiState

    private val _staffInOffices = MutableStateFlow<List<StaffEntity>>(emptyList())
    val staffInOffices: StateFlow<List<StaffEntity>> get() = _staffInOffices

    private val _showOffices = MutableStateFlow<List<OfficeEntity>>(emptyList())
    val showOffices: StateFlow<List<OfficeEntity>> get() = _showOffices

    fun loadClientDetails(clientId: Int = route.id) {
        viewModelScope.launch {
            getClientDetailsUseCase(clientId).collect { result ->
                when (result) {
                    is DataState.Success -> {
                        mutableStateFlow.update {
                            it.copy(
                                client = result.data.client,
                            )
                        }
                    }

                    is DataState.Error -> {}

                    DataState.Loading -> {}
                }
            }
        }
    }

    fun loadOfficeAndClientTemplate() {
        _editClientDetailsUiState.value = EditClientDetailsUiState.ShowProgressbar
        loadClientTemplate()
        loadOffices()
    }

    private fun loadClientTemplate() {
        viewModelScope.launch {
            newClientRepository.clientTemplate().catch {
                _editClientDetailsUiState.value =
                    EditClientDetailsUiState.ShowError(Res.string.feature_client_failed_to_fetch_client_template)
            }.collect {
                _editClientDetailsUiState.value =
                    EditClientDetailsUiState.ShowClientTemplate(
                        clientsTemplate = it.data ?: ClientsTemplateEntity(),
                    )
            }
        }
    }

    private fun loadOffices() {
        viewModelScope.launch {
            newClientRepository.offices()
                .catch {
                    _editClientDetailsUiState.value =
                        EditClientDetailsUiState.ShowError(Res.string.feature_client_failed_to_fetch_offices)
                }.collect { offices ->
                    _showOffices.value = offices.data ?: emptyList()
                }
        }
    }

    fun loadStaffInOffices(officeId: Int) {
        viewModelScope.launch {
            newClientRepository.getStaffInOffice(officeId).collect { result ->
                when (result) {
                    is DataState.Error ->
                        _editClientDetailsUiState.value =
                            EditClientDetailsUiState.ShowError(Res.string.feature_client_failed_to_fetch_staffs)

                    DataState.Loading -> Unit
                    is DataState.Success -> _staffInOffices.value = result.data
                }
            }
        }
    }

    fun updateClient(clientPayload: ClientPayloadEntity) {
        viewModelScope.launch {
            _editClientDetailsUiState.value = EditClientDetailsUiState.ShowProgressbar

            try {
                val clientId = repository.updateClient(clientId = route.id, clientPayload = clientPayload)

                clientId?.let {
                    _editClientDetailsUiState.value = EditClientDetailsUiState.SetClientId(it)
                }
            } catch (e: Exception) {
                MFErrorParser.errorMessage(e)
            }
        }
    }

    override fun handleAction(action: ClientEditDetailsAction) {
        when (action) {
            ClientEditDetailsAction.NavigateBack -> sendEvent(ClientEditDetailsEvent.NavigateBack)
        }
    }

    data class ClientEditDetailsState(
        val client: ClientEntity? = null,
    )

    sealed interface ClientEditDetailsEvent {
        data object NavigateBack : ClientEditDetailsEvent
        data object OnSaveSuccess : ClientEditDetailsEvent
    }

    sealed interface ClientEditDetailsAction {
        data object NavigateBack : ClientEditDetailsAction
    }
}

sealed class EditClientDetailsUiState {

    data object ShowProgressbar : EditClientDetailsUiState()

    data class SetClientId(val id: Int) : EditClientDetailsUiState()

    data class ShowClientTemplate(
        val clientsTemplate: ClientsTemplateEntity,
    ) : EditClientDetailsUiState()

    data class ShowError(val message: StringResource) : EditClientDetailsUiState()

}
