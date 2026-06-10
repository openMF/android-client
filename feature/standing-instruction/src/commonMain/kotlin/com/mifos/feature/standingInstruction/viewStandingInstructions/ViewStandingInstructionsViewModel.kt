/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.standingInstructions.viewStandingInstructions

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.DataState.Loading
import com.mifos.core.common.utils.DataState.Success
import com.mifos.core.common.utils.Page
import com.mifos.core.data.repository.StandingInstructionsRepository
import com.mifos.core.model.objects.standingInstructions.StandingInstruction
import com.mifos.core.ui.util.BaseViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ViewStandingInstructionsViewModel(
    private val repository: StandingInstructionsRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<ViewStandingInstructionsState, ViewStandingInstructionsEvent, ViewStandingInstructionsAction>(
    initialState = run {
        val route = savedStateHandle.toRoute<ViewStandingInstructionsScreenRoute>()
        ViewStandingInstructionsState(
            fromAccountId = route.fromAccountId,
            fromAccountType = route.fromAccountType,
            clientId = route.clientId,
            clientName = route.clientName,
        )
    },
) {

    init {
        viewModelScope.launch {
            loadStandingInstructions()
        }
    }

    override fun handleAction(action: ViewStandingInstructionsAction) {
        when (action) {
            ViewStandingInstructionsAction.OnNavigateBack -> {
                sendEvent(ViewStandingInstructionsEvent.NavigateBack)
            }

            ViewStandingInstructionsAction.Retry -> {
                retry()
            }

            ViewStandingInstructionsAction.DismissErrorDialog -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }
            }
        }
    }

    private fun retry() {
        viewModelScope.launch {
            loadStandingInstructions()
        }
    }

    private fun loadStandingInstructions() {
        mutableStateFlow.update {
            it.copy(dataState = DataState.Loading)
        }

        viewModelScope.launch {
            repository.getStandingInstructionList(
                clientId = state.clientId,
                clientName = state.clientName,
                fromAccountId = state.fromAccountId,
                fromAccountType = state.fromAccountType,
                limit = 14,
                offset = 0,
            ).collect { dataState ->
                when (dataState) {
                    is DataState.Error -> {
                        mutableStateFlow.update {
                            it.copy(
                                dataState = dataState,
                            )
                        }
                    }

                    Loading -> {
                        mutableStateFlow.update {
                            it.copy(dataState = DataState.Loading)
                        }
                    }

                    is Success -> {
                        val tableData = mapToTableData(dataState.data)
                        mutableStateFlow.update {
                            it.copy(
                                tableData = tableData,
                                dataState = DataState.Success(dataState.data),
                            )
                        }
                    }
                }
            }
        }
    }

    private fun mapToTableData(page: Page<StandingInstruction>): StandingInstructionTableData {
        val rows = page.pageItems.map { instruction ->
            StandingInstructionRowData(
                id = instruction.id,
                client = instruction.fromClient?.displayName ?: "--",
                fromAccount = instruction.fromAccount?.accountNo ?: "--",
                beneficiary = instruction.toClient?.displayName ?: "--",
                toAccount = instruction.toAccount?.accountNo ?: "--",
                amount = instruction.amount?.toString() ?: "--",
                validity = instruction.validFrom ?: "-- ",
            )
        }
        return StandingInstructionTableData(rows = rows)
    }
}

/**
 * Represents the state of the standing instructions screen.
 */
data class ViewStandingInstructionsState(
    val fromAccountId: Long,
    val fromAccountType: Int,
    val clientId: Long,
    val clientName: String,
    val dataState: DataState<Page<StandingInstruction>> = Loading,
    val tableData: StandingInstructionTableData? = null,
    val dialogState: DialogState? = null,
) {
    sealed interface DialogState {
        data class Error(val title: String, val message: String) : DialogState
    }
}

data class StandingInstructionTableData(
    val rows: List<StandingInstructionRowData>,
)

data class StandingInstructionRowData(
    val id: Int,
    val client: String,
    val fromAccount: String,
    val beneficiary: String,
    val toAccount: String,
    val amount: String,
    val validity: String,
)

/**
 * One-time events emitted by the ViewModel.
 */
sealed interface ViewStandingInstructionsEvent {
    data object NavigateBack : ViewStandingInstructionsEvent
}

/**
 * Actions that can be sent to the ViewModel.
 */
sealed interface ViewStandingInstructionsAction {
    data object OnNavigateBack : ViewStandingInstructionsAction
    data object Retry : ViewStandingInstructionsAction
    data object DismissErrorDialog : ViewStandingInstructionsAction
}
