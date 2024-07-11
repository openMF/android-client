package com.mifos.feature.report.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.objects.runreports.FullParameterListResponse
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
class ReportViewModel @Inject constructor() : ViewModel() {

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