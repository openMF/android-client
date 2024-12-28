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

import com.mifos.core.data.repository.GroupLoanAccountRepository
import com.mifos.core.`object`.template.loan.GroupLoanTemplate
import com.mifos.core.network.DataManager
import com.mifos.core.objects.accounts.loan.Loans
import com.mifos.core.payloads.GroupLoanPayload
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 12/08/23.
 */
class GroupLoanAccountRepositoryImp @Inject constructor(private val dataManager: DataManager) :
    GroupLoanAccountRepository {

    override fun getGroupLoansAccountTemplate(
        groupId: Int,
        productId: Int,
    ): Observable<GroupLoanTemplate> {
        return dataManager.getGroupLoansAccountTemplate(groupId, productId)
    }

    override fun createGroupLoansAccount(loansPayload: GroupLoanPayload): Observable<Loans> {
        return dataManager.createGroupLoansAccount(loansPayload)
    }
}
