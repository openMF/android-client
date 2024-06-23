package com.mifos.feature.report.run_report

import com.mifos.core.objects.runreports.client.ClientReportTypeItem

sealed class RunReportUiState {

    data object Loading : RunReportUiState()

    data class Error(val message : Int) : RunReportUiState()

    data class RunReports(val runReports: List<ClientReportTypeItem>) : RunReportUiState()
}