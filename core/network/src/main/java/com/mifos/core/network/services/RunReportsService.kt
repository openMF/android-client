package com.mifos.core.network.services

import com.mifos.core.model.APIEndPoint
import com.mifos.core.objects.group.CenterInfo
import com.mifos.core.objects.runreports.FullParameterListResponse
import com.mifos.core.objects.runreports.client.ClientReportTypeItem
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap
import rx.Observable

/**
 * Created by Rajan Maurya on 05/02/17.
 */
interface RunReportsService {
    /**
     * Endpoint to fetch Categories of the reports
     * @param category Client/Savings etc.
     * @param genericResultSet genericResultSet - true/false
     * @param parameterType parameterType - true/false
     * @return List of ClientReportTypeItem
     */
    @GET(APIEndPoint.RUN_REPORTS + "/reportCategoryList")
    fun getReportCategories(
        @Query("R_reportCategory") category: String?,
        @Query("genericResultSet") genericResultSet: Boolean,
        @Query("parameterType") parameterType: Boolean
    ): Observable<List<ClientReportTypeItem>>

    /**
     * Endpoint to fetch FullParameter list after fetching the categories.
     * @param reportName Report's Name for whom the list has to be retrieved
     * @param parameterType parameterType - true/false
     * @return FullParameterListResponse
     */
    @GET(APIEndPoint.RUN_REPORTS + "/FullParameterList")
    fun getReportFullParameterList(
        @Query("R_reportListing") reportName: String?,
        @Query("parameterType") parameterType: Boolean
    ): Observable<FullParameterListResponse>

    /**
     * Endpoint to fetch the details for a particular parameter.
     * @param parameterName Parameter Name
     * @param parameterType parameterType - true/false
     * @return
     */
    @GET(APIEndPoint.RUN_REPORTS + "/{path}")
    fun getReportParameterDetails(
        @Path("path") parameterName: String?,
        @Query("parameterType") parameterType: Boolean
    ): Observable<FullParameterListResponse>

    @GET(APIEndPoint.RUN_REPORTS + "/{path}")
    fun getReportOffice(
        @Path("path") parameterName: String?,
        @Query("R_officeId") office: Int,
        @Query("parameterType") parameterType: Boolean
    ): Observable<FullParameterListResponse>

    @GET(APIEndPoint.RUN_REPORTS + "/{path}")
    fun getReportProduct(
        @Path("path") parameterName: String?,
        @Query("R_currencyId") currency: String?,
        @Query("parameterType") parameterType: Boolean
    ): Observable<FullParameterListResponse>

    /**
     * Endpoint to retrieve final Report based on the parameters.
     * @param reportName Report's Name
     * @param options Map of the queries with their corresponding value.
     * @return
     */
    @GET(APIEndPoint.RUN_REPORTS + "/{path}")
    fun getRunReportWithQuery(
        @Path("path") reportName: String?,
        @QueryMap options: Map<String?, String?>?
    ): Observable<FullParameterListResponse>

    @GET(APIEndPoint.RUN_REPORTS + "/GroupSummaryCounts")
    fun getCenterSummaryInfo(
        @Query("R_groupId") centerId: Int,
        @Query("genericResultSet") genericResultSet: Boolean
    ): Observable<List<CenterInfo>>
}