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
import com.mifos.core.model.utils.IgnoredOnParcel
import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class AccountChart(
    @IgnoredOnParcel val attributeNameOptions: List<AttributeNameOption>? = null,
    @IgnoredOnParcel val chartSlabs: List<ChartSlab>? = null,
//    val clientClassificationOptions: List<Any?>? = null,
    @IgnoredOnParcel val clientTypeOptions: List<ClientTypeOption>? = null,
    @IgnoredOnParcel val conditionTypeOptions: List<ConditionTypeOption>? = null,
    val endDate: List<Int>? = null,
    @IgnoredOnParcel val entityTypeOptions: List<EntityTypeOption>? = null,
    val fromDate: List<Int>? = null,
//    val genderOptions: List<Any?>? = null,
    @IgnoredOnParcel val incentiveTypeOptions: List<IncentiveTypeOption>? = null,
    val isPrimaryGroupingByAmount: Boolean? = null,
    val name: String? = null,
    val description: String? = null,
    @IgnoredOnParcel val periodTypes: List<PeriodType>? = null,
) : Parcelable
