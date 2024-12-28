/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanTransaction

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.mifos.core.common.utils.Constants
import com.mifos.core.data.repository.LoanTransactionsRepository
import com.mifos.core.dbobjects.accounts.loan.LoanWithAssociations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 12/08/23.
 */
@HiltViewModel
class LoanTransactionsViewModel @Inject constructor(
    private val repository: LoanTransactionsRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val loanId = savedStateHandle.getStateFlow(key = Constants.LOAN_ACCOUNT_NUMBER, initialValue = 0)

    private val _loanTransactionsUiState =
        MutableStateFlow<LoanTransactionsUiState>(LoanTransactionsUiState.ShowProgressBar)
    val loanTransactionsUiState: StateFlow<LoanTransactionsUiState> get() = _loanTransactionsUiState

    fun loadLoanTransaction(loanId: Int) {
        _loanTransactionsUiState.value = LoanTransactionsUiState.ShowProgressBar
        repository.getLoanTransactions(loanId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<LoanWithAssociations>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _loanTransactionsUiState.value =
                        LoanTransactionsUiState.ShowFetchingError(e.message.toString())
                }

                override fun onNext(loanWithAssociations: LoanWithAssociations) {
                    _loanTransactionsUiState.value =
                        LoanTransactionsUiState.ShowLoanTransaction(loanWithAssociations)
                }
            })
    }
}
