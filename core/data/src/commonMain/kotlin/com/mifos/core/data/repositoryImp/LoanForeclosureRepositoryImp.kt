/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.repositoryImp

import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.LoanForeclosureRepository
import com.mifos.core.model.objects.account.loan.foreclosure.LoanForeclosureInput
import com.mifos.core.model.objects.account.loan.foreclosure.LoanForeclosureTemplate
import com.mifos.core.network.datamanager.DataManagerLoan
import com.mifos.core.network.mappers.loan.toDomain
import com.mifos.core.network.mappers.loan.toDto
import kotlinx.coroutines.withContext
import template.core.base.common.manager.DispatcherManager

class LoanForeclosureRepositoryImp(
    private val dataManager: DataManagerLoan,
    private val dispatcher: DispatcherManager,
) : LoanForeclosureRepository {

    override suspend fun getLoanForeclosureTemplate(
        loanId: Int,
        transactionDate: String,
        dateFormat: String,
        locale: String,
    ): DataState<LoanForeclosureTemplate> {
        return withContext(dispatcher.io) {
            try {
                val dto = dataManager.getLoanForeclosureTemplate(
                    loanId = loanId,
                    transactionDate = transactionDate,
                    dateFormat = dateFormat,
                    locale = locale,
                )
                DataState.Success(dto.toDomain())
            } catch (e: Exception) {
                DataState.Error(e)
            }
        }
    }

    override suspend fun submitLoanForeclosure(
        loanId: Int,
        input: LoanForeclosureInput,
    ): DataState<Unit> {
        return withContext(dispatcher.io) {
            try {
                dataManager.submitLoanForeclosure(loanId, input.toDto())
                DataState.Success(Unit)
            } catch (e: Exception) {
                DataState.Error(e)
            }
        }
    }
}
