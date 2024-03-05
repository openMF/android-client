package com.mifos.mifosxdroid.online.runreports.reportdetail

import com.mifos.core.objects.runreports.FullParameterListResponse
import rx.Observable

/**
 * Created by Aditya Gupta on 12/08/23.
 */
interface ReportDetailRepository {

    fun getReportFullParameterList(
        reportName: String?, parameterType: Boolean
    ): Observable<FullParameterListResponse>

    fun getReportParameterDetails(
        parameterName: String?, parameterType: Boolean
    ): Observable<FullParameterListResponse>

    fun getRunReportOffices(
        parameterName: String?, officeId: Int, parameterType: Boolean
    ): Observable<FullParameterListResponse>

    fun getRunReportProduct(
        parameterName: String?, currency: String?, parameterType: Boolean
    ): Observable<FullParameterListResponse>

    fun getRunReportWithQuery(
        reportName: String?, options: Map<String?, String?>
    ): Observable<FullParameterListResponse>

}