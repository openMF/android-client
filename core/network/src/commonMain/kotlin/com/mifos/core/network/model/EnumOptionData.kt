package com.mifos.core.network.model

import kotlinx.serialization.Serializable

/**
 *
 *
 * @param code
 * @param id
 * @param `value`
 */

@Serializable
data class EnumOptionData(

    val code: String? = null,

    val id: Long? = null,

    val value: String? = null,
)