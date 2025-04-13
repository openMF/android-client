/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.model

import kotlinx.serialization.Serializable

/**
 *
 *
 * @param code
 * @param decimalPlaces
 * @param displayLabel
 * @param displaySymbol
 * @param name
 * @param nameCode
 */

@Serializable
data class GetClientsSavingsAccountsCurrency(

    val code: kotlin.String? = null,

    val decimalPlaces: kotlin.Int? = null,

    val displayLabel: kotlin.String? = null,

    val displaySymbol: kotlin.String? = null,

    val name: kotlin.String? = null,

    val nameCode: kotlin.String? = null,

)
