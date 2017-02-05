/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.api.services;

import com.mifos.api.model.APIEndPoint;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;


public interface RunreportsService {

    @GET(APIEndPoint.RUNREPORTS + "/GroupSummaryCounts")
    Observable<ArrayList<Map<String, Integer>>> getCenterSummaryInfo(
            @Query("R_groupId") int centerId,
            @Query("genericResultSet") boolean genericResultSet);
}
