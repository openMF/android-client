package com.mifos.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class StaffOption(
    val id: Int,
    val displayName: String
)