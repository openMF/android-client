/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.report.report

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.mifos.core.common.utils.Constants
import com.mifos.core.`object`.runreport.FullParameterListResponse
import com.mifos.feature.report.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val reportParameterString =
        savedStateHandle.getStateFlow(key = Constants.REPORT_PARAMETER_RESPONSE, initialValue = "")
    val report: FullParameterListResponse =
        Gson().fromJson(reportParameterString.value, FullParameterListResponse::class.java)

    private val _reportUiState = MutableStateFlow<ReportUiState>(ReportUiState.Initial)
    val reportUiState = _reportUiState.asStateFlow()

    fun exportCsv(report: FullParameterListResponse, reportDirectoryPath: String) =
        viewModelScope.launch(Dispatchers.IO) {
            _reportUiState.value = ReportUiState.Message(R.string.feature_report_export_started)
            val timestamp = System.currentTimeMillis()
            val reportPath = "$reportDirectoryPath$timestamp.csv"
            val reportDirectory = File(reportDirectoryPath)

            if (!reportDirectory.exists()) {
                val makeRequiredDirectories = reportDirectory.mkdirs()
                if (!makeRequiredDirectories) {
                    _reportUiState.value =
                        ReportUiState.Message(R.string.feature_report_unable_to_create_directory)
                }
            }

            try {
                val fileWriter = FileWriter(reportPath)

                // write headers
                val columnSize = report.columnHeaders.size
                var count = 1
                for (header in report.columnHeaders) {
                    fileWriter.append(header.columnName)
                    if (count == columnSize) {
                        fileWriter.append("\n")
                    } else {
                        fileWriter.append(",")
                    }
                    count++
                }

                // write row data
                for (row in report.data) {
                    fileWriter.append(java.lang.String.join(",", row.row))
                    fileWriter.append("\n")
                }
                fileWriter.flush()
                fileWriter.close()
            } catch (e: Exception) {
                _reportUiState.value =
                    ReportUiState.Message(R.string.feature_report_unable_to_export)
            }
            _reportUiState.value =
                ReportUiState.Message(R.string.feature_report_exported_successfully)
        }
}
