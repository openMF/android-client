package com.mifos.core.network.model

import kotlinx.serialization.Serializable

/**
 * GetCentersResponse
 *
 * @param pageItems
 * @param totalFilteredRecords
 */

@Serializable
data class GetCentersResponse(

    val pageItems: kotlin.collections.Set<GetCentersPageItems>? = null,

    val totalFilteredRecords: kotlin.Int? = null,

    )