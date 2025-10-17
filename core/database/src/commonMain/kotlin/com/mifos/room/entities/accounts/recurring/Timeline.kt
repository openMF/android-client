package com.mifos.room.entities.accounts.recurring

import kotlinx.serialization.Serializable

@Serializable
data class Timeline(
    val submittedByFirstname: String? = null,
    val submittedByLastname: String? = null,
    val submittedByUsername: String? = null,
    val submittedOnDate: List<Int>? = null,
)