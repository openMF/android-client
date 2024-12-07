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

import com.mifos.core.model.APIEndPoint
import com.mifos.core.objects.organisation.Office
import retrofit2.http.GET

/**
 * @author fomenkoo
 */
interface OfficeService {
    /**
     * Fetches List of All the Offices
     *
     * @param listOfOfficesCallback
     */
    @GET(APIEndPoint.OFFICES)
    suspend fun allOffices(): List<Office>
}
