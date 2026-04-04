/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repositoryImp

import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.asDataStateFlow
import com.mifos.core.data.repository.LoanAccountSummaryRepository
import com.mifos.core.network.datamanager.DataManagerLoan
import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Created by Aditya Gupta on 08/08/23.
 */
class LoanAccountSummaryRepositoryImp(
    private val dataManagerLoan: DataManagerLoan,
) : LoanAccountSummaryRepository {

    private val _loanUpdateEvents = MutableSharedFlow<Unit>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    override val loanUpdateEvents: Flow<Unit> = _loanUpdateEvents.asSharedFlow()

    override suspend fun triggerLoanUpdate() {
        _loanUpdateEvents.emit(Unit)
    }

    override fun getLoanById(loanId: Int): Flow<DataState<LoanWithAssociationsEntity?>> {
        return dataManagerLoan.getLoanById(loanId)
            .asDataStateFlow()
    }
}
