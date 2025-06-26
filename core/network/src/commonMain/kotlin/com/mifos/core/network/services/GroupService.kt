/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.services

import com.mifos.core.common.utils.Page
import com.mifos.core.model.objects.clients.ActivatePayload
import com.mifos.core.model.objects.responses.SaveResponse
import com.mifos.room.basemodel.APIEndPoint
import com.mifos.room.entities.accounts.GroupAccounts
import com.mifos.room.entities.group.GroupEntity
import com.mifos.room.entities.group.GroupPayloadEntity
import com.mifos.room.entities.group.GroupWithAssociations
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.http.QueryMap
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow

/**
 * @author fomenkoo
 */
interface GroupService {
    @GET(APIEndPoint.GROUPS)
    fun getGroups(
        @Query("paged") b: Boolean,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
    ): Flow<Page<GroupEntity>>

    @GET(APIEndPoint.GROUPS + "/{groupId}?associations=all")
    fun getGroupWithAssociations(@Path("groupId") groupId: Int): Flow<GroupWithAssociations>

    @GET(APIEndPoint.GROUPS)
    fun getAllGroupsInOffice(
        @Query("officeId") officeId: Int,
        @QueryMap params: Map<String, String>,
    ): Flow<List<GroupEntity>>

    @POST(APIEndPoint.GROUPS)
    suspend fun createGroup(@Body groupPayload: GroupPayloadEntity?): SaveResponse

    @GET(APIEndPoint.GROUPS + "/{groupId}")
    suspend fun getGroup(@Path("groupId") groupId: Int): GroupEntity

    @GET(APIEndPoint.GROUPS + "/{groupId}/accounts")
    suspend fun getGroupAccounts(@Path("groupId") groupId: Int): GroupAccounts

    /**
     * This is the service to activate the Group
     * REST ENT POINT
     * https://demo.openmf.org/fineract-provider/api/v1/groups/{groupId}?command=activate
     *
     * @param groupId
     * @return GenericResponse
     */
    @POST(APIEndPoint.GROUPS + "/{groupId}?command=activate")
    suspend fun activateGroup(
        @Path("groupId") groupId: Int,
        @Body activatePayload: ActivatePayload,
    ): HttpResponse
}
