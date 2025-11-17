package com.mifos.core.network.model

import com.mifos.core.model.objects.account.saving.FieldOfficerOptions
import com.mifos.core.model.utils.IgnoredOnParcel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable



@Serializable
data class FixedDepositTemplate(
    @SerialName("clientId")
    val clientId: Int? = null,

    @SerialName(value = "clientName")
    val clientName: String? = null,

    @SerialName("productOptions")
    val productOptions: List<FixedDepositProductOption>? = null,

    @SerialName("fieldOfficerOptions")
    val fieldOfficerOptions: List<FieldOfficerOptions>? = null,

)