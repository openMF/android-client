package com.mifos.feature.savings.account_approval

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.Resource
import com.mifos.core.domain.use_cases.ApproveSavingsApplicationUseCase
import com.mifos.core.network.GenericResponse
import com.mifos.core.objects.accounts.loan.SavingsApproval
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 13/08/23.
 */
@HiltViewModel
class SavingsAccountApprovalViewModel @Inject constructor(
    private val approveSavingsApplicationUseCase: ApproveSavingsApplicationUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val savingsAccountId = savedStateHandle.getStateFlow(key = Constants.SAVINGS_ACCOUNT_ID, initialValue = 0)

    private val _savingsAccountApprovalUiState =
        MutableStateFlow<SavingsAccountApprovalUiState>(SavingsAccountApprovalUiState.Initial)
    val savingsAccountApprovalUiState: StateFlow<SavingsAccountApprovalUiState>
        get() = _savingsAccountApprovalUiState

    fun approveSavingsApplication(accountId : Int, savingsApproval: SavingsApproval?) =
        viewModelScope.launch(Dispatchers.IO) {
            approveSavingsApplicationUseCase(accountId, savingsApproval).collect { result ->
                when (result) {
                    is Resource.Error -> _savingsAccountApprovalUiState.value =
                        SavingsAccountApprovalUiState.ShowError(
                            result.message ?: "Something went wrong"
                        )

                    is Resource.Loading -> _savingsAccountApprovalUiState.value =
                        SavingsAccountApprovalUiState.ShowProgressbar

                    is Resource.Success -> _savingsAccountApprovalUiState.value =
                        SavingsAccountApprovalUiState.ShowSavingAccountApprovedSuccessfully(
                            result.data ?: GenericResponse()
                        )
                }
            }
        }

}