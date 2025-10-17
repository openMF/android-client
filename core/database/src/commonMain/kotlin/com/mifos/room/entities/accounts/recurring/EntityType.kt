package com.mifos.room.entities.accounts.recurring

import kotlinx.serialization.Serializable

@Serializable
data class EntityType(
    val code: String? = null,
    val id: Int? = null,
    val value: String? = null,
)