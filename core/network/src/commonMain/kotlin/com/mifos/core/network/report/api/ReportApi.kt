/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.network.report.api

import com.mifos.core.model.objects.groups.CenterInfo
import com.mifos.core.model.objects.runreport.FullParameterListResponse
import com.mifos.core.model.objects.runreport.client.ClientReportTypeItem
import com.mifos.core.network.APIEndPoint
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.http.QueryMap

/** Fineract `report` (runreports) domain endpoints. */
interface ReportApi {

    /** Retrieve report categories (`Client`, `Savings`, etc.). */
    @GET(APIEndPoint.RUN_REPORTS + "/reportCategoryList")
    suspend fun getReportCategories(
        @Query("R_reportCategory") category: String?,
        @Query("genericResultSet") genericResultSet: Boolean,
        @Query("parameterType") parameterType: Boolean,
    ): List<ClientReportTypeItem>

    /** Retrieve the full parameter list for a given report. */
    @GET(APIEndPoint.RUN_REPORTS + "/FullParameterList")
    suspend fun getReportFullParameterList(
        @Query("R_reportListing") reportName: String,
        @Query("parameterType") parameterType: Boolean,
    ): FullParameterListResponse

    /** Retrieve details for a specific parameter. */
    @GET(APIEndPoint.RUN_REPORTS + "/{path}")
    suspend fun getReportParameterDetails(
        @Path("path") parameterName: String,
        @Query("parameterType") parameterType: Boolean,
    ): FullParameterListResponse

    /** Resolve a parameter scoped to a specific office. */
    @GET(APIEndPoint.RUN_REPORTS + "/{path}")
    suspend fun getReportOffice(
        @Path("path") parameterName: String,
        @Query("R_officeId") office: Int,
        @Query("parameterType") parameterType: Boolean,
    ): FullParameterListResponse

    /** Resolve a parameter scoped to a specific currency. */
    @GET(APIEndPoint.RUN_REPORTS + "/{path}")
    suspend fun getReportProduct(
        @Path("path") parameterName: String,
        @Query("R_currencyId") currency: String,
        @Query("parameterType") parameterType: Boolean,
    ): FullParameterListResponse

    /** Execute a report with arbitrary query parameters. */
    @GET(APIEndPoint.RUN_REPORTS + "/{path}")
    suspend fun runReport(
        @Path("path") reportName: String,
        @QueryMap options: Map<String, String>,
    ): FullParameterListResponse

    /** Retrieve aggregated counts for a center/group. */
    @GET(APIEndPoint.RUN_REPORTS + "/GroupSummaryCounts")
    suspend fun getCenterSummaryInfo(
        @Query("R_groupId") centerId: Int,
        @Query("genericResultSet") genericResultSet: Boolean,
    ): List<CenterInfo>

    /**
     * Generate the savings-account transaction receipt as raw bytes (default PDF).
     */
    @GET(APIEndPoint.RUN_REPORTS + "/Savings Transaction Receipt")
    suspend fun getSavingsAccountTransactionReceipt(
        @Query("R_transactionId") transactionId: Int,
        @Query("dateFormat") dateFormat: String = "dd MMMM yyyy",
        @Query("output-type") outputType: String = "PDF",
        @Query("locale") locale: String = "en",
    ): ByteArray
}
