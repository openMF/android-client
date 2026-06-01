/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.repositoryImp

import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.asDataStateFlow
import com.mifos.core.data.repository.LoanAccountApprovalRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.data.util.NetworkUnavailableException
import com.mifos.core.data.util.requireOnline
import com.mifos.core.data.util.withNetworkCheck
import com.mifos.core.model.objects.account.loan.LoanApproval
import com.mifos.core.model.objects.account.loan.LoanUndoApprovalRequest
import com.mifos.core.network.DataManager
import com.mifos.core.network.GenericResponse
import com.mifos.core.network.datamanager.DataManagerLoan
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import template.core.base.common.manager.DispatcherManager

/**
 * Created by Aditya Gupta on 10/08/23.
 */
class LoanAccountApprovalRepositoryImp(
    private val dataManager: DataManager,
    private val dataManagerLoan: DataManagerLoan,
    private val dispatcherManager: DispatcherManager,
    private val ioDispatcher: CoroutineDispatcher,
    private val networkMonitor: NetworkMonitor,
) : LoanAccountApprovalRepository {

    override fun approveLoan(
        loanId: Int,
        loanApproval: LoanApproval?,
    ): Flow<DataState<GenericResponse>> {
        return networkMonitor.withNetworkCheck(
            dataManager.approveLoan(loanId, loanApproval)
                .asDataStateFlow(),
        ).flowOn(ioDispatcher)
    }

    override suspend fun undoLoanApproval(
        loanId: Int,
        noteRequest: LoanUndoApprovalRequest,
    ): DataState<Unit> {
        if (!networkMonitor.requireOnline()) {
            return DataState.Error(NetworkUnavailableException())
        }

        return try {
            val response = withContext(dispatcherManager.io) {
                dataManagerLoan.undoLoanApproval(
                    loanId = loanId,
                    noteRequest = noteRequest,
                )
            }
            DataState.Success(response)
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }
}
