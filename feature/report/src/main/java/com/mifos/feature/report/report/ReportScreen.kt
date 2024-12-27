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

import android.Manifest
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.PermissionBox
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.Black
import com.mifos.core.designsystem.theme.White
import com.mifos.core.modelobjects.runreport.FullParameterListResponse
import com.mifos.feature.report.R
import kotlinx.coroutines.launch

@Composable
internal fun ReportScreen(
    onBackPressed: () -> Unit,
    viewModel: ReportViewModel = hiltViewModel(),
) {
    val report = viewModel.report
    val state by viewModel.reportUiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    ReportScreen(
        state = state,
        report = report,
        onBackPressed = onBackPressed,
        exportReport = {
            val reportDirectoryPath =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .toString() + getString(context, R.string.feature_report_export_csv_directory)
            viewModel.exportCsv(
                report = report,
                reportDirectoryPath = reportDirectoryPath,
            )
        },
    )
}

@Composable
internal fun ReportScreen(
    state: ReportUiState,
    report: FullParameterListResponse,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
    exportReport: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var checkPermission by remember { mutableStateOf(false) }

    when (state) {
        is ReportUiState.Initial -> Unit
        is ReportUiState.Message -> {
            Toast.makeText(context, stringResource(id = state.message), Toast.LENGTH_SHORT).show()
        }
    }

    if (checkPermission) {
        PermissionBox(
            requiredPermissions = if (Build.VERSION.SDK_INT >= 33) {
                listOf(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                listOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                )
            },
            title = R.string.feature_report_permission_required,
            description = R.string.feature_report_external_approve_permission_description,
            confirmButtonText = R.string.feature_report_proceed,
            dismissButtonText = R.string.feature_report_dismiss,
            onGranted = {
                LaunchedEffect(key1 = Unit) {
                    scope.launch {
                        exportReport()
                    }
                }
            },
        )
    }

    MifosScaffold(
        modifier = modifier,
        icon = MifosIcons.arrowBack,
        title = stringResource(R.string.feature_report_title),
        onBackPressed = onBackPressed,
        actions = {
            TextButton(
                onClick = {
                    checkPermission = true
                },
                colors = ButtonDefaults.textButtonColors(White),
            ) {
                Text(text = stringResource(id = R.string.feature_report_export_csv), color = Black)
            }
        },
        snackbarHostState = snackbarHostState,
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
        ) {
            LazyRow {
                itemsIndexed(report.columnHeaders.map { it.columnName }) { index, columnName ->
                    Column {
                        Text(
                            modifier = Modifier.padding(8.dp),
                            text = columnName,
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                            ),
                        )
                        report.data.map { it.row }.forEach {
                            Text(text = it[index], modifier = Modifier.padding(8.dp))
                        }
                    }
                }
            }
        }
    }
}

private class ReportUiStateProvider : PreviewParameterProvider<ReportUiState> {

    override val values: Sequence<ReportUiState>
        get() = sequenceOf(
            ReportUiState.Initial,
            ReportUiState.Message(R.string.feature_report_export_csv),
        )
}

@Preview(showBackground = true)
@Composable
private fun ReportScreenPreview(
    @PreviewParameter(ReportUiStateProvider::class) state: ReportUiState,
) {
    ReportScreen(
        state = state,
        report = FullParameterListResponse(emptyList(), emptyList()),
        onBackPressed = { },
        exportReport = { },
    )
}
