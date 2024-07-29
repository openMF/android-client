/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.templates.loans

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 14/02/17.
 */
@Parcelize
data class LoanTransactionTemplate(
    var type: Type? = null,

    var date: List<Int> = ArrayList(),

    var amount: Double? = null,

    var manuallyReversed: Boolean? = null,

    var possibleNextRepaymentDate: List<Int> = ArrayList(),

    var paymentTypeOptions: List<com.mifos.core.objects.PaymentTypeOption> = ArrayList(),
) : Parcelable
