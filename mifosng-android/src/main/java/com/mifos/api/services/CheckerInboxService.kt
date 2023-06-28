package com.mifos.api.services

import com.mifos.api.GenericResponse
import com.mifos.api.model.APIEndPoint
import com.mifos.objects.CheckerTask
import com.mifos.objects.checkerinboxandtasks.CheckerInboxSearchTemplate
import com.mifos.objects.checkerinboxandtasks.RescheduleLoansTask
import retrofit2.http.*
import rx.Observable

interface CheckerInboxService {

    @GET(APIEndPoint.MAKER_CHECKER)
    fun getCheckerList(@Query("actionName") actionName: String? = null,
                       @Query("entityName") entityName: String? = null,
                       @Query("resourceId") resourceId: Int? = null)
            : Observable<List<CheckerTask>>

    @POST(APIEndPoint.MAKER_CHECKER + "/{auditId}?command=approve")
    fun approveCheckerEntry(@Path("auditId") auditId: Int): Observable<GenericResponse>

    @POST(APIEndPoint.MAKER_CHECKER + "/{auditId}?command=reject")
    fun rejectCheckerEntry(@Path("auditId") auditId: Int): Observable<GenericResponse>

    @DELETE(APIEndPoint.MAKER_CHECKER + "/{auditId}")
    fun deleteCheckerEntry(@Path("auditId") auditId: Int): Observable<GenericResponse>

    @GET("rescheduleloans?command=pending")
    fun getRescheduleLoansTaskList(): Observable<List<RescheduleLoansTask>>

    @GET(APIEndPoint.MAKER_CHECKER + "/searchtemplate?fields=entityNames,actionNames")
    fun getCheckerInboxSearchTempalate(): Observable<CheckerInboxSearchTemplate>

    @GET(APIEndPoint.MAKER_CHECKER)
    fun getCheckerTasksFromResourceId(@Query("actionName") actionName: String? = null,
                                      @Query("entityName") entityName: String? = null,
                                      @Query("resourceId") resourceId: Int? = null)
            : Observable<List<CheckerTask>>

}