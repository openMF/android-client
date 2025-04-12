package com.mifos.core.network.model

import kotlinx.serialization.Serializable

/**
 * GetGroupsResponse
 *
 * @param pageItems
 * @param totalFilteredRecords
 */

@Serializable
data class GetGroupsResponse(

    val pageItems: kotlin.collections.Set<GetGroupsPageItems>? = null,

    val totalFilteredRecords: kotlin.Int? = null,

    )