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
    @IgnoredOnParcel val periodTypes: List<PeriodType>? = null,
) : Parcelable