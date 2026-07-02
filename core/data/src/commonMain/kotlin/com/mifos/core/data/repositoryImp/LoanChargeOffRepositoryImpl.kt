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
import com.mifos.core.data.mappers.loan.toDto
import com.mifos.core.data.mappers.loan.toModel
import com.mifos.core.data.repository.LoanChargeOffRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.data.util.runAsDataState
import com.mifos.core.model.objects.account.loan.ChargeOffReasonOption
import com.mifos.core.model.objects.account.loan.LoanChargeOffInput
import com.mifos.core.network.datamanager.DataManagerLoan
import template.core.base.common.manager.DispatcherManager

class LoanChargeOffRepositoryImpl(
    private val dataManagerLoan: DataManagerLoan,
    private val networkMonitor: NetworkMonitor,
    private val dispatcher: DispatcherManager,
) : LoanChargeOffRepository {
    override suspend fun chargeOff(
        loanId: Int,
        loanChargeOffInput: LoanChargeOffInput,
    ): DataState<Unit> {
        return runAsDataState(
            networkMonitor = networkMonitor,
            context = dispatcher.io,
        ) {
            dataManagerLoan.chargeOff(loanId, loanChargeOffInput.toDto())
        }
    }

    override suspend fun getChargeOffTemplate(loanId: Int): DataState<List<ChargeOffReasonOption>> {
        return runAsDataState(
            networkMonitor = networkMonitor,
            context = dispatcher.io,
        ) {
            dataManagerLoan.getChargeOffTemplate(loanId).chargeOffReasonOptions.map { it.toModel() }
        }
    }
}
