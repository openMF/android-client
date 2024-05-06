package com.mifos.mifosxdroid.online.runreports.reportcategory

import com.mifos.core.objects.runreports.client.ClientReportTypeItem

/**
 * Created by Aditya Gupta on 12/08/23.
 */
sealed class ReportCategoryUiState {

    data object ShowProgressbar : ReportCategoryUiState()

    data class ShowError(val message: String) : ReportCategoryUiState()

    data class ShowReportCategories(val clientReportTypeItems: List<ClientReportTypeItem>) :
        ReportCategoryUiState()


}