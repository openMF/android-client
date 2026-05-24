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
import com.mifos.core.common.utils.asDataStateFlow
import com.mifos.core.data.repository.LoanAccountGeneralRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.data.util.withNetworkCheck
import com.mifos.core.model.objects.account.loan.loanWithAssociations.LoanWithAssociations
import com.mifos.core.network.datamanager.DataManagerLoan
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class LoanAccountGeneralRepositoryImp(
    private val dataManagerLoan: DataManagerLoan,
    private val networkMonitor: NetworkMonitor,
    private val ioDispatcher: CoroutineDispatcher,
) : LoanAccountGeneralRepository {

    override fun getLoanById(loanId: Int): Flow<DataState<LoanWithAssociations?>> {
        return networkMonitor.withNetworkCheck(
            dataManagerLoan.getLoanById(loanId)
                .asDataStateFlow(),
        ).flowOn(ioDispatcher)
    }
}
