package com.mifos.room.entities.accounts.recurring.interest

import kotlinx.serialization.Serializable


@Serializable
data class InterestCalculationType(
    val code: String? = null,
    val id: Int? = null,
    val value: String? = null,
)