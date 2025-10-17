package com.mifos.core.model.objects.template.recurring.incentive

import com.mifos.core.model.objects.template.recurring.AttributeName
import com.mifos.core.model.objects.template.recurring.ConditionType
import com.mifos.core.model.objects.template.recurring.EntityType
import com.mifos.core.model.utils.IgnoredOnParcel
import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Incentive(
    val amount: Double? = null,
    @IgnoredOnParcel val attributeName: AttributeName? = null,
    val attributeValue: String? = null,
    val attributeValueDesc: String? = null,
    @IgnoredOnParcel val conditionType: ConditionType? = null,
    @IgnoredOnParcel val entityType: EntityType? = null,
    @IgnoredOnParcel val incentiveType: IncentiveType? = null,
) : Parcelable