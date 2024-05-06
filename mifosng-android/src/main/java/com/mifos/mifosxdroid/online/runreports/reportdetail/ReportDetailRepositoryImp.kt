package com.mifos.mifosxdroid.online.runreports.reportdetail

import com.mifos.core.network.datamanager.DataManagerRunReport
import com.mifos.core.objects.runreports.FullParameterListResponse
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 12/08/23.
 */
class ReportDetailRepositoryImp @Inject constructor(private val dataManager: DataManagerRunReport) :
    ReportDetailRepository {

    override fun getReportFullParameterList(
        reportName: String?,
        parameterType: Boolean
    ): Observable<FullParameterListResponse> {
        return dataManager.getReportFullParameterList(reportName, parameterType)
    }

    override fun getReportParameterDetails(
        parameterName: String?,
        parameterType: Boolean
    ): Observable<FullParameterListResponse> {
        return dataManager.getReportParameterDetails(parameterName, parameterType)
    }

    override fun getRunReportOffices(
        parameterName: String?,
        officeId: Int,
        parameterType: Boolean
    ): Observable<FullParameterListResponse> {
        return dataManager.getRunReportOffices(parameterName, officeId, parameterType)
    }

    override fun getRunReportProduct(
        parameterName: String?,
        currency: String?,
        parameterType: Boolean
    ): Observable<FullParameterListResponse> {
        return dataManager.getRunReportProduct(parameterName, currency, parameterType)
    }

    override fun getRunReportWithQuery(
        reportName: String?,
        options: Map<String?, String?>
    ): Observable<FullParameterListResponse> {
        return dataManager.getRunReportWithQuery(reportName, options)
    }
}