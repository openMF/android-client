package com.mifos.room.entities.accounts.recurring

import kotlinx.serialization.Serializable

@Serializable
data class ChartSlab(
    val amountRangeFrom: Double? = null,
    val annualInterestRate: Double? = null,
    val currency: Currency? = null,
    val description: String? = null,
    val fromPeriod: Int? = null,
    val id: Int? = null,
    val incentives: List<Incentive>? = null,
    val periodType: PeriodType? = null,
)