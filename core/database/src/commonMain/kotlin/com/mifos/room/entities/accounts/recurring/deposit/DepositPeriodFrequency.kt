package com.mifos.room.entities.accounts.recurring.deposit

import kotlinx.serialization.Serializable

@Serializable
data class DepositPeriodFrequency(
    val code: String,
    val id: Int,
    val value: String
)