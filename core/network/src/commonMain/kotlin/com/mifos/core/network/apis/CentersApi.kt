/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.apis

import com.mifos.core.network.model.GetCentersResponse
import com.mifos.core.network.model.PostCentersCenterIdRequest
import com.mifos.core.network.model.PostCentersCenterIdResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

interface CentersApi {

    /**
     * List Centers
     * The default implementation supports pagination and sorting with the default pagination size set to 200 records. The parameter limit with description -1 will return all entries.  Example Requests:    centers    centers?fields&#x3D;name,officeName,joinedDate    centers?offset&#x3D;10&amp;limit&#x3D;50    centers?orderBy&#x3D;name&amp;sortOrder&#x3D;DESC
     * Responses:
     *  - 200: OK
     *
     * @param officeId officeId (optional)
     * @param staffId staffId (optional)
     * @param externalId externalId (optional)
     * @param name name (optional)
     * @param underHierarchy underHierarchy (optional)
     * @param paged paged (optional)
     * @param offset offset (optional)
     * @param limit limit (optional)
     * @param orderBy orderBy (optional)
     * @param sortOrder sortOrder (optional)
     * @param meetingDate meetingDate (optional)
     * @param dateFormat dateFormat (optional)
     * @param locale locale (optional)
     * @return [GetCentersResponse]
     */
    @GET("centers")
    suspend fun retrieveAll23(
        @Query("officeId") officeId: Long? = null,
        @Query("staffId") staffId: Long? = null,
        @Query("externalId") externalId: String? = null,
        @Query("name") name: String? = null,
        @Query("underHierarchy") underHierarchy: String? = null,
        @Query("paged") paged: Boolean? = null,
        @Query("offset") offset: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("orderBy") orderBy: String? = null,
        @Query("sortOrder") sortOrder: String? = null,
        @Query("meetingDate") meetingDate: String? = null,
        @Query("dateFormat") dateFormat: String? = null,
        @Query("locale") locale: String? = null,
    ): GetCentersResponse

    /**
     * Activate a Center | Generate Collection Sheet | Save Collection Sheet | Close a Center | Associate Groups | Disassociate Groups
     * Activate a Center:  Centers can be created in a Pending state. This API exists to enable center activation. If the center happens to be already active, this API will result in an error.  Close a Center:  Centers can be closed if they don&#39;t have any non-closed groups or saving accounts. If the Center has any active groups or savings accounts, this API will result in an error.  Associate Groups:  This API allows associating existing groups to a center. The groups are listed from the office to which the center is associated. If group(s) is already associated with a center, this API will result in an error.  Disassociate Groups:  This API allows to disassociate groups from a center.  Generate Collection Sheet:  This Api retrieves repayment details of all jlg loans under a center as on a specified meeting date.  Save Collection Sheet:  This Api allows the loan officer to perform bulk repayments of JLG loans for a center on a given meeting date.  Showing Request/Response for Close a Center
     * Responses:
     *  - 200: OK
     *
     * @param centerId centerId
     * @param postCentersCenterIdRequest
     * @param command command (optional)
     * @return [PostCentersCenterIdResponse]
     */
    @POST("centers/{centerId}")
    suspend fun activate2(
        @Path("centerId") centerId: Long,
        @Body postCentersCenterIdRequest: PostCentersCenterIdRequest,
        @Query("command") command: String? = null,
    ): PostCentersCenterIdResponse
}
