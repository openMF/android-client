/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.model.objects.account.loan.reschedules

import kotlinx.serialization.Serializable

@Serializable
data class RescheduleReasonOption(
    val id: Int,
    val name: String,
    val active: Boolean? = null,
    val mandatory: Boolean? = null,
)
