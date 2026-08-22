/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.model.objects.template.recurring

import com.mifos.core.model.objects.template.recurring.incentive.IncentiveTypeOption
import com.mifos.core.model.objects.template.recurring.period.PeriodType
import kotlinx.serialization.Serializable

@Serializable
data class AccountChart(
    val attributeNameOptions: List<AttributeNameOption>? = null,
    val chartSlabs: List<ChartSlab>? = null,
//    val clientClassificationOptions: List<Any?>? = null,
    val clientTypeOptions: List<ClientTypeOption>? = null,
    val conditionTypeOptions: List<ConditionTypeOption>? = null,
    val endDate: List<Int>? = null,
    val entityTypeOptions: List<EntityTypeOption>? = null,
    val fromDate: List<Int>? = null,
//    val genderOptions: List<Any?>? = null,
    val incentiveTypeOptions: List<IncentiveTypeOption>? = null,
    val isPrimaryGroupingByAmount: Boolean? = null,
    val name: String? = null,
    val description: String? = null,
    val periodTypes: List<PeriodType>? = null,
)
