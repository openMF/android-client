package com.mifos.room.entities.accounts.recurring

import kotlinx.serialization.Serializable

@Serializable
data class Summary(
    val accountBalance: Double? = null,
    val currency: Currency? = null,
)