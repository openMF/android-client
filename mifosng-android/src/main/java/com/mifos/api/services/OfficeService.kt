/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.api.services

import com.mifos.api.model.APIEndPoint
import com.mifos.objects.organisation.Office
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