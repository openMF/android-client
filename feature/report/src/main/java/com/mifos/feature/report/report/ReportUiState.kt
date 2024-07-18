package com.mifos.feature.report.report

sealed class ReportUiState {

    data object Initial : ReportUiState()

    data class Message(val message: Int) : ReportUiState()
}