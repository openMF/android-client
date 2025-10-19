/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.accounts.recurring

import kotlinx.serialization.Serializable

@Serializable
data class Incentive(
    val amount: Double? = null,
    val attributeName: AttributeName? = null,
    val attributeValue: String? = null,
    val attributeValueDesc: String? = null,
    val conditionType: ConditionType? = null,
    val entityType: EntityType? = null,
    val id: Int? = null,
    val incentiveType: IncentiveType? = null,
)
