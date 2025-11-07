/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.createShareAccount

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.feature_client_error_network_not_available
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.data.repository.ShareAccountRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.network.model.share.FrequencyTypeOption
import com.mifos.core.network.model.share.ProductOption
import com.mifos.core.network.model.share.SavingsAccountOption
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.core.ui.util.TextFieldsValidator
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class CreateShareAccountViewModel(
    private val repository: ShareAccountRepository,
    private val networkMonitor: NetworkMonitor,
    val savedStateHandle: SavedStateHandle,

) : BaseViewModel<ShareAccountState, ShareAccountEvent, ShareAccountAction>
    (ShareAccountState()) {

    val route = savedStateHandle.toRoute<CreateShareAccountRoute>()

    init {
        loadShareTemplate(route.clientId)
    }

    private fun loadShareTemplateFromProduct(client: Int, productId: Int) {
        viewModelScope.launch {
            val online = networkMonitor.isOnline.first()
            if (online) {
                repository.getShareTemplate(client, productId).collect { dataState ->
                    when (dataState) {
                        is DataState.Error -> {
                            mutableStateFlow.update {
                                it.copy(
                                    screenState = ShareAccountState.ScreenState.Error(dataState.message),
                                )
                            }
                        }

                        DataState.Loading -> {
                            mutableStateFlow.update {
                                it.copy(
                                    screenState = ShareAccountState.ScreenState.Loading,
                                )
                            }
                        }

                        is DataState.Success -> {
                            mutableStateFlow.update {
                                it.copy(
                                    screenState = ShareAccountState.ScreenState.Success,
                                    currency = dataState.data.currency?.name,
                                    currentPrice = dataState.data.currentMarketPrice?.toString(),
                                    savingsAccountOptions = dataState.data.savingsAccountOptions.orEmpty(),
                                    lockInPeriodFrequencyTypeOptions = dataState.data.lockinPeriodFrequencyTypeOptions.orEmpty(),
                                    minimumActivePeriodFrequencyTypeOptions = dataState.data.minimumActivePeriodFrequencyTypeOptions.orEmpty(),
                                )
                            }
                        }
                    }
                }
            } else {
                mutableStateFlow.update {
                    it.copy(
                        screenState = ShareAccountState.ScreenState.Error(getString(Res.string.feature_client_error_network_not_available)),
                    )
                }
            }
        }
    }

    private fun loadShareTemplate(client: Int) {
        viewModelScope.launch {
            val online = networkMonitor.isOnline.first()
            if (online) {
                repository.getShareTemplate(client, null).collect { dataState ->
                    when (dataState) {
                        is DataState.Error -> {
                            mutableStateFlow.update {
                                it.copy(
                                    screenState = ShareAccountState.ScreenState.Error(dataState.message),
                                )
                            }
                        }

                        DataState.Loading -> {
                            mutableStateFlow.update {
                                it.copy(
                                    screenState = ShareAccountState.ScreenState.Loading,
                                )
                            }
                        }

                        is DataState.Success -> {
                            mutableStateFlow.update {
                                it.copy(
                                    screenState = ShareAccountState.ScreenState.Success,
                                    productOption = dataState.data.productOptions,
                                )
                            }
                        }
                    }
                }
            } else {
                mutableStateFlow.update {
                    it.copy(
                        screenState = ShareAccountState.ScreenState.Error(getString(Res.string.feature_client_error_network_not_available)),
                    )
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
            state.selectedProduct?.id?.let { productId ->
                loadShareTemplateFromProduct(client = route.clientId, productId = productId)
            }
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
                newState = newState.copy(minActivePeriodFreqTypeError = TextFieldsValidator.stringValidator(""))
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
                newState = newState.copy(lockInPeriodFreqTypeError = TextFieldsValidator.stringValidator(""))
                hasError = true
            }
        }

        mutableStateFlow.update { newState }

        if (!hasError) {
            moveToNextStep()
        }
    }

    override fun handleAction(action: ShareAccountAction) {
        when (action) {
            ShareAccountAction.NextStep -> {
                moveToNextStep()
            }

            is ShareAccountAction.OnStepChange -> {
                mutableStateFlow.update { it.copy(currentStep = action.index) }
            }

            ShareAccountAction.NavigateBack -> {
                sendEvent(ShareAccountEvent.NavigateBack)
            }

            ShareAccountAction.Finish -> {
                sendEvent(ShareAccountEvent.Finish)
            }

            is ShareAccountAction.OnSubmissionDateChange -> {
                mutableStateFlow.update {
                    it.copy(
                        submissionDate = action.date,
                    )
                }
            }

            is ShareAccountAction.OnApplicationDateChange -> {
                mutableStateFlow.update {
                    it.copy(
                        applicationDate = action.date,
                        applicationDateError = null,
                    )
                }
            }

            is ShareAccountAction.OnOpenSubmissionDatePicker -> {
                mutableStateFlow.update {
                    it.copy(
                        showSubmissionDatePicker = action.state,
                    )
                }
            }

            is ShareAccountAction.OnOpenApplicationDatePicker -> {
                mutableStateFlow.update {
                    it.copy(
                        showApplicationDatePicker = action.state,
                    )
                }
            }

            is ShareAccountAction.OnShareProductChange -> {
                mutableStateFlow.update {
                    it.copy(
                        shareProductIndex = action.index,
                        shareProductError = null,
                    )
                }
            }

            is ShareAccountAction.OnSavingsAccountChange -> {
                mutableStateFlow.update {
                    it.copy(
                        savingsAccountIdx = action.index,
                        savingsAccountError = null,
                    )
                }
            }

            is ShareAccountAction.OnMinActiveFreqTypeChange -> {
                mutableStateFlow.update {
                    it.copy(
                        minActivePeriodFreqTypeIdx = action.index,
                        minActivePeriodFreqTypeError = null,
                    )
                }
            }

            is ShareAccountAction.OnLockInFreqTypeChange -> {
                mutableStateFlow.update {
                    it.copy(
                        lockInPeriodFreqTypeIdx = action.index,
                        lockInPeriodFreqTypeError = null,
                    )
                }
            }

            is ShareAccountAction.OnExternalIdChange -> {
                mutableStateFlow.update {
                    it.copy(
                        externalId = action.value,
                    )
                }
            }

            is ShareAccountAction.OnTotalSharesChange -> {
                mutableStateFlow.update {
                    it.copy(
                        totalShares = action.value,
                        totalSharesError = null,
                    )
                }
            }

            is ShareAccountAction.OnMinActiveFreqChange -> {
                mutableStateFlow.update {
                    it.copy(
                        minActivePeriodFreq = action.value,
                        minActivePeriodFreqError = null,
                    )
                }
            }

            is ShareAccountAction.OnLockInFreqChange -> {
                mutableStateFlow.update {
                    it.copy(
                        lockInPeriodFreq = action.value,
                        lockInPeriodFreqError = null,
                    )
                }
            }

            is ShareAccountAction.OnIsDividendAllowedClicked -> {
                mutableStateFlow.update {
                    it.copy(
                        isDividendAllowed = !it.isDividendAllowed,
                    )
                }
            }

            ShareAccountAction.Retry -> {
                loadShareTemplate(route.clientId)
            }

            ShareAccountAction.OnDetailNext -> {
                handleOnDetailNext()
            }

            ShareAccountAction.OnTermsNext -> {
                handleOnTermsNext()
            }

            ShareAccountAction.PreviousStep -> {
                moveToPreviousStep()
            }
        }
    }
}

data class ShareAccountState
@OptIn(ExperimentalTime::class)
constructor(
    val currentStep: Int = 0,
    val totalSteps: Int = 4,
    val dialogState: Any? = null,
    val externalId: String? = null,
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
) {
    val selectedProduct: ProductOption? get() = shareProductIndex?.let { productOption.getOrNull(it) }

    interface ScreenState {
        object Loading : ScreenState
        object Success : ScreenState
        data class Error(val message: String) : ScreenState
    }
}

sealed interface ShareAccountAction {
    object NextStep : ShareAccountAction
    data object PreviousStep : ShareAccountAction
    data class OnStepChange(val index: Int) : ShareAccountAction
    object NavigateBack : ShareAccountAction
    object Finish : ShareAccountAction
    data class OnShareProductChange(val index: Int) : ShareAccountAction
    data class OnSavingsAccountChange(val index: Int) : ShareAccountAction
    data class OnSubmissionDateChange(val date: String) : ShareAccountAction
    data class OnApplicationDateChange(val date: String) : ShareAccountAction
    data class OnOpenSubmissionDatePicker(val state: Boolean) : ShareAccountAction
    data class OnOpenApplicationDatePicker(val state: Boolean) : ShareAccountAction
    data class OnExternalIdChange(val value: String?) : ShareAccountAction
    data class OnTotalSharesChange(val value: String) : ShareAccountAction
    data class OnMinActiveFreqChange(val value: String) : ShareAccountAction
    data class OnMinActiveFreqTypeChange(val index: Int?) : ShareAccountAction
    data class OnLockInFreqChange(val value: String) : ShareAccountAction
    data class OnLockInFreqTypeChange(val index: Int?) : ShareAccountAction
    data object OnIsDividendAllowedClicked : ShareAccountAction
    object OnDetailNext : ShareAccountAction
    object OnTermsNext : ShareAccountAction
    object Retry : ShareAccountAction
}

sealed interface ShareAccountEvent {
    object NavigateBack : ShareAccountEvent
    object Finish : ShareAccountEvent
}
