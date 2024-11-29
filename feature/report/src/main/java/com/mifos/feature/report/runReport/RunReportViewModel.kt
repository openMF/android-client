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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Resource
import com.mifos.core.domain.use_cases.GetReportCategoryUseCase
import com.mifos.feature.report.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RunReportViewModel @Inject constructor(
    private val getReportCategoryUseCase: GetReportCategoryUseCase,
) : ViewModel() {

    private val _runReportUiState = MutableStateFlow<RunReportUiState>(RunReportUiState.Loading)
    val runReportUiState = _runReportUiState.asStateFlow()

    fun fetchCategories(
        reportCategory: String,
        genericResultSet: Boolean,
        parameterType: Boolean,
    ) = viewModelScope.launch(Dispatchers.IO) {
        getReportCategoryUseCase(
            reportCategory,
            genericResultSet,
            parameterType,
        ).collect { result ->
            when (result) {
                is Resource.Error ->
                    _runReportUiState.value =
                        RunReportUiState.Error(R.string.feature_report_failed_to_fetch_reports)

                is Resource.Loading -> _runReportUiState.value = RunReportUiState.Loading

                is Resource.Success -> {
                    result.data?.let { reports ->
                        if (reports.isNotEmpty()) {
                            _runReportUiState.value = RunReportUiState.RunReports(reports)
                        } else {
                            _runReportUiState.value =
                                RunReportUiState.Error(R.string.feature_report_no_reports_found)
                        }
                    }
                }
            }
        }
    }
}
