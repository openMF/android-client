package com.mifos.room.entities.accounts.recurring

import kotlinx.serialization.Serializable

@Serializable
data class AccountChart(
    val accountId: Int? = null,
    val accountNumber: String? = null,
    val chartSlabs: List<ChartSlab>? = null,
    val endDate: List<Int>? = null,
    val fromDate: List<Int>? = null,
    val id: Int? = null,
    val isPrimaryGroupingByAmount: Boolean? = null,
    val name: String? = null,
    val periodTypes: List<PeriodType>? = null,
)