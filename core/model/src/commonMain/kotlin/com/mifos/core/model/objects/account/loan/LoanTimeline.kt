/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.model.objects.account.loan

import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class LoanTimeline(
    val loanId: Int? = null,
    val submittedOnDate: List<Int>? = null,
    val submittedByUsername: String? = null,
    val submittedByFirstname: String? = null,
    val submittedByLastname: String? = null,
    val approvedOnDate: List<Int>? = null,
    val approvedByUsername: String? = null,
    val approvedByFirstname: String? = null,
    val approvedByLastname: String? = null,
    val expectedDisbursementDate: List<Int>? = null,
    val actualDisburseDate: ActualDisbursementDate? = null,
    val actualDisbursementDate: List<Int?>? = null,
    val disbursedByUsername: String? = null,
    val disbursedByFirstname: String? = null,
    val disbursedByLastname: String? = null,
    val closedOnDate: List<Int>? = null,
    val expectedMaturityDate: List<Int>? = null,
) : Parcelable
