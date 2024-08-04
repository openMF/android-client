package com.mifos.feature.savings.account_approval

import androidx.lifecycle.ViewModel
import com.mifos.core.data.repository.SavingsAccountApprovalRepository
import com.mifos.core.network.GenericResponse
import com.mifos.core.objects.accounts.loan.SavingsApproval
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 13/08/23.
 */
@HiltViewModel
class SavingsAccountApprovalViewModel @Inject constructor(private val repository: SavingsAccountApprovalRepository) :
    ViewModel() {

    private val _savingsAccountApprovalUiState =
        MutableStateFlow<SavingsAccountApprovalUiState>(SavingsAccountApprovalUiState.Initial)
    val savingsAccountApprovalUiState: StateFlow<SavingsAccountApprovalUiState>
        get() = _savingsAccountApprovalUiState

    var savingsAccountId = 0

    fun approveSavingsApplication(savingsApproval: SavingsApproval?) {
        _savingsAccountApprovalUiState.value = SavingsAccountApprovalUiState.ShowProgressbar
        repository
            .approveSavingsApplication(savingsAccountId, savingsApproval)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<GenericResponse>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _savingsAccountApprovalUiState.value =
                        SavingsAccountApprovalUiState.ShowError(e.message ?: "Something went wrong")
                }

                override fun onNext(genericResponse: GenericResponse) {
                    _savingsAccountApprovalUiState.value =
                        SavingsAccountApprovalUiState.ShowSavingAccountApprovedSuccessfully(
                            genericResponse
                        )
                }
            })
    }
}