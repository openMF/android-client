/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.repositoryImp.loan

import com.mifos.core.common.utils.DataState
import com.mifos.core.data.mappers.loan.toDomain
import com.mifos.core.data.mappers.loan.toDto
import com.mifos.core.data.repository.loan.LoanCreateGuarantorRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.data.util.runAsDataState
import com.mifos.core.model.objects.account.loan.guarantor.CreateGuarantor
import com.mifos.core.model.objects.account.loan.guarantor.CreateGuarantorInput
import com.mifos.core.model.objects.account.loan.guarantor.GuarantorAccountTemplate
import com.mifos.core.model.objects.account.loan.guarantor.GuarantorTemplate
import com.mifos.core.network.datamanager.DataManagerLoan
import kotlinx.coroutines.CoroutineDispatcher

class LoanCreateGuarantorRepositoryImp(
    private val dataManagerLoan: DataManagerLoan,
    private val ioDispatcher: CoroutineDispatcher,
    private val networkMonitor: NetworkMonitor,
) : LoanCreateGuarantorRepository {

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
    ): DataState<CreateGuarantor> {
        return runAsDataState(
            networkMonitor,
            ioDispatcher,
        ) {
            dataManagerLoan.createGuarantor(loanId, createGuarantorInput.toDto()).toDomain()
        }
    }

    override suspend fun getGuarantorAccountTemplate(
        loanId: Int,
        clientId: Int,
    ): DataState<GuarantorAccountTemplate> {
        return runAsDataState(
            networkMonitor,
            ioDispatcher,
        ) {
            dataManagerLoan
                .getGuarantorAccountTemplate(
                    loanId = loanId,
                    clientId = clientId,
                )
                .toDomain()
        }
    }
}
