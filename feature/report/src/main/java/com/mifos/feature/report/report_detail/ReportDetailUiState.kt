package com.mifos.feature.report.report_detail

/**
 * Created by Aditya Gupta on 12/08/23.
 */
sealed class ReportDetailUiState {

    data object Loading : ReportDetailUiState()

    data class Error(val message: Int) : ReportDetailUiState()

    data object ParameterDetailsSuccess : ReportDetailUiState()
}