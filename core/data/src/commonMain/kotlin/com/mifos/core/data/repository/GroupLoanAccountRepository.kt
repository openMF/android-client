/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repository

import com.mifos.core.common.utils.DataState
import com.mifos.core.model.objects.payloads.GroupLoanPayload
import com.mifos.core.model.objects.template.loan.GroupLoanTemplate
import com.mifos.room.entities.accounts.loans.Loan
import kotlinx.coroutines.flow.Flow

/**
 * Created by Aditya Gupta on 12/08/23.
 */
interface GroupLoanAccountRepository {

    fun getGroupLoansAccountTemplate(groupId: Int, productId: Int): Flow<DataState<GroupLoanTemplate>>

    fun createGroupLoansAccount(loansPayload: GroupLoanPayload): Flow<DataState<Loan>>
}
