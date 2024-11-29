/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.report.reportDetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.Resource
import com.mifos.core.domain.use_cases.GetReportFullParameterListUseCase
import com.mifos.core.domain.use_cases.GetReportParameterDetailsUseCase
import com.mifos.core.domain.use_cases.GetRunReportOfficesUseCase
import com.mifos.core.domain.use_cases.GetRunReportProductUseCase
import com.mifos.core.domain.use_cases.GetRunReportWithQueryUseCase
import com.mifos.core.objects.runreports.DataRow
import com.mifos.core.objects.runreports.FullParameterListResponse
import com.mifos.core.objects.runreports.client.ClientReportTypeItem
import com.mifos.feature.report.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportDetailViewModel @Inject constructor(
    private val getReportFullParameterListUseCase: GetReportFullParameterListUseCase,
    private val getReportParameterDetailsUseCase: GetReportParameterDetailsUseCase,
    private val getRunReportProductUseCase: GetRunReportProductUseCase,
    private val getRunReportWithQueryUseCase: GetRunReportWithQueryUseCase,
    private val getRunReportOfficesUseCase: GetRunReportOfficesUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val reportName =
        savedStateHandle.getStateFlow(key = Constants.REPORT_TYPE_ITEM, initialValue = "")
    val reportItem: ClientReportTypeItem =
        Gson().fromJson(reportName.value, ClientReportTypeItem::class.java)

    private val _reportDetailUiState =
        MutableStateFlow<ReportDetailUiState>(ReportDetailUiState.Loading)
    val reportDetailUiState = _reportDetailUiState.asStateFlow()

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
        viewModelScope.launch(Dispatchers.IO) {
            getReportFullParameterListUseCase(reportName, parameterType).collect { result ->
                when (result) {
                    is Resource.Error ->
                        _reportDetailUiState.value =
                            ReportDetailUiState.Error(R.string.feature_report_failed_to_load_report_details)

                    is Resource.Loading -> _reportDetailUiState.value = ReportDetailUiState.Loading

                    is Resource.Success ->
                        _reportParameterList.value =
                            result.data?.data ?: emptyList()
                }
            }
        }

    fun fetchParameterDetails(parameterName: String, parameterType: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            getReportParameterDetailsUseCase(parameterName, parameterType).collect { result ->
                when (result) {
                    is Resource.Error -> Unit

                    is Resource.Loading -> Unit

                    is Resource.Success -> {
                        _reportDetail.value =
                            Pair(result.data?.data ?: emptyList(), parameterName)
                    }
                }
            }
        }

    fun fetchOffices(parameterName: String, officeId: Int, parameterType: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            getRunReportOfficesUseCase(parameterName, officeId, parameterType).collect { result ->
                when (result) {
                    is Resource.Error ->
                        _reportDetailUiState.value =
                            ReportDetailUiState.Error(R.string.feature_report_failed_to_load_report_details)

                    is Resource.Loading -> Unit

                    is Resource.Success -> {
                        _reportOffices.value = result.data?.data ?: emptyList()
                        _reportDetailUiState.value = ReportDetailUiState.ParameterDetailsSuccess
                    }
                }
            }
        }

    fun fetchProduct(parameterName: String, currencyId: String, parameterType: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            getRunReportProductUseCase(parameterName, currencyId, parameterType).collect { result ->
                when (result) {
                    is Resource.Error ->
                        _reportDetailUiState.value =
                            ReportDetailUiState.Error(R.string.feature_report_failed_to_load_report_details)

                    is Resource.Loading -> Unit

                    is Resource.Success -> {
                        _reportProducts.value = result.data?.data ?: emptyList()
                        _reportDetailUiState.value = ReportDetailUiState.ParameterDetailsSuccess
                    }
                }
            }
        }

    fun fetchRunReportWithQuery(reportName: String, options: MutableMap<String, String>) =
        viewModelScope.launch(Dispatchers.IO) {
            getRunReportWithQueryUseCase(reportName, options).collect { result ->
                when (result) {
                    is Resource.Error ->
                        _reportDetailUiState.value =
                            ReportDetailUiState.Error(R.string.feature_report_failed_to_load_report_details)

                    is Resource.Loading -> _reportDetailUiState.value = ReportDetailUiState.Loading

                    is Resource.Success -> _runReport.value = result.data
                }
            }
        }
}
