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

import com.mifos.core.data.repository.LoanTransactionsRepository
import com.mifos.core.entity.accounts.loan.LoanWithAssociations
import com.mifos.core.network.DataManager
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 12/08/23.
 */
class LoanTransactionsRepositoryImp @Inject constructor(private val dataManager: DataManager) :
    LoanTransactionsRepository {

    override fun getLoanTransactions(loan: Int): Observable<LoanWithAssociations> {
        return dataManager.getLoanTransactions(loan)
    }
}
