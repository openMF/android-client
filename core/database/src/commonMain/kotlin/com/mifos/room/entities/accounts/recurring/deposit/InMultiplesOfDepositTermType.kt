package com.mifos.room.entities.accounts.recurring.deposit

import kotlinx.serialization.Serializable

@Serializable
data class InMultiplesOfDepositTermType(
    val code: String? = null,
    val id: Int? = null,
    val value: String? = null,
)