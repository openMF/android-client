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

import androidclient.feature.report.generated.resources.Res
import androidclient.feature.report.generated.resources.feature_report_export_started
import androidclient.feature.report.generated.resources.feature_report_exported_successfully
import androidclient.feature.report.generated.resources.feature_report_unable_to_create_directory
import androidclient.feature.report.generated.resources.feature_report_unable_to_export
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Constants
import com.mifos.core.model.objects.runreport.FullParameterListResponse
import io.ktor.http.ContentDisposition.Companion.File
import io.ktor.http.ContentType.Application.Json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
//import java.io.File
//import java.io.FileWriter
import kotlinx.coroutines.IO
import kotlinx.datetime.Clock
import kotlinx.io.files.FileSystem
import okio.Path.Companion.toPath

class ReportViewModel(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val reportParameterString =
        savedStateHandle.getStateFlow(key = Constants.REPORT_PARAMETER_RESPONSE, initialValue = "")
    val report: FullParameterListResponse =
        Json.decodeFromString<FullParameterListResponse>(reportParameterString.value)

    private val _reportUiState = MutableStateFlow<ReportUiState>(ReportUiState.Initial)
    val reportUiState = _reportUiState.asStateFlow()

    fun exportCsv(report: FullParameterListResponse, reportDirectoryPath: String) =
        viewModelScope.launch(Dispatchers.IO) {
            _reportUiState.value = ReportUiState.Message(Res.string.feature_report_export_started)
            val timestamp = Clock.System.now().toEpochMilliseconds().toString()
            val reportPath = "$reportDirectoryPath$timestamp.csv".toPath()
            val reportDirectory = File(reportDirectoryPath)
            val fileSystem = FileSystem.SYSTEM

            if (!reportDirectory.exists()) {
                val makeRequiredDirectories = reportDirectory.mkdirs()
                if (!makeRequiredDirectories) {
                    _reportUiState.value =
                        ReportUiState.Message(Res.string.feature_report_unable_to_create_directory)
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
                    ReportUiState.Message(Res.string.feature_report_unable_to_export)
            }
            _reportUiState.value =
                ReportUiState.Message(Res.string.feature_report_exported_successfully)
        }
}
