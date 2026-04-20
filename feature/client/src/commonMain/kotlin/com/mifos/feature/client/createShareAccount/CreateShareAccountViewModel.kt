/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.client.createShareAccount

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.feature_client_error_network_not_available
import androidclient.feature.client.generated.resources.feature_share_account_created_successfully
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.data.repository.ShareAccountRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.model.objects.payloads.ChargeItem
import com.mifos.core.network.model.share.ChargeOptions
import com.mifos.core.network.model.share.FrequencyTypeOption
import com.mifos.core.network.model.share.ProductOption
import com.mifos.core.network.model.share.SavingsAccountOption
import com.mifos.core.network.model.share.ShareAccountPayload
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.core.ui.util.TextFieldsValidator
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import kotlin.random.Random
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class CreateShareAccountViewModel(
    private val repository: ShareAccountRepository,
    private val networkMonitor: NetworkMonitor,
    val savedStateHandle: SavedStateHandle,

) : BaseViewModel<CreateShareAccountState, CreateShareAccountEvent, CreateShareAccountAction>
    (CreateShareAccountState()) {

    val route = savedStateHandle.toRoute<CreateShareAccountRoute>()

    init {
        loadShareTemplate(route.clientId)
    }

    suspend fun isOnline(
        content: suspend () -> Unit,
    ) {
        if (networkMonitor.isOnline.first()) {
            content()
        } else {
            mutableStateFlow.update {
                it.copy(
                    screenState = CreateShareAccountState.ScreenState.Error(getString(Res.string.feature_client_error_network_not_available)),
                )
            }
        }
    }

    private fun createShareAccount() {
        val shareAccountPayload = ShareAccountPayload(
            clientId = route.clientId,
            productId = state.productOption[state.shareProductIndex!!].id,
            requestedShares = state.totalShares.toInt(),
            externalId = state.externalId,
            submittedDate = state.submissionDate,
            dateFormat = DateHelper.SHORT_MONTH,
            minimumActivePeriod = state.minActivePeriodFreq.toIntOrNull(),
            minimumActivePeriodFrequencyType = if (state.minActivePeriodFreqTypeIdx != null) state.minimumActivePeriodFrequencyTypeOptions[state.minActivePeriodFreqTypeIdx!!].id else null,
            lockinPeriodFrequency = state.lockInPeriodFreq.toIntOrNull(),
            lockinPeriodFrequencyType = if (state.lockInPeriodFreqTypeIdx != null) state.lockInPeriodFrequencyTypeOptions[state.lockInPeriodFreqTypeIdx!!].id else null,
            applicationDate = state.applicationDate,
            allowDividendCalculationForInactiveClients = state.isDividendAllowed,
            charges = state.addedCharges,
            locale = Constants.LOCALE_EN,
            savingsAccountId = state.savingsAccountOptions[state.savingsAccountIdx!!].id,
        )
        viewModelScope.launch {
            isOnline {
                repository.createShareAccount(shareAccountPayload).collect { dataState ->
                    when (dataState) {
                        is DataState.Error -> {
                            if (dataState.exception is IllegalStateException) {
                                mutableStateFlow.update {
                                    it.copy(
                                        dialogState = CreateShareAccountState.DialogState.SuccessResponseStatus(
                                            successStatus = false,
                                            msg = dataState.message,
                                        ),
                                        launchEffectKey = Random.nextInt(),
                                        isOverLayLoadingActive = false,
                                    )
                                }
                            } else {
                                mutableStateFlow.update {
                                    it.copy(
                                        screenState = CreateShareAccountState.ScreenState.Error(
                                            dataState.message,
                                        ),
                                        isOverLayLoadingActive = false,
                                    )
                                }
                            }
                        }

                        DataState.Loading -> {
                            mutableStateFlow.update {
                                it.copy(
                                    isOverLayLoadingActive = true,
                                )
                            }
                        }

                        is DataState.Success -> {
                            mutableStateFlow.update {
                                it.copy(
                                    isOverLayLoadingActive = false,
                                    launchEffectKey = Random.nextInt(),
                                    dialogState = CreateShareAccountState.DialogState.SuccessResponseStatus(
                                        successStatus = true,
                                        msg = getString(Res.string.feature_share_account_created_successfully),
                                    ),
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun loadShareTemplateFromProduct(client: Int, productId: Int?) {
        viewModelScope.launch {
            isOnline {
                repository.getShareTemplate(client, productId).collect { dataState ->
                    when (dataState) {
                        is DataState.Error -> {
                            mutableStateFlow.update {
                                it.copy(
                                    screenState = CreateShareAccountState.ScreenState.Error(
                                        dataState.message,
                                    ),
                                    isOverLayLoadingActive = false,
                                )
                            }
                        }

                        DataState.Loading -> {
                            mutableStateFlow.update {
                                it.copy(
                                    isOverLayLoadingActive = true,
                                )
                            }
                        }

                        is DataState.Success -> {
                            mutableStateFlow.update {
                                it.copy(
                                    screenState = CreateShareAccountState.ScreenState.Success,
                                    currency = dataState.data.currency?.name,
                                    currentPrice = dataState.data.currentMarketPrice?.toString(),
                                    savingsAccountOptions = dataState.data.savingsAccountOptions.orEmpty(),
                                    lockInPeriodFrequencyTypeOptions = dataState.data.lockinPeriodFrequencyTypeOptions.orEmpty(),
                                    minimumActivePeriodFrequencyTypeOptions = dataState.data.minimumActivePeriodFrequencyTypeOptions.orEmpty(),
                                    isOverLayLoadingActive = false,
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun loadShareTemplate(client: Int) {
        viewModelScope.launch {
            isOnline {
                repository.getShareTemplate(client, null).collect { dataState ->
                    when (dataState) {
                        is DataState.Error -> {
                            mutableStateFlow.update {
                                it.copy(
                                    screenState = CreateShareAccountState.ScreenState.Error(
                                        dataState.message,
                                    ),
                                )
                            }
                        }

                        DataState.Loading -> {
                            mutableStateFlow.update {
                                it.copy(
                                    screenState = CreateShareAccountState.ScreenState.Loading,
                                )
                            }
                        }

                        is DataState.Success -> {
                            mutableStateFlow.update {
                                it.copy(
                                    screenState = CreateShareAccountState.ScreenState.Success,
                                    productOption = dataState.data.productOptions,
                                    chargeOptions = dataState.data.chargeOptions,
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun moveToNextStep() {
        val current = state.currentStep
        if (current < state.totalSteps) {
            mutableStateFlow.update {
                it.copy(
                    currentStep = current + 1,
                )
            }
        }
    }

    private fun moveToPreviousStep() {
        val current = state.currentStep
        mutableStateFlow.update {
            it.copy(
                currentStep = current - 1,
            )
        }
    }

    private fun handleOnDetailNext() {
        if (state.shareProductIndex == null) {
            mutableStateFlow.update {
                it.copy(
                    shareProductError = TextFieldsValidator.stringValidator(""),
                )
            }
        } else {
            loadShareTemplateFromProduct(client = route.clientId, productId = state.productId)

            moveToNextStep()
        }
    }

    private fun handleOnTermsNext() {
        var hasError = false
        var newState = state

        val totalSharesError = TextFieldsValidator.numberValidator(state.totalShares)
        if (totalSharesError != null) {
            newState = newState.copy(totalSharesError = totalSharesError)
            hasError = true
        }

        if (state.savingsAccountIdx == null) {
            newState = newState.copy(savingsAccountError = TextFieldsValidator.stringValidator(""))
            hasError = true
        }

        if (state.minActivePeriodFreq.isNotBlank()) {
            val freqError = TextFieldsValidator.numberValidator(state.minActivePeriodFreq)
            if (freqError != null) {
                newState = newState.copy(minActivePeriodFreqError = freqError)
                hasError = true
            }
            if (state.minActivePeriodFreqTypeIdx == null) {
                newState = newState.copy(
                    minActivePeriodFreqTypeError = TextFieldsValidator.stringValidator(""),
                )
                hasError = true
            }
        }

        if (state.lockInPeriodFreq.isNotBlank()) {
            val freqError = TextFieldsValidator.numberValidator(state.lockInPeriodFreq)
            if (freqError != null) {
                newState = newState.copy(lockInPeriodFreqError = freqError)
                hasError = true
            }
            if (state.lockInPeriodFreqTypeIdx == null) {
                newState =
                    newState.copy(lockInPeriodFreqTypeError = TextFieldsValidator.stringValidator(""))
                hasError = true
            }
        }

        mutableStateFlow.update { newState }

        if (!hasError) {
            moveToNextStep()
        }
    }

    override fun handleAction(action: CreateShareAccountAction) {
        when (action) {
            CreateShareAccountAction.NextStep -> {
                moveToNextStep()
            }

            is CreateShareAccountAction.OnStepChange -> {
                mutableStateFlow.update { it.copy(currentStep = action.index) }
            }

            CreateShareAccountAction.NavigateBack -> {
                sendEvent(CreateShareAccountEvent.NavigateBack)
            }

            CreateShareAccountAction.Finish -> {
                sendEvent(CreateShareAccountEvent.Finish)
            }

            is CreateShareAccountAction.OnSubmissionDateChange -> {
                mutableStateFlow.update {
                    it.copy(
                        submissionDate = action.date,
                    )
                }
            }

            is CreateShareAccountAction.OnApplicationDateChange -> {
                mutableStateFlow.update {
                    it.copy(
                        applicationDate = action.date,
                        applicationDateError = null,
                    )
                }
            }

            is CreateShareAccountAction.OnOpenSubmissionDatePicker -> {
                mutableStateFlow.update {
                    it.copy(
                        showSubmissionDatePicker = action.state,
                    )
                }
            }

            is CreateShareAccountAction.OnOpenApplicationDatePicker -> {
                mutableStateFlow.update {
                    it.copy(
                        showApplicationDatePicker = action.state,
                    )
                }
            }

            is CreateShareAccountAction.OnShareProductChange -> {
                mutableStateFlow.update {
                    it.copy(
                        shareProductIndex = action.index,
                        shareProductError = null,
                        productId = state.productOption[action.index].id,
                    )
                }
            }

            is CreateShareAccountAction.OnSavingsAccountChange -> {
                mutableStateFlow.update {
                    it.copy(
                        savingsAccountIdx = action.index,
                        savingsAccountError = null,
                    )
                }
            }

            is CreateShareAccountAction.OnMinActiveFreqTypeChange -> {
                mutableStateFlow.update {
                    it.copy(
                        minActivePeriodFreqTypeIdx = action.index,
                        minActivePeriodFreqTypeError = null,
                    )
                }
            }

            is CreateShareAccountAction.OnLockInFreqTypeChange -> {
                mutableStateFlow.update {
                    it.copy(
                        lockInPeriodFreqTypeIdx = action.index,
                        lockInPeriodFreqTypeError = null,
                    )
                }
            }

            is CreateShareAccountAction.OnExternalIdChange -> {
                mutableStateFlow.update {
                    it.copy(
                        externalId = action.value,
                    )
                }
            }

            is CreateShareAccountAction.OnTotalSharesChange -> {
                mutableStateFlow.update {
                    it.copy(
                        totalShares = action.value,
                        totalSharesError = null,
                    )
                }
            }

            is CreateShareAccountAction.OnMinActiveFreqChange -> {
                mutableStateFlow.update {
                    it.copy(
                        minActivePeriodFreq = action.value,
                        minActivePeriodFreqError = null,
                    )
                }
            }

            is CreateShareAccountAction.OnLockInFreqChange -> {
                mutableStateFlow.update {
                    it.copy(
                        lockInPeriodFreq = action.value,
                        lockInPeriodFreqError = null,
                    )
                }
            }

            is CreateShareAccountAction.OnIsDividendAllowedClicked -> {
                mutableStateFlow.update {
                    it.copy(
                        isDividendAllowed = !it.isDividendAllowed,
                    )
                }
            }

            CreateShareAccountAction.Retry -> {
                loadShareTemplate(route.clientId)
            }

            CreateShareAccountAction.OnDetailNext -> {
                handleOnDetailNext()
            }

            CreateShareAccountAction.OnTermsNext -> {
                handleOnTermsNext()
            }

            CreateShareAccountAction.PreviousStep -> {
                moveToPreviousStep()
            }

            is CreateShareAccountAction.OnChargeAmountChange -> {
                mutableStateFlow.update {
                    it.copy(
                        chargeAmount = action.amount,
                    )
                }
            }

            is CreateShareAccountAction.AddChargeToList -> {
                val createdData = ChargeItem(
                    chargeId = state.chargeOptions[state.chooseChargeIndex!!].id,
                    amount = state.chargeAmount.toDoubleOrNull(),
                )

                mutableStateFlow.update {
                    it.copy(
                        addedCharges = it.addedCharges + createdData,
                        chooseChargeIndex = null,
                        dialogState = null,
                        chargeAmount = "",
                    )
                }
            }

            is CreateShareAccountAction.EditCharge -> {
                val createdData = ChargeItem(
                    chargeId = state.chargeOptions[action.index].id,
                    amount = state.chargeAmount.toDoubleOrNull(),
                )

                val currentAddedCharges = state.addedCharges.toMutableList()
                currentAddedCharges[action.index] = createdData
                mutableStateFlow.update {
                    it.copy(
                        addedCharges = currentAddedCharges,
                        chooseChargeIndex = null,
                        dialogState = CreateShareAccountState.DialogState.ShowCharges,
                        chargeAmount = "",
                    )
                }
            }

            is CreateShareAccountAction.OnChooseChargeIndexChange -> {
                mutableStateFlow.update {
                    it.copy(
                        chooseChargeIndex = action.index,
                    )
                }
            }

            CreateShareAccountAction.OnDismissDialog -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = null,
                    )
                }
            }

            is CreateShareAccountAction.DeleteChargeFromSelectedCharges -> {
                val newCharges = state.addedCharges.toMutableList().apply {
                    removeAt(action.index)
                }
                mutableStateFlow.update {
                    it.copy(addedCharges = newCharges)
                }
            }

            is CreateShareAccountAction.EditChargeDialog -> {
                val selectedEditCharge = state.addedCharges[action.index]
                val chooseChargeIndex = state.chargeOptions
                    .indexOfFirst { it.id == selectedEditCharge.chargeId }

                mutableStateFlow.update {
                    it.copy(
                        chargeAmount = selectedEditCharge.amount.toString(),
                        chooseChargeIndex = chooseChargeIndex,
                        dialogState = CreateShareAccountState.DialogState.AddNewCharge(
                            true,
                            action.index,
                        ),
                    )
                }
            }

            CreateShareAccountAction.ShowAddChargeDialog -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = CreateShareAccountState.DialogState.AddNewCharge(false),
                    )
                }
            }

            CreateShareAccountAction.ShowListOfChargesDialog -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = CreateShareAccountState.DialogState.ShowCharges,
                    )
                }
            }

            CreateShareAccountAction.SubmitShareAccount -> {
                createShareAccount()
            }
        }
    }
}

data class CreateShareAccountState
@OptIn(ExperimentalTime::class)
constructor(
    val currentStep: Int = 0,
    val totalSteps: Int = 4,
    val dialogState: DialogState? = null,
    val chargeOptions: List<ChargeOptions> = emptyList(),
    val chooseChargeIndex: Int? = null,
    val chargeAmount: String = "",
    val externalId: String? = null,
    val productId: Int? = null,
    val shareProductIndex: Int? = null,
    val shareProductError: StringResource? = null,
    val submissionDate: String = DateHelper.getDateAsStringFromLong(
        Clock.System.now().toEpochMilliseconds(),
    ),
    val showSubmissionDatePicker: Boolean = false,
    val productOption: List<ProductOption> = emptyList(),
    val savingsAccountOptions: List<SavingsAccountOption> = emptyList(),
    val lockInPeriodFrequencyTypeOptions: List<FrequencyTypeOption> = emptyList(),
    val minimumActivePeriodFrequencyTypeOptions: List<FrequencyTypeOption> = emptyList(),
    val currency: String? = null,
    val currentPrice: String? = null,
    val totalShares: String = "",
    val totalSharesError: StringResource? = null,
    val savingsAccountIdx: Int? = null,
    val savingsAccountError: StringResource? = null,
    val applicationDate: String = DateHelper.getDateAsStringFromLong(
        Clock.System.now().toEpochMilliseconds(),
    ),
    val applicationDateError: StringResource? = null,
    val showApplicationDatePicker: Boolean = false,
    val isDividendAllowed: Boolean = false,
    val minActivePeriodFreq: String = "",
    val minActivePeriodFreqError: StringResource? = null,
    val minActivePeriodFreqTypeIdx: Int? = null,
    val minActivePeriodFreqTypeError: StringResource? = null,
    val lockInPeriodFreq: String = "",
    val lockInPeriodFreqError: StringResource? = null,
    val lockInPeriodFreqTypeIdx: Int? = null,
    val lockInPeriodFreqTypeError: StringResource? = null,
    val screenState: ScreenState = ScreenState.Loading,
    val isOverLayLoadingActive: Boolean = false,
    val addedCharges: List<ChargeItem> = emptyList(),
    val launchEffectKey: Int? = null,
) {

    interface ScreenState {
        object Loading : ScreenState
        object Success : ScreenState
        data class Error(val message: String) : ScreenState
    }

    sealed interface DialogState {
        data class AddNewCharge(val edit: Boolean, val index: Int = -1) : DialogState
        data object ShowCharges : DialogState
        data class SuccessResponseStatus(val successStatus: Boolean, val msg: String = "") :
            DialogState
    }
}

sealed interface CreateShareAccountAction {
    object NextStep : CreateShareAccountAction
    data object PreviousStep : CreateShareAccountAction
    data class OnStepChange(val index: Int) : CreateShareAccountAction
    object NavigateBack : CreateShareAccountAction
    object Finish : CreateShareAccountAction
    data class OnShareProductChange(val index: Int) : CreateShareAccountAction
    data class OnSavingsAccountChange(val index: Int) : CreateShareAccountAction
    data class OnSubmissionDateChange(val date: String) : CreateShareAccountAction
    data class OnApplicationDateChange(val date: String) : CreateShareAccountAction
    data class OnOpenSubmissionDatePicker(val state: Boolean) : CreateShareAccountAction
    data class OnOpenApplicationDatePicker(val state: Boolean) : CreateShareAccountAction
    data class OnExternalIdChange(val value: String?) : CreateShareAccountAction
    data class OnTotalSharesChange(val value: String) : CreateShareAccountAction
    data class OnMinActiveFreqChange(val value: String) : CreateShareAccountAction
    data class OnMinActiveFreqTypeChange(val index: Int?) : CreateShareAccountAction
    data class OnLockInFreqChange(val value: String) : CreateShareAccountAction
    data class OnLockInFreqTypeChange(val index: Int?) : CreateShareAccountAction
    data object OnIsDividendAllowedClicked : CreateShareAccountAction
    object OnDetailNext : CreateShareAccountAction
    object OnTermsNext : CreateShareAccountAction
    object Retry : CreateShareAccountAction
    data object AddChargeToList : CreateShareAccountAction
    object OnDismissDialog : CreateShareAccountAction
    object ShowAddChargeDialog : CreateShareAccountAction
    object ShowListOfChargesDialog : CreateShareAccountAction
    data class EditCharge(val index: Int) : CreateShareAccountAction
    data class OnChooseChargeIndexChange(val index: Int) : CreateShareAccountAction
    data class OnChargeAmountChange(val amount: String) : CreateShareAccountAction
    data class DeleteChargeFromSelectedCharges(val index: Int) : CreateShareAccountAction
    data class EditChargeDialog(val index: Int) : CreateShareAccountAction
    object SubmitShareAccount : CreateShareAccountAction
}

sealed interface CreateShareAccountEvent {
    object NavigateBack : CreateShareAccountEvent
    object Finish : CreateShareAccountEvent
}
