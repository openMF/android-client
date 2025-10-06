/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.feature.client.fixedDepositAccount.preview

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.mifos.core.ui.util.BaseViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PreviewStepViewModel(
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel<
    PreviewStepState,
    PreviewStepEvent,
    PreviewStepAction
>(initialState = PreviewStepState()) {

    init {
        loadPreviewData()
    }

    override fun handleAction(action: PreviewStepAction) {
        when (action) {
            is PreviewStepAction.ToggleSection -> toggleSection(action.sectionId)
            PreviewStepAction.ViewRateChart -> showRateChartModal()
            PreviewStepAction.DownloadRateChart -> downloadRateChart()
            PreviewStepAction.DismissRateChartModal -> dismissRateChartModal()
            is PreviewStepAction.EditRateChartEntry -> editRateChartEntry(action.entry)
            is PreviewStepAction.DeleteRateChartEntry -> showDeleteConfirmation(action.entry)
            PreviewStepAction.ConfirmDelete -> confirmDelete()
            PreviewStepAction.DismissDeleteConfirmation -> dismissDeleteConfirmation()
            PreviewStepAction.NavigateBack -> navigateBack()
            PreviewStepAction.Submit -> submitAccount()
        }
    }

    private fun loadPreviewData() {
        viewModelScope.launch {
            // Load data from SavedStateHandle or repository
            // This should retrieve data from previous steps
            
            // Example: Loading from SavedStateHandle
            val productName = savedStateHandle.get<String>("productName") ?: ""
            val submissionDate = savedStateHandle.get<String>("submissionDate") ?: ""
            val externalId = savedStateHandle.get<String>("externalId") ?: ""
            val fieldOfficer = savedStateHandle.get<String>("fieldOfficer") ?: ""
            
            val currency = savedStateHandle.get<String>("currency") ?: ""
            val currencyMultiple = savedStateHandle.get<String>("currencyMultiple") ?: ""
            val decimalPlaces = savedStateHandle.get<String>("decimalPlaces") ?: ""
            
            val depositAmount = savedStateHandle.get<String>("depositAmount") ?: ""
            val depositPeriod = savedStateHandle.get<String>("depositPeriod") ?: ""
            val compoundingPeriod = savedStateHandle.get<String>("compoundingPeriod") ?: ""
            val postingPeriod = savedStateHandle.get<String>("postingPeriod") ?: ""
            val calculationMethod = savedStateHandle.get<String>("calculationMethod") ?: ""
            val daysInYear = savedStateHandle.get<String>("daysInYear") ?: ""
            
            val lockInPeriod = savedStateHandle.get<String>("lockInPeriod") ?: ""
            val minDepositTerm = savedStateHandle.get<String>("minDepositTerm") ?: ""
            val transferToSavings = savedStateHandle.get<String>("transferToSavings") ?: ""
            val maturityInstructions = savedStateHandle.get<String>("maturityInstructions") ?: ""
            val applyPenalInterest = savedStateHandle.get<String>("applyPenalInterest") ?: ""
            
            val chartName = savedStateHandle.get<String>("chartName") ?: ""
            val chartValidFrom = savedStateHandle.get<String>("chartValidFrom") ?: ""
            val chartValidTo = savedStateHandle.get<String>("chartValidTo") ?: ""
            val chartDescription = savedStateHandle.get<String>("chartDescription") ?: ""
            val chartGroupingByAmount = savedStateHandle.get<String>("chartGroupingByAmount") ?: ""
            
            // Load rate chart entries
            // This would typically come from a repository or saved state
            val rateChartEntries = listOf(
                RateChartEntry(
                    id = "1",
                    amountRange = "0 - 10,000",
                    period = "3-6 months",
                    interestRate = 5.5
                ),
                RateChartEntry(
                    id = "2",
                    amountRange = "10,000 - 50,000",
                    period = "6-12 months",
                    interestRate = 6.0
                ),
                RateChartEntry(
                    id = "3",
                    amountRange = "50,000+",
                    period = "12+ months",
                    interestRate = 6.5
                )
            )

            mutableStateFlow.update {
                it.copy(
                    productName = productName,
                    submissionDate = submissionDate,
                    externalId = externalId,
                    fieldOfficer = fieldOfficer,
                    currency = currency,
                    currencyMultiple = currencyMultiple,
                    decimalPlaces = decimalPlaces,
                    depositAmount = depositAmount,
                    depositPeriod = depositPeriod,
                    compoundingPeriod = compoundingPeriod,
                    postingPeriod = postingPeriod,
                    calculationMethod = calculationMethod,
                    daysInYear = daysInYear,
                    lockInPeriod = lockInPeriod,
                    minDepositTerm = minDepositTerm,
                    transferToSavings = transferToSavings,
                    maturityInstructions = maturityInstructions,
                    applyPenalInterest = applyPenalInterest,
                    chartName = chartName,
                    chartValidFrom = chartValidFrom,
                    chartValidTo = chartValidTo,
                    chartDescription = chartDescription,
                    chartGroupingByAmount = chartGroupingByAmount,
                    rateChartEntries = rateChartEntries
                )
            }
        }
    }

    private fun toggleSection(sectionId: String) {
        mutableStateFlow.update { currentState ->
            val expandedSections = currentState.expandedSections.toMutableSet()
            if (expandedSections.contains(sectionId)) {
                expandedSections.remove(sectionId)
            } else {
                expandedSections.add(sectionId)
            }
            currentState.copy(expandedSections = expandedSections)
        }
    }

    private fun showRateChartModal() {
        mutableStateFlow.update {
            it.copy(showRateChartModal = true)
        }
    }

    private fun dismissRateChartModal() {
        mutableStateFlow.update {
            it.copy(showRateChartModal = false)
        }
    }

    private fun downloadRateChart() {
        viewModelScope.launch {
            // Implement download logic
            // Generate CSV or PDF of the rate chart
            sendEvent(PreviewStepEvent.ShowMessage("Rate chart downloaded successfully"))
        }
    }

    private fun editRateChartEntry(entry: RateChartEntry) {
        // Navigate to edit screen or show edit dialog
        sendEvent(PreviewStepEvent.NavigateToEditRateChart(entry))
    }

    private fun showDeleteConfirmation(entry: RateChartEntry) {
        mutableStateFlow.update {
            it.copy(showDeleteConfirmation = entry)
        }
    }

    private fun dismissDeleteConfirmation() {
        mutableStateFlow.update {
            it.copy(showDeleteConfirmation = null)
        }
    }

    private fun confirmDelete() {
        val entryToDelete = state.showDeleteConfirmation
        if (entryToDelete != null) {
            mutableStateFlow.update { currentState ->
                currentState.copy(
                    rateChartEntries = currentState.rateChartEntries.filter { it.id != entryToDelete.id },
                    showDeleteConfirmation = null
                )
            }
            sendEvent(PreviewStepEvent.ShowMessage("Rate chart entry deleted"))
        }
    }

    private fun navigateBack() {
        // Navigate to Charges step (Step 5)
        sendEvent(PreviewStepEvent.NavigateToChargesStep)
    }

    private fun submitAccount() {
        viewModelScope.launch {
            // Validate all data before submission
            if (validateData()) {
                sendEvent(PreviewStepEvent.NavigateToConfirmation)
            } else {
                sendEvent(PreviewStepEvent.ShowError("Please ensure all required fields are filled"))
            }
        }
    }

    private fun validateData(): Boolean {
        // Validate that all required fields are present
        with(state) {
            return productName.isNotEmpty() &&
                    currency.isNotEmpty() &&
                    depositAmount.isNotEmpty() &&
                    depositPeriod.isNotEmpty()
        }
    }
}

sealed class PreviewStepEvent {
    data class ShowMessage(val message: String) : PreviewStepEvent()
    data class ShowError(val message: String) : PreviewStepEvent()
    data class NavigateToEditRateChart(val entry: RateChartEntry) : PreviewStepEvent()
    data object NavigateToChargesStep : PreviewStepEvent()
    data object NavigateToConfirmation : PreviewStepEvent()
}