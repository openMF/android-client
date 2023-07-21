package com.mifos.mifosxdroid.online.runreports.reportcategory

import com.mifos.mifosxdroid.base.MvpView
import com.mifos.objects.runreports.client.ClientReportTypeItem

/**
 * Created by Tarun on 03-08-17.
 */
interface ReportCategoryMvpView : MvpView {
    fun showError(error: String)

    fun showReportCategories(reportTypes: List<ClientReportTypeItem>)
}