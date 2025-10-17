package com.mifos.core.model.objects.template.recurring

import com.mifos.core.model.objects.template.recurring.incentive.Incentive
import com.mifos.core.model.objects.template.recurring.period.PeriodType
import com.mifos.core.model.utils.IgnoredOnParcel
import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class ChartSlab(
    val amountRangeFrom: Double? = null,
    val annualInterestRate: Double? = null,
    val currency: Currency? = null,
    val description: String? = null,
    val fromPeriod: Int? = null,
    @IgnoredOnParcel val incentives: List<Incentive>? = null,
    @IgnoredOnParcel val periodType: PeriodType? = null,
) : Parcelable