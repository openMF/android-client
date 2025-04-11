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

import com.mifos.room.basemodel.APIEndPoint
import com.mifos.room.entities.organisation.StaffEntity
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query
import kotlinx.coroutines.flow.Flow

/**
 * @author fomenkoo
 */
interface StaffService {
    @GET(APIEndPoint.STAFF + "?status=all")
    fun getStaffForOffice(@Query("officeId") officeId: Int): Flow<List<StaffEntity>>

    @GET(APIEndPoint.STAFF)
    fun allStaff(): Flow<List<StaffEntity>>

    @GET(APIEndPoint.STAFF + "?isLoanOfficer=true")
    fun fieldStaffForOffice(): Flow<List<StaffEntity>>
}
