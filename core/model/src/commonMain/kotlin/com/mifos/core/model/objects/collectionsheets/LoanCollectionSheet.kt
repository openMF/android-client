/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.model.objects.collectionsheets

import com.mifos.core.model.objects.template.loan.Currency
import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class LoanCollectionSheet(
    val accountId: String? = null,
    val accountStatusId: Int = 0,
    val currency: Currency? = null,
    val interestDue: Double? = null,
    val interestPaid: Double? = null,
    val loanId: Int = 0,
    val principalDue: Double? = null,
    val productId: Double? = null,
    val totalDue: Double = 0.0,
    val chargesDue: Double = 0.0,
    val productShortName: String? = null,
) : Parcelable
