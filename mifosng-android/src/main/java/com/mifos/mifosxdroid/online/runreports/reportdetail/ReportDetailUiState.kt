package com.mifos.mifosxdroid.online.runreports.reportdetail

import com.mifos.core.objects.runreports.FullParameterListResponse

/**
 * Created by Aditya Gupta on 12/08/23.
 */
sealed class ReportDetailUiState {

    data object ShowProgressbar : ReportDetailUiState()

    data class ShowError(val message: String) : ReportDetailUiState()

    data class ShowFullParameterResponse(val response: FullParameterListResponse) :
        ReportDetailUiState()

    data class ShowParameterDetails(
        val response: FullParameterListResponse,
        val parameterName: String
    ) : ReportDetailUiState()

    data class ShowOffices(val response: FullParameterListResponse, val parameterName: String) :
        ReportDetailUiState()

    data class ShowProduct(val response: FullParameterListResponse, val parameterName: String) :
        ReportDetailUiState()

    data class ShowRunReport(val response: FullParameterListResponse) : ReportDetailUiState()
}