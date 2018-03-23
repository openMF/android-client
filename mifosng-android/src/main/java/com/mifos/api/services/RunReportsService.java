package com.mifos.api.services;

import com.mifos.api.model.APIEndPoint;
import com.mifos.objects.group.CenterInfo;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Rajan Maurya on 05/02/17.
 */
public interface RunReportsService {

    @GET(APIEndPoint.RUNREPORTS + "/GroupSummaryCounts")
    Observable<List<CenterInfo>> getCenterSummaryInfo(
            @Query("R_groupId") int centerId,
            @Query("genericResultSet") boolean genericResultSet);
}
