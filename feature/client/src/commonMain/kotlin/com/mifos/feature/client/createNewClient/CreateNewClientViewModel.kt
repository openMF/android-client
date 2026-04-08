/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.createNewClient

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.feature_client_error_address_type_is_required
import androidclient.feature.client.generated.resources.feature_client_error_first_name_can_not_be_empty
import androidclient.feature.client.generated.resources.feature_client_error_first_name_should_contain_only_alphabets
import androidclient.feature.client.generated.resources.feature_client_error_last_name_can_not_be_empty
import androidclient.feature.client.generated.resources.feature_client_error_last_name_should_contain_only_alphabets
import androidclient.feature.client.generated.resources.feature_client_error_middle_name_should_contain_only_alphabets
import androidclient.feature.client.generated.resources.feature_client_waiting_for_checker_approval
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.ApiDateFormatter
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.MFErrorParser
import com.mifos.core.common.utils.combineDataState
import com.mifos.core.common.utils.formatDate
import com.mifos.core.data.repository.CreateNewClientRepository
import com.mifos.core.model.objects.clients.Address
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.core.ui.util.ImageUtil
import com.mifos.core.ui.util.multipartRequestBody
import com.mifos.feature.client.utils.PhoneNumberUtil
import com.mifos.room.entities.client.AddressTemplate
import com.mifos.room.entities.client.ClientPayloadEntity
import com.mifos.room.entities.noncore.DataTableEntity
import com.mifos.room.entities.organisation.OfficeEntity
import com.mifos.room.entities.organisation.StaffEntity
import com.mifos.room.entities.templates.clients.ClientsTemplateEntity
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.extension
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import kotlin.time.Clock

