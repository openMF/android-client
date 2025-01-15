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

import com.mifos.core.entity.group.Group
import com.mifos.core.entity.group.GroupPayload
import com.mifos.core.entity.group.GroupWithAssociations
import com.mifos.core.model.APIEndPoint
import com.mifos.core.network.GenericResponse
import com.mifos.core.objects.clients.ActivatePayload
import com.mifos.core.objects.clients.Page
import com.mifos.core.objects.responses.SaveResponse
import com.mifos.room.entities.accounts.GroupAccounts
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap
import rx.Observable

/**
 * @author fomenkoo
 */
interface GroupService {
    @GET(APIEndPoint.GROUPS)
    fun getGroups(
        @Query("paged") b: Boolean,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
    ): Observable<Page<Group>>

    @GET(APIEndPoint.GROUPS + "/{groupId}?associations=all")
    fun getGroupWithAssociations(@Path("groupId") groupId: Int): Observable<GroupWithAssociations>

    @GET(APIEndPoint.GROUPS)
    suspend fun getAllGroupsInOffice(
        @Query("officeId") officeId: Int,
        @QueryMap params: Map<String, String>,
    ): List<Group>

    @POST(APIEndPoint.GROUPS)
    fun createGroup(@Body groupPayload: GroupPayload?): Observable<SaveResponse>

    @GET(APIEndPoint.GROUPS + "/{groupId}")
    fun getGroup(@Path("groupId") groupId: Int): Observable<Group>

    @GET(APIEndPoint.GROUPS + "/{groupId}/accounts")
    fun getGroupAccounts(@Path("groupId") groupId: Int): Observable<GroupAccounts>

    /**
     * This is the service to activate the Group
     * REST ENT POINT
     * https://demo.openmf.org/fineract-provider/api/v1/groups/{groupId}?command=activate
     *
     * @param groupId
     * @return GenericResponse
     */
    @POST(APIEndPoint.GROUPS + "/{groupId}?command=activate")
    fun activateGroup(
        @Path("groupId") groupId: Int,
        @Body activatePayload: ActivatePayload?,
    ): Observable<GenericResponse>
}
