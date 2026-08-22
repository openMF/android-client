/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.report.reportDetail

import kpt.feature.report.generated.resources.Res
import kpt.feature.report.generated.resources.feature_report_export_started
import kpt.feature.report.generated.resources.feature_report_exported_successfully
import kpt.feature.report.generated.resources.feature_report_failed_to_load_report_details
import kpt.feature.report.generated.resources.feature_report_unable_to_export
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Constants
import com.mifos.core.domain.useCases.GetReportFullParameterListUseCase
import com.mifos.core.domain.useCases.GetReportParameterDetailsUseCase
import com.mifos.core.domain.useCases.GetRunReportOfficesUseCase
import com.mifos.core.domain.useCases.GetRunReportProductUseCase
import com.mifos.core.domain.useCases.GetRunReportWithQueryUseCase
import com.mifos.core.model.objects.runreport.DataRow
import com.mifos.core.model.objects.runreport.FullParameterListResponse
import com.mifos.core.model.objects.runreport.client.ClientReportTypeItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class ReportDetailViewModel(
    private val getReportFullParameterListUseCase: GetReportFullParameterListUseCase,
    private val getReportParameterDetailsUseCase: GetReportParameterDetailsUseCase,
    private val getRunReportProductUseCase: GetRunReportProductUseCase,
    private val getRunReportWithQueryUseCase: GetRunReportWithQueryUseCase,
    private val getRunReportOfficesUseCase: GetRunReportOfficesUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val reportName =
        savedStateHandle.getStateFlow(key = Constants.REPORT_TYPE_ITEM, initialValue = "")
    val reportItem: ClientReportTypeItem =
        Json.decodeFromString<ClientReportTypeItem>(reportName.value)

    private val _reportDetailUiState =
        MutableStateFlow<ReportDetailUiState>(ReportDetailUiState.Loading)
    val reportDetailUiState = _reportDetailUiState.asStateFlow()

    private val _reportUiState = MutableStateFlow<ReportUiState>(ReportUiState.Initial)
    val reportUiState: StateFlow<ReportUiState> = _reportUiState.asStateFlow()

    private val _reportParameterList = MutableStateFlow<List<DataRow>>(emptyList())
    val reportParameterList = _reportParameterList.asStateFlow()

    private val _reportDetail = MutableStateFlow<Pair<List<DataRow>, String>>(Pair(emptyList(), ""))
    val reportDetail = _reportDetail.asStateFlow()

    private val _reportOffices = MutableStateFlow<List<DataRow>>(emptyList())
    val reportOffices = _reportOffices.asStateFlow()

    private val _reportProducts = MutableStateFlow<List<DataRow>>(emptyList())
    val reportProducts = _reportProducts.asStateFlow()

    private val _runReport = MutableStateFlow<FullParameterListResponse?>(null)
    val runReport = _runReport.asStateFlow()

    fun fetchFullParameterList(reportName: String, parameterType: Boolean) =

        viewModelScope.launch {
            _reportDetailUiState.value = ReportDetailUiState.Loading
            getReportFullParameterListUseCase(reportName, parameterType)
                .catch {
                    _reportDetailUiState.value =
                        ReportDetailUiState.Error(Res.string.feature_report_failed_to_load_report_details)
                }
                .collect { response ->
                    _reportParameterList.value = response.data
                }
        }

    fun fetchParameterDetails(parameterName: String, parameterType: Boolean) =
        viewModelScope.launch {
            getReportParameterDetailsUseCase(parameterName, parameterType)
                .catch { }
                .collect { response ->
                    _reportDetail.value =
                        Pair(response.data, parameterName)
                }
        }

    fun fetchOffices(parameterName: String, officeId: Int, parameterType: Boolean) =
        viewModelScope.launch {
            getRunReportOfficesUseCase(parameterName, officeId, parameterType)
                .catch {
                    _reportDetailUiState.value =
                        ReportDetailUiState.Error(Res.string.feature_report_failed_to_load_report_details)
                }
                .collect { response ->
                    _reportOffices.value = response.data
                    _reportDetailUiState.value = ReportDetailUiState.ParameterDetailsSuccess
                }
        }

    fun fetchProduct(parameterName: String, currencyId: String, parameterType: Boolean) =
        viewModelScope.launch {
            getRunReportProductUseCase(parameterName, currencyId, parameterType)
                .catch {
                    _reportDetailUiState.value =
                        ReportDetailUiState.Error(Res.string.feature_report_failed_to_load_report_details)
                }
                .collect { response ->
                    _reportProducts.value = response.data
                    _reportDetailUiState.value = ReportDetailUiState.ParameterDetailsSuccess
                }
        }

    fun fetchRunReportWithQuery(reportName: String, options: MutableMap<String, String>) =
        viewModelScope.launch {
            _reportDetailUiState.value = ReportDetailUiState.Loading
            getRunReportWithQueryUseCase(reportName, options)
                .catch {
                    _reportDetailUiState.value =
                        ReportDetailUiState.Error(Res.string.feature_report_failed_to_load_report_details)
                }
                .collect { response ->
                    _runReport.value = response
                }
        }

    @OptIn(ExperimentalTime::class)
    fun exportCsv(report: FullParameterListResponse) {
        _reportUiState.value = ReportUiState.Message(Res.string.feature_report_export_started)

        viewModelScope.launch {
            val reportName = "report_${Clock.System.now()}"
            val isSuccess = getFileHelper(reportName).exportCsv(report)
            _reportUiState.value = if (isSuccess) {
                ReportUiState.Message(Res.string.feature_report_exported_successfully)
            } else {
                ReportUiState.Message(Res.string.feature_report_unable_to_export)
            }
        }
    }
}