class CreateNewClientViewModel(
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

            is CreateNewClientAction.UpdateGender -> {
                updateForm {
                    copy(
                        gender = action.name,
                        genderId = action.id,
                    )
                }
            }

            is CreateNewClientAction.UpdateAddressType -> {
                updateForm {
                    copy(
                        selectedAddressType = action.name,
                        selectedAddressTypeId = action.id,
                    )
                }
            }

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
                        selectedStateProvinceId = action.id,
                    )
                }
            }

            is CreateNewClientAction.UpdateCountry -> {
                updateForm {
                    copy(
                        selectedCountryName = action.name,
                        selectedCountryId = action.id,
                    )
                }
            }

            is CreateNewClientAction.UpdatePostalCode -> {
                updateForm { copy(postalCode = action.value) }
            }

            is CreateNewClientAction.ToggleAddressActive -> {
                updateForm { copy(isAddressActive = action.isActive) }
            }

            is CreateNewClientAction.UpdateClientType -> {
                updateForm {
                    copy(
                        clientType = action.name,
                        selectedClientTypeId = action.id,
                    )
                }
            }

            is CreateNewClientAction.UpdateClientClassification -> {
                updateForm {
                    copy(
                        clientClassification = action.name,
                        selectedClientClassificationId = action.id,
                    )
                }
            }

            is CreateNewClientAction.UpdateOffice -> {
                updateForm {
                    copy(
                        selectedOffice = action.name,
                        selectedOfficeId = action.id,
                    )
                }
            }

            is CreateNewClientAction.UpdateStaff -> {
                updateForm {
                    copy(
                        staff = action.name,
                        selectedStaffId = action.id,
                    )
                }
            }

            is CreateNewClientAction.ToggleClientActive -> {
                updateForm { copy(isActive = action.isActive) }
            }

            is CreateNewClientAction.UpdateDateOfBirth -> {
                updateForm { copy(dateOfBirth = action.value) }
            }

            is CreateNewClientAction.UpdateActivationDate -> {
                updateForm { copy(activationDate = action.value) }
            }

            is CreateNewClientAction.ToggleDateOfBirthPicker -> {
                updateForm { copy(showDateOfBirthDatepicker = action.show) }
            }

            is CreateNewClientAction.ToggleActivationDatePicker -> {
                updateForm { copy(showActivateDatepicker = action.show) }
            }

            is CreateNewClientAction.ToggleImagePickerDialog -> {
                updateForm { copy(showImagePickerDialog = action.show) }
            }

            is CreateNewClientAction.UpdateSelectedImageFile -> {
                updateForm { copy(selectedImage = action.file) }
            }

            is CreateNewClientAction.Retry -> loadInitialData()

            is CreateNewClientAction.CreateClient -> {
                submitClient()
            }
            is CreateNewClientAction.LoadStaffInOffices -> {
                loadStaffInOffices(action.id)
            }
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            combineDataState(
                repository.clientTemplate(),
                repository.offices(),
            ) { clientTemplate, offices ->
                clientTemplate to offices
            }.collect { state ->
                when (state) {
                    is DataState.Loading -> mutableStateFlow.update {
                        it.copy(screenState = CreateNewClientState.ScreenState.Loading)
                    }

                    is DataState.Error -> mutableStateFlow.update {
                        it.copy(
                            screenState = CreateNewClientState.ScreenState.Error(
                                state.exception.message ?: "Unknown Error",
                            ),
                        )
                    }

                    is DataState.Success -> {
                        val clientsTemplate = state.data.first
                        val offices = state.data.second

                        loadAddressConfiguration()
                        mutableStateFlow.update {
                            it.copy(
                                clientsTemplate = clientsTemplate,
                                officeOptions = offices,
                                screenState = CreateNewClientState.ScreenState.Success,
                            )
                        }

                        if (offices.isNotEmpty()) {
                            loadStaffInOffices(offices[0].id)
                        }
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
        crossinline update: CreateNewClientState.ClientFormState.() -> CreateNewClientState.ClientFormState,
    ) {
        mutableStateFlow.update { currentState ->
            currentState.copy(
                formState = currentState.formState.update(),
            )
        }
    }

    fun createClient(clientPayload: ClientPayloadEntity) {
        viewModelScope.launch {
            try {
                mutableStateFlow.update {
                    it.copy(
                        showOverLayProgressIndicator = true,
                    )
                }
                val clientId = repository.createClient(clientPayload)

                clientId?.let {
                    uploadImage(it)
                } ?: run {
                    sendEvent(CreateNewClientEvent.ShowSnackBar(getString(Res.string.feature_client_waiting_for_checker_approval)))
                }

                mutableStateFlow.update {
                    it.copy(
                        showOverLayProgressIndicator = false,
                    )
                }
            } catch (e: Exception) {
                val err = MFErrorParser.errorMessage(e)
                sendEvent(CreateNewClientEvent.ShowSnackBar(err))

                mutableStateFlow.update {
                    it.copy(
                        showOverLayProgressIndicator = false,
                    )
                }
            }
        }
    }

    fun uploadImage(id: Int) {
        viewModelScope.launch {
            try {
                val selectedImage = state.formState.selectedImage
                if (selectedImage == null) {
                    delay(500)
                    sendEvent(CreateNewClientEvent.NavigateToClientDetails(id))
                } else {
                    val compressedImage = ImageUtil.compressImage(
                        selectedImage.readBytes(),
                        150f,
                        150f,
                    )

                    val requestFile = multipartRequestBody(
                        file = compressedImage,
                        name = selectedImage.name,
                        extension = selectedImage.extension,
                    )

                    repository.uploadClientImage(id, requestFile)
                    sendEvent(CreateNewClientEvent.NavigateToClientDetails(id))
                }
            } catch (e: Exception) {
                val err = MFErrorParser.errorMessage(e)
                sendEvent(CreateNewClientEvent.ShowSnackBar(err))
            }
        }
    }

    private fun submitClient() {
        viewModelScope.launch {
            val form = state.formState
            val template = state.clientsTemplate ?: return@launch

            if (!validateForm(form, state.isAddressEnabled)) return@launch

            var payload = createClientPayload(
                form = form,
                template = template,
                staffInOffices = state.staffInOffices,
                isAddressEnabled = state.isAddressEnabled,
            )

            if (state.clientsTemplate?.dataTables?.isNotEmpty() ?: false) {
                sendEvent(
                    CreateNewClientEvent.HasDatatables(
                        template.dataTables ?: emptyList(),
                        payload,
                    ),
                )
            } else {
                payload = payload.copy(
                    datatables = null,
                )
                createClient(payload)
            }
        }
    }

    private suspend fun validateForm(
        form: CreateNewClientState.ClientFormState,
        isAddressEnabled: Boolean,
    ): Boolean {
        val errorMessage = when {
            form.firstName.isBlank() ->
                getString(Res.string.feature_client_error_first_name_can_not_be_empty)

            form.firstName.contains("[^a-zA-Z ]".toRegex()) ->
                getString(Res.string.feature_client_error_first_name_should_contain_only_alphabets)

            form.lastName.isBlank() ->
                getString(Res.string.feature_client_error_last_name_can_not_be_empty)

            form.lastName.contains("[^a-zA-Z ]".toRegex()) ->
                getString(Res.string.feature_client_error_last_name_should_contain_only_alphabets)

            form.middleName.isNotBlank() && form.middleName.contains("[^a-zA-Z ]".toRegex()) ->
                getString(Res.string.feature_client_error_middle_name_should_contain_only_alphabets)

            isAddressEnabled && form.selectedAddressTypeId <= 0 ->
                getString(Res.string.feature_client_error_address_type_is_required)

            else -> null
        }

        if (errorMessage != null) {
            sendEvent(CreateNewClientEvent.ShowSnackBar(errorMessage))
            return false
        }

        return true
    }

    private fun createClientPayload(
        form: CreateNewClientState.ClientFormState,
        template: ClientsTemplateEntity,
        staffInOffices: List<StaffEntity>,
        isAddressEnabled: Boolean,
    ): ClientPayloadEntity {
        val formattedActivationDate =
            if (form.isActive) formatDate(form.activationDate) else null

        val formattedDob = form.dateOfBirth?.let { formatDate(it) }

        val hasAnyDate = formattedActivationDate != null || formattedDob != null

        var payload = ClientPayloadEntity(
            firstname = form.firstName,
            lastname = form.lastName,
            officeId = form.selectedOfficeId,
            legalFormId = 1,

            active = form.isActive,
            activationDate = formattedActivationDate,
            dateOfBirth = formattedDob,
            dateFormat = if (hasAnyDate) ApiDateFormatter.DATE_FORMAT else null,
            locale = ApiDateFormatter.LOCALE,
        )

        if (isAddressEnabled) {
            payload = payload.copy(
                address = listOf(
                    Address(
                        addressTypeId = form.selectedAddressTypeId.takeIf { it > 0 },
                        isActive = form.isAddressActive,
                        addressLine1 = form.addressLine1.ifBlank { null },
                        addressLine2 = form.addressLine2.ifBlank { null },
                        addressLine3 = form.addressLine3.ifBlank { null },
                        city = form.city.ifBlank { null },
                        stateProvinceId = form.selectedStateProvinceId.takeIf { it > 0 },
                        countryId = form.selectedCountryId.takeIf { it > 0 },
                        postalCode = form.postalCode.ifBlank { null },
                    ),
                ),
            )
        }

        if (form.middleName.isNotBlank()) {
            payload = payload.copy(middlename = form.middleName)
        }

        if (PhoneNumberUtil.isGlobalPhoneNumber(form.mobileNumber)) {
            payload = payload.copy(mobileNo = form.mobileNumber)
        }

        if (form.externalId.isNotBlank()) {
            payload = payload.copy(externalId = form.externalId)
        }

        if (template.genderOptions?.isNotEmpty() == true && form.genderId > 0) {
            payload = payload.copy(genderId = form.genderId)
        }

        if (staffInOffices.isNotEmpty() && form.selectedStaffId != null && form.selectedStaffId > 0) {
            payload = payload.copy(staffId = form.selectedStaffId)
        }

        if (template.clientTypeOptions?.isNotEmpty() == true && form.selectedClientTypeId > 0) {
            payload = payload.copy(clientTypeId = form.selectedClientTypeId)
        }

        if (template.clientClassificationOptions?.isNotEmpty() == true && form.selectedClientClassificationId > 0) {
            payload = payload.copy(clientClassificationId = form.selectedClientClassificationId)
        }

        return payload
    }
}

data class CreateNewClientState(
    val screenState: ScreenState = ScreenState.Loading,
    val officeOptions: List<OfficeEntity> = emptyList(),
    val isAddressEnabled: Boolean = false,
    val addressTemplate: AddressTemplate? = null,
    val clientsTemplate: ClientsTemplateEntity? = null,
    val staffInOffices: List<StaffEntity> = emptyList(),
    val formState: ClientFormState = ClientFormState(),
    val showOverLayProgressIndicator: Boolean = false,
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
    )
}

