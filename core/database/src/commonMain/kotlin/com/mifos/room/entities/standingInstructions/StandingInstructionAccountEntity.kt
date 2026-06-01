package com.mifos.room.entities.standingInstructions

import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class StandingInstructionAccountEntity(
    val id: Int? = null,
    val accountNo: String? = null,
    val productName: String? = null
) : Parcelable