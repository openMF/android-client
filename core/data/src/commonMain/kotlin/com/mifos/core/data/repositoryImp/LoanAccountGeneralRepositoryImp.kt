/*
 * Copyright 2026 Mifos Initiative
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
import com.mifos.core.data.mappers.loan.toModel
import com.mifos.core.data.repository.LoanAccountGeneralRepository
import com.mifos.core.model.entity.loan.LoanWithAssociations
import com.mifos.core.network.datamanager.DataManagerLoan
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class LoanAccountGeneralRepositoryImp(
    private val dataManagerLoan: DataManagerLoan,
    private val ioDispatcher: CoroutineDispatcher,
) : LoanAccountGeneralRepository {

    override fun getLoanById(loanId: Int): Flow<DataState<LoanWithAssociations?>> {
        return dataManagerLoan.getLoanById(loanId)
            .asDataStateFlow()
            .map { response ->
                when (response) {
                    is DataState.Loading -> DataState.Loading
                    is DataState.Error -> DataState.Error(response.exception, response.data?.toModel())
                    is DataState.Success -> DataState.Success(response.data?.toModel())
                }
            }
            .flowOn(ioDispatcher)
    }
}
