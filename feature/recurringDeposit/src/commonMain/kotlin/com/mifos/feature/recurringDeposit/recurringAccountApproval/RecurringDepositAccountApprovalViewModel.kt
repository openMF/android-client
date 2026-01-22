package com.mifos.feature.recurringDeposit.recurringAccountApproval

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.RecurringAccountRepository
import com.mifos.core.model.objects.template.recurring.approval.RecurringDepositApprovall
import com.mifos.core.network.GenericResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RecurringDepositAccountApprovalViewModel(
    savedStateHandle: SavedStateHandle,
    private val approveRecurringDepositUseCase: ApproveRecurringDepositUseCase,
) : ViewModel() {

    private val route = savedStateHandle.toRoute<RecurringDepositAccountApprovalRoute>()

    private val _recurringDepositAccountApprovalUiState =
        MutableStateFlow<RecurringDepositAccountApprovalUiState>(
            RecurringDepositAccountApprovalUiState.Initial
        )
    val recurringDepositAccountApprovalUiState: StateFlow<RecurringDepositAccountApprovalUiState> =
        _recurringDepositAccountApprovalUiState.asStateFlow()

    fun approveRecurringDepositApplication(recurringDepositApproval: RecurringDepositApprovall) {
        _recurringDepositAccountApprovalUiState.value =
            RecurringDepositAccountApprovalUiState.ShowProgressbar

        viewModelScope.launch {
            approveRecurringDepositUseCase.invoke(
                accountId = route.accountId,
                approval = recurringDepositApproval
            ).collect { result ->
                when (result) {
                    is DataState.Error -> {
                        _recurringDepositAccountApprovalUiState.value =
                            RecurringDepositAccountApprovalUiState.ShowError(result.message)
                    }

                    is DataState.Loading -> {
                        _recurringDepositAccountApprovalUiState.value =
                            RecurringDepositAccountApprovalUiState.ShowProgressbar
                    }

                    is DataState.Success -> {
                        _recurringDepositAccountApprovalUiState.value =
                            RecurringDepositAccountApprovalUiState.ShowRecurringDepositAccountApprovedSuccessfully(
                                result.data
                            )
                    }
                }
            }
        }
    }
}

// Use Case
/*class ApproveRecurringDepositUseCase(
    private val repository: RecurringDepositRepository,
) {
    operator fun invoke(
        accountId: String,
        approval: RecurringDepositApproval
    ): Flow<DataState<GenericResponse>> = flow {
        emit(repository.approveRecurringDepositAccount(accountId, approval))
    }.asDataStateFlow()
}*/
class ApproveRecurringDepositUseCase(
    private val repository: RecurringAccountRepository,
) {
    operator fun invoke(
        accountId: String,
        approval: RecurringDepositApprovall
    ): Flow<DataState<GenericResponse>> {
        return repository.approveRecurringDepositAccount(accountId, approval)
    }
}

// Repository interface (add this method to your existing repository)
interface RecurringDepositRepository {
    suspend fun approveRecurringDepositAccount(
        accountId: String,
        approval: RecurringDepositApproval
    ): GenericResponse
}