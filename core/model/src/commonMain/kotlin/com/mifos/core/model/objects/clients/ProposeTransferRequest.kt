package com.mifos.core.model.objects.clients

import kotlinx.serialization.Serializable

@Serializable
data class ProposeTransferRequest(
    val destinationOfficeId: Int,
    val transferDate: String,
    val note: String,
    val dateFormat: String,
    val locale: String,
)