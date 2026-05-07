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
import com.mifos.core.data.mapper.loan.toDomain
import com.mifos.core.data.repository.LoanAccountSummaryRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.data.util.runAsDataState
import com.mifos.core.model.entity.accounts.loan.GuarantorTemplate
import com.mifos.core.model.objects.account.loan.CreateGuarantorInput
import com.mifos.core.network.GenericResponse
import com.mifos.core.network.datamanager.DataManagerLoan
import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

/**
 * Created by Aditya Gupta on 08/08/23.
 */
class LoanAccountSummaryRepositoryImp(
    private val dataManagerLoan: DataManagerLoan,
    private val ioDispatcher: CoroutineDispatcher,
    private val networkMonitor: NetworkMonitor,
) : LoanAccountSummaryRepository {

    override fun getLoanById(loanId: Int): Flow<DataState<LoanWithAssociationsEntity?>> {
        return dataManagerLoan.getLoanById(loanId)
            .asDataStateFlow()
            .flowOn(ioDispatcher)
    }

    override suspend fun getGuarantorTemplate(loanId: Int): DataState<GuarantorTemplate> {
        return runAsDataState(
            networkMonitor,
            ioDispatcher,
        ) {
            dataManagerLoan.getGuarantorTemplate(loanId).toDomain()
        }
    }

    override suspend fun createGuarantor(
        loanId: Int,
        createGuarantorInput: CreateGuarantorInput,
    ): DataState<GenericResponse> {
        return runAsDataState(
            networkMonitor,
            ioDispatcher,
        ) {
            dataManagerLoan.createGuarantor(loanId, createGuarantorInput.toDto())
        }
    }
}
