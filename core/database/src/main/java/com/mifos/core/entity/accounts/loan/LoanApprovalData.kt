/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.entity.accounts.loan

import android.os.Parcelable
import com.mifos.room.entities.accounts.loans.LoanWithAssociations
import kotlinx.parcelize.Parcelize

/**
 * Created by Pronay Sarker on 16/08/2024 (7:17 AM)
 */
@Parcelize
data class LoanApprovalData(
    val loanID: Int,
    val loanWithAssociations: LoanWithAssociations,
) : Parcelable
