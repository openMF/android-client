package com.mifos.api.services;

import com.mifos.api.model.APIEndPoint;
import com.mifos.objects.group.CenterInfo;
import com.mifos.objects.runreports.FullParameterListResponse;
import com.mifos.objects.runreports.client.ClientReportTypeItem;

import java.util.List;
import java.util.Map;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by Rajan Maurya on 05/02/17.
 */
public interface RunReportsService {

    /**
     * Endpoint to fetch Categories of the reports
     * @param category Client/Savings etc.
     * @param genericResultSet genericResultSet - true/false
     * @param parameterType parameterType - true/false
     * @return List of ClientReportTypeItem
     */
    @GET(APIEndPoint.RUNREPORTS + "/reportCategoryList")
    Observable<List<ClientReportTypeItem>> getReportCategories(
            @Query("R_reportCategory") String category,
            @Query("genericResultSet") boolean genericResultSet,
            @Query("parameterType") boolean parameterType
    );

    /**
     * Endpoint to fetch FullParameter list after fetching the categories.
     * @param reportName Report's Name for whom the list has to be retrieved
     * @param parameterType parameterType - true/false
     * @return FullParameterListResponse
     */

    @GET(APIEndPoint.RUNREPORTS + "/FullParameterList")
    Observable<FullParameterListResponse> getReportFullParameterList(
            @Query("R_reportListing") String reportName,
            @Query("parameterType") boolean parameterType
    );

    /**
     * Endpoint to fetch the details for a particular parameter.
     * @param parameterName Parameter Name
     * @param parameterType parameterType - true/false
     * @return
     */

    @GET(APIEndPoint.RUNREPORTS + "/{path}")
    Observable<FullParameterListResponse> getReportParameterDetails(
            @Path("path") String parameterName,
            @Query("parameterType") boolean parameterType
    );

    @GET(APIEndPoint.RUNREPORTS + "/{path}")
    Observable<FullParameterListResponse> getReportOffice(
            @Path("path") String parameterName,
            @Query("R_officeId") int office,
            @Query("parameterType") boolean parameterType
    );

    @GET(APIEndPoint.RUNREPORTS + "/{path}")
    Observable<FullParameterListResponse> getReportProduct(
            @Path("path") String parameterName,
            @Query("R_currencyId") String currency,
            @Query("parameterType") boolean parameterType
    );
    /**
     * Endpoint to retrieve final Report based on the parameters.
     * @param reportName Report's Name
     * @param options Map of the queries with their corresponding value.
     * @return
     */

    @GET(APIEndPoint.RUNREPORTS + "/{path}")
    Observable<FullParameterListResponse> getRunReportWithQuery(
            @Path("path") String reportName,
            @QueryMap Map<String, String> options
            );

    @GET(APIEndPoint.RUNREPORTS + "/GroupSummaryCounts")
    Observable<List<CenterInfo>> getCenterSummaryInfo(
            @Query("R_groupId") int centerId,
            @Query("genericResultSet") boolean genericResultSet);
}
