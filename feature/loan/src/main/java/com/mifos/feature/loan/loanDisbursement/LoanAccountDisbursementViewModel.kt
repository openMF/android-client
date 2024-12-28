/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanDisbursement

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.mifos.core.common.utils.Constants
import com.mifos.core.data.repository.LoanAccountDisbursementRepository
import com.mifos.core.model.APIEndPoint
import com.mifos.core.`object`.account.loan.LoanDisbursement
import com.mifos.core.network.GenericResponse
import com.mifos.core.objects.templates.loans.LoanTransactionTemplate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 10/08/23.
 */
@HiltViewModel
class LoanAccountDisbursementViewModel @Inject constructor(
    private val repository: LoanAccountDisbursementRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val loadId = savedStateHandle.getStateFlow(key = Constants.LOAN_ACCOUNT_NUMBER, initialValue = 0)

    private val _loanAccountDisbursementUiState = MutableStateFlow<LoanAccountDisbursementUiState>(LoanAccountDisbursementUiState.ShowProgressbar)

    val loanAccountDisbursementUiState: StateFlow<LoanAccountDisbursementUiState>
        get() = _loanAccountDisbursementUiState

    fun loadLoanTemplate(loanId: Int) {
        _loanAccountDisbursementUiState.value = LoanAccountDisbursementUiState.ShowProgressbar
        repository.getLoanTransactionTemplate(loanId, APIEndPoint.DISBURSE)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<LoanTransactionTemplate>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _loanAccountDisbursementUiState.value =
                        LoanAccountDisbursementUiState.ShowError(e.message.toString())
                }

                override fun onNext(loanTransactionTemplate: LoanTransactionTemplate) {
                    _loanAccountDisbursementUiState.value =
                        LoanAccountDisbursementUiState.ShowLoanTransactionTemplate(
                            loanTransactionTemplate,
                        )
                }
            })
    }

    fun disburseLoan(loanId: Int, loanDisbursement: LoanDisbursement?) {
        _loanAccountDisbursementUiState.value = LoanAccountDisbursementUiState.ShowProgressbar
        repository.disburseLoan(loanId, loanDisbursement)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<GenericResponse?>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _loanAccountDisbursementUiState.value =
                        LoanAccountDisbursementUiState.ShowError(e.message.toString())
                }

                override fun onNext(genericResponse: GenericResponse?) {
                    _loanAccountDisbursementUiState.value =
                        LoanAccountDisbursementUiState.ShowDisburseLoanSuccessfully(genericResponse)
                }
            })
    }
}
