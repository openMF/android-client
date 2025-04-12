package com.mifos.core.network.apis

import com.mifos.core.network.model.GetOfficesResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query

interface OfficesApi {

    /**
     * List Offices
     * Example Requests:  offices   offices?fields&#x3D;id,name,openingDate
     * Responses:
     *  - 200: OK
     *
     * @param includeAllOffices includeAllOffices (optional, default to false)
     * @param orderBy orderBy (optional)
     * @param sortOrder sortOrder (optional)
     * @return [kotlin.collections.List<GetOfficesResponse]
     */
    @GET("v1/offices")
    suspend fun retrieveOffices(
        @Query("includeAllOffices") includeAllOffices: Boolean? = false,
        @Query("orderBy") orderBy: String? = null,
        @Query("sortOrder") sortOrder: String? = null,
    ): List<GetOfficesResponse>
}