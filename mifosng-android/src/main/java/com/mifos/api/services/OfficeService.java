/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.api.services;

import com.mifos.objects.organisation.Office;
import com.mifos.api.model.APIEndPoint;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import rx.Observable;

/**
 * @author fomenkoo
 */
public interface OfficeService {
    /**
     * Fetches List of All the Offices
     *
     * @param
     */
    @GET(APIEndPoint.OFFICES)
    Observable<List<Office>> getAllOffices();
}
