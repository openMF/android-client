package com.mifos.core.model.objects.clients

import kotlinx.serialization.Serializable

@Serializable
data class AssignStaffRequest(
    val staffId: Int
)