sealed interface CreateNewClientAction {

    object Retry : CreateNewClientAction
    object CreateClient : CreateNewClientAction
    data class LoadStaffInOffices(val id: Int) : CreateNewClientAction

    data class UpdateFirstName(val value: String) : CreateNewClientAction
    data class UpdateMiddleName(val value: String) : CreateNewClientAction
    data class UpdateLastName(val value: String) : CreateNewClientAction
    data class UpdateMobileNumber(val value: String) : CreateNewClientAction
    data class UpdateExternalId(val value: String) : CreateNewClientAction

    data class UpdateGender(val name: String, val id: Int) : CreateNewClientAction

    data class UpdateAddressType(val name: String, val id: Int) : CreateNewClientAction

    data class UpdateAddressLine1(val value: String) : CreateNewClientAction
    data class UpdateAddressLine2(val value: String) : CreateNewClientAction
    data class UpdateAddressLine3(val value: String) : CreateNewClientAction
    data class UpdateCity(val value: String) : CreateNewClientAction
    data class UpdateState(val name: String, val id: Int) : CreateNewClientAction
    data class UpdateCountry(val name: String, val id: Int) : CreateNewClientAction
    data class UpdatePostalCode(val value: String) : CreateNewClientAction
    data class ToggleAddressActive(val isActive: Boolean) : CreateNewClientAction

