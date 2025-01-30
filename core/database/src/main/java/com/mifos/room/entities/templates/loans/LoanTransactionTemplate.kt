/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.templates.loans

import android.os.Parcelable
import com.mifos.core.objects.template.loan.Type
import com.mifos.room.entities.PaymentTypeOption
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 14/02/17.
 */
@Parcelize
data class LoanTransactionTemplate(
    val type: Type? = null,

    val date: List<Int> = emptyList(),

    val amount: Double? = null,

    val manuallyReversed: Boolean? = null,

    val possibleNextRepaymentDate: List<Int> = emptyList(),

    val paymentTypeOptions: List<PaymentTypeOption> = emptyList(),
) : Parcelable
