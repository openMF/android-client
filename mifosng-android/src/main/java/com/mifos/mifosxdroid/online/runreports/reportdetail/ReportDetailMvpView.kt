package com.mifos.mifosxdroid.online.runreports.reportdetail

import com.mifos.mifosxdroid.base.MvpView
import com.mifos.objects.runreports.FullParameterListResponse

/**
 * Created by Tarun on 04-08-17.
 */
interface ReportDetailMvpView : MvpView {
    fun showError(error: String)
    fun showFullParameterResponse(response: FullParameterListResponse)
    fun showParameterDetails(response: FullParameterListResponse, identifier: String)
    fun showRunReport(response: FullParameterListResponse)
    fun showOffices(response: FullParameterListResponse, identifier: String)
    fun showProduct(response: FullParameterListResponse, identifier: String)
}