/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.api.services

import com.mifos.api.model.APIEndPoint
import com.mifos.objects.organisation.Staff
import retrofit2.http.GET
import retrofit2.http.Query
import rx.Observable

/**
 * @author fomenkoo
 */
interface StaffService {
    @GET(APIEndPoint.STAFF + "?status=all")
    fun getStaffForOffice(@Query("officeId") officeId: Int): Observable<List<Staff>>

    @get:GET(APIEndPoint.STAFF)
    val allStaff: Observable<List<Staff>>

    @get:GET(APIEndPoint.STAFF + "?isLoanOfficer=true")
    val fieldStaffForOffice: Observable<List<Staff>>
}