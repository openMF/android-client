package com.mifos.core.data.repository

import com.mifos.core.objects.runreports.FullParameterListResponse

/**
 * Created by Aditya Gupta on 12/08/23.
 */
interface ReportDetailRepository {

    suspend fun getReportFullParameterList(
        reportName: String, parameterType: Boolean
    ): FullParameterListResponse

    suspend fun getReportParameterDetails(
        parameterName: String, parameterType: Boolean
    ): FullParameterListResponse

    suspend fun getRunReportOffices(
        parameterName: String, officeId: Int, parameterType: Boolean
    ): FullParameterListResponse

    suspend fun getRunReportProduct(
        parameterName: String, currency: String, parameterType: Boolean
    ): FullParameterListResponse

    suspend fun getRunReportWithQuery(
        reportName: String, options: Map<String, String>
    ): FullParameterListResponse

}