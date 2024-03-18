/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.services

import com.mifos.core.model.APIEndPoint
import com.mifos.core.objects.organisation.Office
import retrofit2.http.GET
import rx.Observable

/**
 * @author fomenkoo
 */
interface OfficeService {
    /**
     * Fetches List of All the Offices
     *
     * @param listOfOfficesCallback
     */
    @get:GET(APIEndPoint.OFFICES)
    val allOffices: Observable<List<Office>>
}