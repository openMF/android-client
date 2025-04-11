package com.mifos.core.network.model

import kotlinx.serialization.Serializable

/**
 * PostCentersCenterIdRequest
 *
 * @param closureDate
 * @param closureReasonId
 * @param dateFormat
 * @param locale
 */

@Serializable
data class PostCentersCenterIdRequest(

    val closureDate: kotlin.String? = null,

    val closureReasonId: kotlin.Long? = null,

    val dateFormat: kotlin.String? = null,

    val locale: kotlin.String? = null,

    )