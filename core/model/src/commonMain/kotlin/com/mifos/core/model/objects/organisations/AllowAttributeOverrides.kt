/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.model.objects.organisations

import com.mifos.core.common.utils.Parcelable
import com.mifos.core.common.utils.Parcelize
import kotlinx.serialization.Serializable

/**
 * Created by Rajan Maurya on 15/07/16.
 */
@Parcelize
@Serializable
data class AllowAttributeOverrides(
    var amortizationType: Boolean? = null,

    var interestType: Boolean? = null,

    var transactionProcessingStrategyId: Boolean? = null,

    var interestCalculationPeriodType: Boolean? = null,

    var inArrearsTolerance: Boolean? = null,

    var repaymentEvery: Boolean? = null,

    var graceOnPrincipalAndInterestPayment: Boolean? = null,

    var graceOnArrearsAgeing: Boolean? = null,
) : Parcelable
