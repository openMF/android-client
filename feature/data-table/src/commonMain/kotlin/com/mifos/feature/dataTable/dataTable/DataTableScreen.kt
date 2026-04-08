/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.dataTable.dataTable

import androidclient.feature.data_table.generated.resources.Res
import androidclient.feature.data_table.generated.resources.feature_data_table_empty_data_table
import androidclient.feature.data_table.generated.resources.feature_data_table_failed_to_fetch_data_table
import androidclient.feature.data_table.generated.resources.feature_data_table_title
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.ui.components.MifosEmptyUi
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.room.entities.noncore.DataTableEntity
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.viewmodel.koinViewModel
import template.core.base.designsystem.theme.KptTheme
/**
 * Created on 27/06/2024 (11:38 PM) by Pronay Sarker
 */

@Composable
fun DataTableScreen(
    navigateBack: () -> Unit,
    viewModel: DataTableViewModel = koinViewModel(),
    onClick: (table: String, entityId: Int, dataTable: DataTableEntity) -> Unit,
) {
    val tableName = viewModel.route.tableName
    val entityId = viewModel.route.entityId
    val uiState by viewModel.dataTableUiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    DataTableScreen(
        uiState = uiState,
        navigateBack = navigateBack,
        onRefresh = { viewModel.refresh(tableName) },
        isRefreshing = isRefreshing,
        onClick = {
            onClick(it.registeredTableName ?: "", entityId, it)
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataTableScreen(
    uiState: DataTableUiState,
    navigateBack: () -> Unit,
    onRefresh: () -> Unit,
    isRefreshing: Boolean,
    onClick: (dataTable: DataTableEntity) -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val pullRefreshState = rememberPullToRefreshState()

    MifosScaffold(
        title = stringResource(Res.string.feature_data_table_title),
        onBackPressed = navigateBack,
        snackbarHostState = snackbarHostState,
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(it),
        ) {
            PullToRefreshBox(
                state = pullRefreshState,
                isRefreshing = isRefreshing,
                onRefresh = onRefresh,
            ) {
                when (uiState) {
                    is DataTableUiState.ShowDataTables -> {
                        DataTableContent(
                            dataTable = uiState.dataTables,
                            onClick = onClick,
                        )
                    }

                    DataTableUiState.ShowEmptyDataTables -> {
                        MifosEmptyUi(text = stringResource(Res.string.feature_data_table_empty_data_table))
                    }

                    is DataTableUiState.ShowError -> {
                        MifosSweetError(
                            message = stringResource(uiState.message),
                            onclick = onRefresh,
                        )
                    }

                    DataTableUiState.ShowProgressbar -> {
                        MifosProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
fun DataTableContent(
    dataTable: List<DataTableEntity>,
    onClick: (dataTable: DataTableEntity) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
    ) {
        items(dataTable) { dataTable ->
            DataTableItem(
                dataTable = dataTable,
                onClick = onClick,
            )
        }
    }
}

@Composable
fun DataTableItem(
    dataTable: DataTableEntity,
    onClick: (dataTable: DataTableEntity) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = KptTheme.spacing.xs,
                vertical = KptTheme.spacing.xs,
            ),
        shape = DesignToken.shapes.none,
        elevation = CardDefaults.cardElevation(defaultElevation = DesignToken.elevation.dp2),
        colors = CardDefaults.cardColors(
            containerColor = KptTheme.colorScheme.surface,
        ),
        onClick = { onClick.invoke(dataTable) },
    ) {
        dataTable.registeredTableName?.let {
            Text(
                modifier = Modifier.padding(
                    horizontal = KptTheme.spacing.md,
                    vertical = DesignToken.spacing.dp18,
                ),
                style = KptTheme.typography.bodyLarge,
                text = it,
            )
        }
    }
}

class DataTablePreviewProvider : PreviewParameterProvider<DataTableUiState> {
    val dataTable: List<DataTableEntity> = listOf(
        DataTableEntity(
            applicationTableName = "AppTable1",
            columnHeaderData = listOf(),
            registeredTableName = "registered Table Name",
        ),
        DataTableEntity(
            applicationTableName = "AppTable1",
            columnHeaderData = listOf(),
            registeredTableName = "registered Table Name",
        ),
        DataTableEntity(
            applicationTableName = "AppTable1",
            columnHeaderData = listOf(),
            registeredTableName = "registered Table Name",
        ),
    )

    override val values: Sequence<DataTableUiState>
        get() = sequenceOf(
            DataTableUiState.ShowEmptyDataTables,
            DataTableUiState.ShowProgressbar,
            DataTableUiState.ShowDataTables(dataTable),
            DataTableUiState.ShowError(Res.string.feature_data_table_failed_to_fetch_data_table),
        )
}

@Composable
@Preview
private fun PreviewDataTable(
    @PreviewParameter(DataTablePreviewProvider::class) dataTableUiState: DataTableUiState,
) {
    DataTableScreen(
        uiState = dataTableUiState,
        navigateBack = { },
        onRefresh = { },
        isRefreshing = false,
        onClick = { },
    )
}
