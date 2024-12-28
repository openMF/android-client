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

import com.mifos.core.dbobjects.accounts.loan.Loans
import com.mifos.core.objects.template.loan.GroupLoanTemplate
import com.mifos.core.payloads.GroupLoanPayload
import rx.Observable

/**
 * Created by Aditya Gupta on 12/08/23.
 */
interface GroupLoanAccountRepository {

    fun getGroupLoansAccountTemplate(groupId: Int, productId: Int): Observable<GroupLoanTemplate>

    fun createGroupLoansAccount(loansPayload: GroupLoanPayload): Observable<Loans>
}
