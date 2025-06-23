/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.report.runReport

import androidclient.feature.report.generated.resources.Res
import androidclient.feature.report.generated.resources.feature_report_failed_to_fetch_reports
import androidclient.feature.report.generated.resources.feature_report_no_reports_found
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.DataState
import com.mifos.core.domain.useCases.GetReportCategoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RunReportViewModel(
    private val getReportCategoryUseCase: GetReportCategoryUseCase,
) : ViewModel() {

    private val _runReportUiState = MutableStateFlow<RunReportUiState>(RunReportUiState.Loading)
    val runReportUiState = _runReportUiState.asStateFlow()

    fun fetchCategories(
        reportCategory: String,
        genericResultSet: Boolean,
        parameterType: Boolean,
    ) = viewModelScope.launch {
        getReportCategoryUseCase(
            reportCategory,
            genericResultSet,
            parameterType,
        ).collect { result ->
            when (result) {
                is DataState.Error ->
                    _runReportUiState.value =
                        RunReportUiState.Error(Res.string.feature_report_failed_to_fetch_reports)

                is DataState.Loading -> _runReportUiState.value = RunReportUiState.Loading

                is DataState.Success -> {
                    if (result.data.isNotEmpty()) {
                        _runReportUiState.value = RunReportUiState.RunReports(result.data)
                    } else {
                        _runReportUiState.value =
                            RunReportUiState.Error(Res.string.feature_report_no_reports_found)
                    }
                }
            }
        }
    }
}
