///*
// * Copyright 2024 Mifos Initiative
// *
// * This Source Code Form is subject to the terms of the Mozilla Public
// * License, v. 2.0. If a copy of the MPL was not distributed with this
// * file, You can obtain one at https://mozilla.org/MPL/2.0/.
// *
// * See https://github.com/openMF/android-client/blob/master/LICENSE.md
// */
package com.mifos.feature.report.report


import androidclient.feature.report.generated.resources.Res
import androidclient.feature.report.generated.resources.feature_report_export_started
import androidclient.feature.report.generated.resources.feature_report_exported_successfully
import androidclient.feature.report.generated.resources.feature_report_unable_to_export
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Constants
import com.mifos.core.model.objects.runreport.FullParameterListResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.SYSTEM
import okio.buffer
import okio.use
//
//class ReportViewModel(
//    savedStateHandle: SavedStateHandle,
//) : ViewModel() {
//
//    private val reportParameterString =
//        savedStateHandle.getStateFlow(key = Constants.REPORT_PARAMETER_RESPONSE, initialValue = "")
//
//    val report: FullParameterListResponse =
//        Json.decodeFromString(reportParameterString.value)
//
//    private val _reportUiState = MutableStateFlow<ReportUiState>(ReportUiState.Initial)
//    val reportUiState = _reportUiState.asStateFlow()
//
//    fun exportCsv(report: FullParameterListResponse, reportDirectoryPath: String) {
//        viewModelScope.launch {
//            _reportUiState.value = ReportUiState.Message(Res.string.feature_report_export_started)
//
//            val timestamp = Clock.System.now().toEpochMilliseconds().toString()
//            val fileName = "$reportDirectoryPath/$timestamp.csv"
//            val path = fileName.toPath()
//
//            try {
//                FileSystem.SYSTEM.sink(path).buffer().use { sink ->
//                    val headers = report.columnHeaders.joinToString(",") { it.columnName }
//                    sink.writeUtf8(headers + "\n")
//
//                    for (row in report.data) {
//                        val line = row.row.joinToString(",")
//                        sink.writeUtf8(line + "\n")
//                    }
//                }
//
//                _reportUiState.value =
//                    ReportUiState.Message(Res.string.feature_report_exported_successfully)
//            } catch (e: Exception) {
//                _reportUiState.value =
//                    ReportUiState.Message(Res.string.feature_report_unable_to_export)
//            }
//        }
//    }
//}
