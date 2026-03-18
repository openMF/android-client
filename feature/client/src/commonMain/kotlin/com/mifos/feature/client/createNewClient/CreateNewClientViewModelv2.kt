package com.mifos.feature.client.createNewClient


import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.feature_client_client_created_successfully
import androidclient.feature.client.generated.resources.feature_client_waiting_for_checker_approval
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.MFErrorParser
import com.mifos.core.data.repository.CreateNewClientRepository
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.room.entities.client.AddressTemplate
import com.mifos.room.entities.client.ClientPayloadEntity
import com.mifos.room.entities.organisation.OfficeEntity
import com.mifos.room.entities.organisation.StaffEntity
import com.mifos.room.entities.templates.clients.ClientsTemplateEntity
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import kotlin.time.Clock

class CreateNewClientViewModelV2(
    private val repository: CreateNewClientRepository,
) : BaseViewModel<
        CreateNewClientState,
        CreateNewClientEvent,
        CreateNewClientAction,
        >(
    initialState = CreateNewClientState(),
) {

    init {
        loadInitialData()
    }

    override fun handleAction(action: CreateNewClientAction) {
        when (action) {

            // -------------------------
            // BASIC DETAILS
            // -------------------------
            is CreateNewClientAction.UpdateFirstName -> {
                updateForm { copy(firstName = action.value) }
            }

            is CreateNewClientAction.UpdateMiddleName -> {
                updateForm { copy(middleName = action.value) }
            }

            is CreateNewClientAction.UpdateLastName -> {
                updateForm { copy(lastName = action.value) }
            }

            is CreateNewClientAction.UpdateMobileNumber -> {
                updateForm { copy(mobileNumber = action.value) }
            }

            is CreateNewClientAction.UpdateExternalId -> {
                updateForm { copy(externalId = action.value) }
            }

            // -------------------------
            // GENDER
            // -------------------------
            is CreateNewClientAction.UpdateGender -> {
                updateForm {
                    copy(
                        gender = action.name,
                        genderId = action.id
                    )
                }
            }

            // -------------------------
            // ADDRESS TYPE
            // -------------------------
            is CreateNewClientAction.UpdateAddressType -> {
                updateForm {
                    copy(
                        selectedAddressType = action.name,
                        selectedAddressTypeId = action.id
                    )
                }
            }

            // -------------------------
            // ADDRESS DETAILS
            // -------------------------
            is CreateNewClientAction.UpdateAddressLine1 -> {
                updateForm { copy(addressLine1 = action.value) }
            }

            is CreateNewClientAction.UpdateAddressLine2 -> {
                updateForm { copy(addressLine2 = action.value) }
            }

            is CreateNewClientAction.UpdateAddressLine3 -> {
                updateForm { copy(addressLine3 = action.value) }
            }

            is CreateNewClientAction.UpdateCity -> {
                updateForm { copy(city = action.value) }
            }

            is CreateNewClientAction.UpdateState -> {
                updateForm {
                    copy(
                        selectedStateName = action.name,
                        selectedStateProvinceId = action.id
                    )
                }
            }

            is CreateNewClientAction.UpdateCountry -> {
                updateForm {
                    copy(
                        selectedCountryName = action.name,
                        selectedCountryId = action.id
                    )
                }
            }

            is CreateNewClientAction.UpdatePostalCode -> {
                updateForm { copy(postalCode = action.value) }
            }

            is CreateNewClientAction.ToggleAddressActive -> {
                updateForm { copy(isAddressActive = action.isActive) }
            }

            // -------------------------
            // CLIENT DETAILS
            // -------------------------
            is CreateNewClientAction.UpdateClientType -> {
                updateForm {
                    copy(
                        clientType = action.name,
                        selectedClientTypeId = action.id
                    )
                }
            }

            is CreateNewClientAction.UpdateClientClassification -> {
                updateForm {
                    copy(
                        clientClassification = action.name,
                        selectedClientClassificationId = action.id
                    )
                }
            }

            // -------------------------
            // OFFICE & STAFF
            // -------------------------
            is CreateNewClientAction.UpdateOffice -> {
                updateForm {
                    copy(
                        selectedOffice = action.name,
                        selectedOfficeId = action.id
                    )
                }
            }

            is CreateNewClientAction.UpdateStaff -> {
                updateForm {
                    copy(
                        staff = action.name,
                        selectedStaffId = action.id
                    )
                }
            }

            // -------------------------
            // STATUS
            // -------------------------
            is CreateNewClientAction.ToggleClientActive -> {
                updateForm { copy(isActive = action.isActive) }
            }

            // -------------------------
            // DATES
            // -------------------------
            is CreateNewClientAction.UpdateDateOfBirth -> {
                updateForm { copy(dateOfBirth = action.value) }
            }

            is CreateNewClientAction.UpdateActivationDate -> {
                updateForm { copy(activationDate = action.value) }
            }

            // -------------------------
            // DATE PICKERS
            // -------------------------
            is CreateNewClientAction.ToggleDateOfBirthPicker -> {
                updateForm { copy(showDateOfBirthDatepicker = action.show) }
            }

            is CreateNewClientAction.ToggleActivationDatePicker -> {
                updateForm { copy(showActivateDatepicker = action.show) }
            }

            // -------------------------
            // IMAGE
            // -------------------------
            is CreateNewClientAction.ToggleImagePickerDialog -> {
                updateForm { copy(showImagePickerDialog = action.show) }
            }

            is CreateNewClientAction.UpdateSelectedImagePath -> {
                updateForm { copy(selectedImagePath = action.path) }
            }

            is CreateNewClientAction.UpdateSelectedImageFile -> {
                updateForm { copy(selectedImage = action.file) }
            }

            // -------------------------
            // EXISTING
            // -------------------------
            is CreateNewClientAction.Retry -> loadInitialData()

            is CreateNewClientAction.LoadStaffInOffices -> {
                loadStaffInOffices(action.officeId)
            }

            is CreateNewClientAction.CreateClient -> {
                createClient(action.clientPayload)
            }
        }
    }


    private fun loadInitialData() {
        viewModelScope.launch {
            combine(
                repository.clientTemplate(),
                repository.offices(),
            ) { clientResult, officeResult ->
                clientResult to officeResult
            }.collect { (clientResult, officeResult) ->

                if (clientResult is DataState.Loading || officeResult is DataState.Loading) {
                    mutableStateFlow.update {
                        it.copy(
                            screenState = CreateNewClientState.ScreenState.Loading,
                        )
                    }
                    return@collect
                }

                if (clientResult is DataState.Error) {
                    mutableStateFlow.update {
                        it.copy(
                            screenState = CreateNewClientState.ScreenState.Error(clientResult.message),
                        )
                    }
                    return@collect
                }

                if (officeResult is DataState.Error) {
                    mutableStateFlow.update {
                        it.copy(
                            screenState = CreateNewClientState.ScreenState.Error(officeResult.message),
                        )
                    }
                    return@collect
                }


                if (clientResult is DataState.Success && officeResult is DataState.Success) {

                    loadAddressConfiguration()

                    mutableStateFlow.update {
                        it.copy(
                            clientsTemplate = clientResult.data,
                            officeOptions = officeResult.data,
                            screenState = CreateNewClientState.ScreenState.Success,
                        )
                    }
                }
            }
        }
    }

    suspend fun loadAddressConfiguration() {
        try {
            val addressConfig = repository.getAddressConfiguration()
            mutableStateFlow.update {
                it.copy(
                    isAddressEnabled = addressConfig.enabled,
                )
            }

            if (addressConfig.enabled) {
                loadAddressTemplate()
            }
        } catch (e: Exception) {
            val err = MFErrorParser.errorMessage(e)
            sendEvent(CreateNewClientEvent.ShowSnackBar(err))
        }
    }

    suspend fun loadAddressTemplate() {
        try {
            val template = repository.getAddressTemplate()
            mutableStateFlow.update {
                it.copy(
                    addressTemplate = template,
                )
            }
        } catch (e: Exception) {
            val err = MFErrorParser.errorMessage(e)
            sendEvent(CreateNewClientEvent.ShowSnackBar(err))
        }
    }

    fun loadStaffInOffices(officeId: Int) {
        viewModelScope.launch {
            repository.getStaffInOffice(officeId).collect { result ->
                when (result) {
                    is DataState.Error -> {
                        val err = MFErrorParser.errorMessage(result.exception)
                        sendEvent(CreateNewClientEvent.ShowSnackBar(err))
                    }

                    DataState.Loading -> {}
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

    private inline fun updateForm(
        crossinline update: CreateNewClientState.ClientFormState.() -> CreateNewClientState.ClientFormState
    ) {
        mutableStateFlow.update { currentState ->
            currentState.copy(
                formState = currentState.formState.update()
            )
        }
    }

    fun createClient(clientPayload: ClientPayloadEntity) {
        viewModelScope.launch {
            try {
                val clientId = repository.createClient(clientPayload)

                clientId?.let {
                    sendEvent(CreateNewClientEvent.ShowSnackBar(getString(Res.string.feature_client_client_created_successfully)))
                    delay(1000)
                    sendEvent(CreateNewClientEvent.NavigateToClientDetails(it))
                } ?: run {
                    sendEvent(CreateNewClientEvent.ShowSnackBar(getString(Res.string.feature_client_waiting_for_checker_approval)))
                }
            } catch (e: Exception) {
                val err = MFErrorParser.errorMessage(e)
                sendEvent(CreateNewClientEvent.ShowSnackBar(err))
            }
        }
    }
}

data class CreateNewClientState(
    val screenState: ScreenState = ScreenState.Loading,
    val officeOptions: List<OfficeEntity> = emptyList(),
    val isAddressEnabled: Boolean = false,
    val addressTemplate: AddressTemplate? = null,
    val clientsTemplate: ClientsTemplateEntity? = null,
    val staffInOffices: List<StaffEntity> = emptyList(),

    val formState: ClientFormState = ClientFormState()
) {
    sealed interface ScreenState {
        object Loading : ScreenState
        object Success : ScreenState
        class Error(val message: String) : ScreenState
    }

    data class ClientFormState(
        val firstName: String = "",
        val middleName: String = "",
        val lastName: String = "",
        val mobileNumber: String = "",
        val externalId: String = "",
        val gender: String = "",
        val genderId: Int = 0,

        val selectedAddressType: String = "",
        val selectedAddressTypeId: Int = 0,
        val addressLine1: String = "",
        val addressLine2: String = "",
        val addressLine3: String = "",
        val city: String = "",
        val selectedStateName: String = "",
        val selectedStateProvinceId: Int = 0,
        val selectedCountryName: String = "",
        val selectedCountryId: Int = 0,
        val postalCode: String = "",
        val isAddressActive: Boolean = false,

        val clientType: String = "",
        val selectedClientTypeId: Int = 0,
        val clientClassification: String = "",
        val selectedClientClassificationId: Int = 0,
        val selectedOffice: String = "",
        val selectedOfficeId: Int? = 0,
        val staff: String = "",
        val selectedStaffId: Int? = 0,

        val isActive: Boolean = false,
        val dateOfBirth: Long? = null,

        val activationDate: Long = Clock.System.now().toEpochMilliseconds(),

        val showDateOfBirthDatepicker: Boolean = false,
        val showActivateDatepicker: Boolean = false,
        val showImagePickerDialog: Boolean = false,
        val selectedImage: PlatformFile? = null,
        val selectedImagePath: String? = null,
    )
}

sealed interface CreateNewClientAction {

    // -------------------------
    // GENERAL
    // -------------------------
    object Retry : CreateNewClientAction
    class CreateClient(val clientPayload: ClientPayloadEntity) : CreateNewClientAction
    data class LoadStaffInOffices(val officeId: Int) : CreateNewClientAction

    // -------------------------
    // BASIC DETAILS
    // -------------------------
    data class UpdateFirstName(val value: String) : CreateNewClientAction
    data class UpdateMiddleName(val value: String) : CreateNewClientAction
    data class UpdateLastName(val value: String) : CreateNewClientAction
    data class UpdateMobileNumber(val value: String) : CreateNewClientAction
    data class UpdateExternalId(val value: String) : CreateNewClientAction

    // -------------------------
    // GENDER
    // -------------------------
    data class UpdateGender(val name: String, val id: Int) : CreateNewClientAction

    // -------------------------
    // ADDRESS TYPE
    // -------------------------
    data class UpdateAddressType(val name: String, val id: Int) : CreateNewClientAction

    // -------------------------
    // ADDRESS DETAILS
    // -------------------------
    data class UpdateAddressLine1(val value: String) : CreateNewClientAction
    data class UpdateAddressLine2(val value: String) : CreateNewClientAction
    data class UpdateAddressLine3(val value: String) : CreateNewClientAction
    data class UpdateCity(val value: String) : CreateNewClientAction
    data class UpdateState(val name: String, val id: Int) : CreateNewClientAction
    data class UpdateCountry(val name: String, val id: Int) : CreateNewClientAction
    data class UpdatePostalCode(val value: String) : CreateNewClientAction
    data class ToggleAddressActive(val isActive: Boolean) : CreateNewClientAction

    // -------------------------
    // CLIENT DETAILS
    // -------------------------
    data class UpdateClientType(val name: String, val id: Int) : CreateNewClientAction
    data class UpdateClientClassification(val name: String, val id: Int) : CreateNewClientAction

    // -------------------------
    // OFFICE & STAFF
    // -------------------------
    data class UpdateOffice(val name: String, val id: Int?) : CreateNewClientAction
    data class UpdateStaff(val name: String, val id: Int?) : CreateNewClientAction

    // -------------------------
    // STATUS
    // -------------------------
    data class ToggleClientActive(val isActive: Boolean) : CreateNewClientAction

    // -------------------------
    // DATES
    // -------------------------
    data class UpdateDateOfBirth(val value: Long?) : CreateNewClientAction
    data class UpdateActivationDate(val value: Long) : CreateNewClientAction

    // -------------------------
    // DATE PICKERS
    // -------------------------
    data class ToggleDateOfBirthPicker(val show: Boolean) : CreateNewClientAction
    data class ToggleActivationDatePicker(val show: Boolean) : CreateNewClientAction

    // -------------------------
    // IMAGE
    // -------------------------
    data class ToggleImagePickerDialog(val show: Boolean) : CreateNewClientAction
    data class UpdateSelectedImagePath(val path: String?) : CreateNewClientAction
    data class UpdateSelectedImageFile(val file: PlatformFile?) : CreateNewClientAction
}

sealed interface CreateNewClientEvent {
    object NavigateBack : CreateNewClientEvent
    class NavigateToClientDetails(val clientId: Int) : CreateNewClientEvent
    class ShowSnackBar(val message: String) : CreateNewClientEvent
}
