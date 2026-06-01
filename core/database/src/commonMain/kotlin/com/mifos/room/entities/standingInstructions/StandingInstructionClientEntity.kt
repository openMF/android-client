package com.mifos.room.entities.standingInstructions

import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class StandingInstructionClientEntity(
    val id: Int? = null,
    val displayName: String? = null,
) : Parcelable