    data class UpdateClientType(val name: String, val id: Int) : CreateNewClientAction
    data class UpdateClientClassification(val name: String, val id: Int) : CreateNewClientAction

    data class UpdateOffice(val name: String, val id: Int?) : CreateNewClientAction
    data class UpdateStaff(val name: String, val id: Int?) : CreateNewClientAction

    data class ToggleClientActive(val isActive: Boolean) : CreateNewClientAction

    data class UpdateDateOfBirth(val value: Long?) : CreateNewClientAction
    data class UpdateActivationDate(val value: Long) : CreateNewClientAction

    data class ToggleDateOfBirthPicker(val show: Boolean) : CreateNewClientAction
    data class ToggleActivationDatePicker(val show: Boolean) : CreateNewClientAction

    data class ToggleImagePickerDialog(val show: Boolean) : CreateNewClientAction
    data class UpdateSelectedImageFile(val file: PlatformFile?) : CreateNewClientAction
}

sealed interface CreateNewClientEvent {
    object NavigateBack : CreateNewClientEvent
    class NavigateToClientDetails(val clientId: Int) : CreateNewClientEvent
    class ShowSnackBar(val message: String) : CreateNewClientEvent
    class HasDatatables(
        val datatables: List<DataTableEntity>,
        val clientPayload: ClientPayloadEntity,
    ) : CreateNewClientEvent
}
