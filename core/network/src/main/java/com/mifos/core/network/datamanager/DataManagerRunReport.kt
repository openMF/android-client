package com.mifos.core.network.datamanager

import com.mifos.core.network.BaseApiManager
import com.mifos.core.objects.group.CenterInfo
import com.mifos.core.objects.runreports.FullParameterListResponse
import com.mifos.core.objects.runreports.client.ClientReportTypeItem
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Rajan Maurya on 05/02/17.
 */
@Singleton
class DataManagerRunReport @Inject constructor(val mBaseApiManager: BaseApiManager) {
    fun getReportCategories(
        reportCategory: String?,
        genericResultSet: Boolean,
        parameterType: Boolean
    ): Observable<List<ClientReportTypeItem>> {
        return mBaseApiManager.runReportsService.getReportCategories(
            reportCategory,
            genericResultSet, parameterType
        )
    }

    fun getReportFullParameterList(
        reportName: String?, parameterType: Boolean
    ): Observable<FullParameterListResponse> {
        return mBaseApiManager.runReportsService
            .getReportFullParameterList(reportName, parameterType)
    }

    fun getReportParameterDetails(
        parameterName: String?, parameterType: Boolean
    ): Observable<FullParameterListResponse> {
        return mBaseApiManager.runReportsService
            .getReportParameterDetails(parameterName, parameterType)
    }

    fun getRunReportWithQuery(
        reportName: String?, options: Map<String?, String?>
    ): Observable<FullParameterListResponse> {
        return mBaseApiManager.runReportsService
            .getRunReportWithQuery(reportName, options)
    }

    fun getCenterSummaryInfo(
        centerId: Int,
        genericResultSet: Boolean
    ): Observable<List<CenterInfo>> {
        return mBaseApiManager.runReportsService
            .getCenterSummaryInfo(centerId, genericResultSet)
    }

    fun getRunReportOffices(
        parameterName: String?, officeId: Int, parameterType: Boolean
    ): Observable<FullParameterListResponse> {
        return mBaseApiManager.runReportsService.getReportOffice(
            parameterName,
            officeId,
            parameterType
        )
    }

    fun getRunReportProduct(
        parameterName: String?, currency: String?, parameterType: Boolean
    ): Observable<FullParameterListResponse> {
        return mBaseApiManager.runReportsService.getReportProduct(
            parameterName,
            currency,
            parameterType
        )
    }
}