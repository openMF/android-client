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

import com.mifos.core.data.repository.LoanAccountApprovalRepository
import com.mifos.core.network.DataManager
import com.mifos.core.network.GenericResponse
import com.mifos.core.objects.accounts.loan.LoanApproval
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 10/08/23.
 */
class LoanAccountApprovalRepositoryImp @Inject constructor(private val dataManager: DataManager) :
    LoanAccountApprovalRepository {

    override fun approveLoan(
        loanId: Int,
        loanApproval: LoanApproval?,
    ): Observable<GenericResponse> {
        return dataManager.approveLoan(loanId, loanApproval)
    }
}
