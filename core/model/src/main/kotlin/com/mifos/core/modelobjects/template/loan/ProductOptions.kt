/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.modelobjects.template.loan

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 16/07/16.
 */
@Parcelize
data class ProductOptions(
    var id: Int? = null,

    var name: String? = null,

    var includeInBorrowerCycle: Boolean? = null,

    var useBorrowerCycle: Boolean? = null,

    var isLinkedToFloatingInterestRates: Boolean? = null,

    var isFloatingInterestRateCalculationAllowed: Boolean? = null,

    var allowVariableInstallments: Boolean? = null,

    var isInterestRecalculationEnabled: Boolean? = null,

    var canDefineInstallmentAmount: Boolean? = null,

    var holdGuaranteeFunds: Boolean? = null,

    var accountMovesOutOfNPAOnlyOnArrearsCompletion: Boolean? = null,
) : Parcelable
