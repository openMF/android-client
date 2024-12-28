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
import com.mifos.core.objects.groups.CenterInfo
import com.mifos.core.objects.runreport.FullParameterListResponse
import com.mifos.core.objects.runreport.client.ClientReportTypeItem
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

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
    suspend fun getReportCategories(
        @Query("R_reportCategory") category: String?,
        @Query("genericResultSet") genericResultSet: Boolean,
        @Query("parameterType") parameterType: Boolean,
    ): List<ClientReportTypeItem>

    /**
     * Endpoint to fetch FullParameter list after fetching the categories.
     * @param reportName Report's Name for whom the list has to be retrieved
     * @param parameterType parameterType - true/false
     * @return FullParameterListResponse
     */
    @GET(APIEndPoint.RUN_REPORTS + "/FullParameterList")
    suspend fun getReportFullParameterList(
        @Query("R_reportListing") reportName: String,
        @Query("parameterType") parameterType: Boolean,
    ): FullParameterListResponse

    /**
     * Endpoint to fetch the details for a particular parameter.
     * @param parameterName Parameter Name
     * @param parameterType parameterType - true/false
     * @return
     */
    @GET(APIEndPoint.RUN_REPORTS + "/{path}")
    suspend fun getReportParameterDetails(
        @Path("path") parameterName: String,
        @Query("parameterType") parameterType: Boolean,
    ): FullParameterListResponse

    @GET(APIEndPoint.RUN_REPORTS + "/{path}")
    suspend fun getReportOffice(
        @Path("path") parameterName: String,
        @Query("R_officeId") office: Int,
        @Query("parameterType") parameterType: Boolean,
    ): FullParameterListResponse

    @GET(APIEndPoint.RUN_REPORTS + "/{path}")
    suspend fun getReportProduct(
        @Path("path") parameterName: String,
        @Query("R_currencyId") currency: String,
        @Query("parameterType") parameterType: Boolean,
    ): FullParameterListResponse

    /**
     * Endpoint to retrieve final Report based on the parameters.
     * @param reportName Report's Name
     * @param options Map of the queries with their corresponding value.
     * @return
     */
    @GET(APIEndPoint.RUN_REPORTS + "/{path}")
    suspend fun getRunReportWithQuery(
        @Path("path") reportName: String,
        @QueryMap options: Map<String, String>,
    ): FullParameterListResponse

    @GET(APIEndPoint.RUN_REPORTS + "/GroupSummaryCounts")
    suspend fun getCenterSummaryInfo(
        @Query("R_groupId") centerId: Int,
        @Query("genericResultSet") genericResultSet: Boolean,
    ): List<CenterInfo>
}
