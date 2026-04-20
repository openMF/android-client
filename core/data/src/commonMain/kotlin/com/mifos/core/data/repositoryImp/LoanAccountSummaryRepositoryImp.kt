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
import com.mifos.core.data.mapper.loan.toDto
import com.mifos.core.data.mapper.loan.toModel
import com.mifos.core.data.repository.LoanAccountSummaryRepository
import com.mifos.core.model.entity.accounts.loan.CreateGuarantorRequest
import com.mifos.core.model.entity.accounts.loan.GuarantorTemplate
import com.mifos.core.network.GenericResponse
import com.mifos.core.network.datamanager.DataManagerLoan
import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

/**
 * Created by Aditya Gupta on 08/08/23.
 */
class LoanAccountSummaryRepositoryImp(
    private val dataManagerLoan: DataManagerLoan,
    private val ioDispatcher: CoroutineDispatcher,
) : LoanAccountSummaryRepository {

    override fun getLoanById(loanId: Int): Flow<DataState<LoanWithAssociationsEntity?>> {
        return dataManagerLoan.getLoanById(loanId)
            .asDataStateFlow()
            .flowOn(ioDispatcher)
    }

    override suspend fun getGuarantorTemplate(loanId: Int): DataState<GuarantorTemplate> {
        return withContext(ioDispatcher) {
            try {
                DataState.Success(dataManagerLoan.getGuarantorTemplate(loanId).toModel())
            } catch (throwable: Throwable) {
                if (throwable is CancellationException) throw throwable
                DataState.Error(throwable)
            }
        }
    }

    override suspend fun createGuarantor(
        loanId: Int,
        request: CreateGuarantorRequest,
    ): DataState<GenericResponse> {
        return withContext(ioDispatcher) {
            try {
                DataState.Success(dataManagerLoan.createGuarantor(loanId, request.toDto()))
            } catch (throwable: Throwable) {
                if (throwable is CancellationException) throw throwable
                DataState.Error(throwable)
            }
        }
    }
}
