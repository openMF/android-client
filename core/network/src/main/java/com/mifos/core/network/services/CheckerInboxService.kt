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

import com.mifos.core.network.GenericResponse
import com.mifos.core.objects.checkerinboxtask.CheckerInboxSearchTemplate
import com.mifos.core.objects.checkerinboxtask.CheckerTask
import com.mifos.core.objects.checkerinboxtask.RescheduleLoansTask
import com.mifos.room.basemodel.APIEndPoint
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import rx.Observable

interface CheckerInboxService {

    @GET(APIEndPoint.MAKER_CHECKER)
    suspend fun getCheckerList(
        @Query("actionName") actionName: String? = null,
        @Query("entityName") entityName: String? = null,
        @Query("resourceId") resourceId: Int? = null,
    ): List<CheckerTask>

    @POST(APIEndPoint.MAKER_CHECKER + "/{auditId}?command=approve")
    suspend fun approveCheckerEntry(@Path("auditId") auditId: Int): GenericResponse

    @POST(APIEndPoint.MAKER_CHECKER + "/{auditId}?command=reject")
    suspend fun rejectCheckerEntry(@Path("auditId") auditId: Int): GenericResponse

    @DELETE(APIEndPoint.MAKER_CHECKER + "/{auditId}")
    suspend fun deleteCheckerEntry(@Path("auditId") auditId: Int): GenericResponse

    @GET("rescheduleloans?command=pending")
    suspend fun getRescheduleLoansTaskList(): List<RescheduleLoansTask>

    @GET(APIEndPoint.MAKER_CHECKER + "/searchtemplate?fields=entityNames,actionNames")
    fun getCheckerInboxSearchTempalate(): Observable<CheckerInboxSearchTemplate>

    @GET(APIEndPoint.MAKER_CHECKER)
    fun getCheckerTasksFromResourceId(
        @Query("actionName") actionName: String? = null,
        @Query("entityName") entityName: String? = null,
        @Query("resourceId") resourceId: Int? = null,
    ): Observable<List<CheckerTask>>
}
