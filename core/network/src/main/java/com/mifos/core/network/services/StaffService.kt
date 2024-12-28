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

import com.mifos.core.dbobjects.organisation.Staff
import com.mifos.core.model.APIEndPoint
import retrofit2.http.GET
import retrofit2.http.Query
import rx.Observable

/**
 * @author fomenkoo
 */
interface StaffService {
    @GET(APIEndPoint.STAFF + "?status=all")
    suspend fun getStaffForOffice(@Query("officeId") officeId: Int): List<Staff>

    @get:GET(APIEndPoint.STAFF)
    val allStaff: Observable<List<Staff>>

    @get:GET(APIEndPoint.STAFF + "?isLoanOfficer=true")
    val fieldStaffForOffice: Observable<List<Staff>>
}
