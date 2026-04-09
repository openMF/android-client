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
import com.mifos.core.data.repository.LoanAccountDisbursementRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.model.objects.account.loan.LoanDisbursement
import com.mifos.core.network.GenericResponse
import com.mifos.core.network.datamanager.DataManagerLoan
import com.mifos.room.entities.templates.loans.LoanTransactionTemplate
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn

/**
 * Created by Aditya Gupta on 10/08/23.
 */
class LoanAccountDisbursementRepositoryImp(
    private val dataManagerLoan: DataManagerLoan,
    private val ioDispatcher: CoroutineDispatcher,
    private val networkMonitor: NetworkMonitor,
) : LoanAccountDisbursementRepository {

    override fun getLoanTransactionTemplate(
        loanId: Int,
        command: String?,
    ): Flow<DataState<LoanTransactionTemplate>> {
        return combine(
            networkMonitor.isOnline,
            dataManagerLoan.getLoanTransactionTemplate(loanId, command)
                .asDataStateFlow(),
        ) { isOnline, dataState ->
            if (!isOnline && dataState !is DataState.Success) {
                DataState.Error(NetworkUnavailableException())
            } else {
                dataState
            }
        }.flowOn(ioDispatcher)
    }

    override suspend fun disburseLoan(
        loanId: Int,
        loanDisbursement: LoanDisbursement?,
    ): DataState<GenericResponse> {
        return try {
            if (!networkMonitor.isOnline.first()) {
                return DataState.Error(NetworkUnavailableException())
            }

            val response = dataManagerLoan.disburseLoan(loanId, loanDisbursement)
                .asDataStateFlow()
                .first { it !is DataState.Loading }

            if (response is DataState.Success) {
                DataState.Success(response.data)
            } else {
                DataState.Error((response as DataState.Error).exception)
            }
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }
}

class NetworkUnavailableException : IllegalStateException()
