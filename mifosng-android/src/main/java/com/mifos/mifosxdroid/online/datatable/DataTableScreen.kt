package com.mifos.mifosxdroid.online.datatable

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.common.utils.Network
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.objects.noncore.ColumnHeader
import com.mifos.core.objects.noncore.DataTable
import com.mifos.core.ui.components.MifosEmptyUi
import com.mifos.mifosxdroid.R

/**
 * Created on 27/06/2024 (11:38 PM) by Pronay Sarker
 */

@Composable
fun DataTableScreen(
    tableName: String?,
    viewModel: DataTableViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    onClick: (dataTable: DataTable) -> Unit
) {
    val uiState by viewModel.dataTableUiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.loadDataTable(tableName)
    }

    DataTableScreen(
        uiState = uiState,
        navigateBack = navigateBack,
        onRefresh = { viewModel.refresh(tableName) },
        isRefreshing = isRefreshing,
        onClick = onClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataTableScreen(
    uiState: DataTableUiState,
    navigateBack: () -> Unit,
    onRefresh: () -> Unit,
    isRefreshing: Boolean,
    onClick: (dataTable: DataTable) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val pullRefreshState = rememberPullToRefreshState()
    val context = LocalContext.current

    MifosScaffold(
        icon = MifosIcons.arrowBack,
        title = stringResource(id = R.string.datatables),
        onBackPressed = navigateBack,
        snackbarHostState = snackbarHostState
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .nestedScroll(pullRefreshState.nestedScrollConnection)
        ) {
            when (uiState) {
                is DataTableUiState.ShowDataTables -> {
                    DataTableContent(
                        dataTable = uiState.dataTables,
                        onClick = onClick
                    )
                }

                DataTableUiState.ShowEmptyDataTables -> {
                    MifosEmptyUi(text = stringResource(id = R.string.empty_data_table))
                }

                is DataTableUiState.ShowError -> {
                    MifosSweetError(
                        message = stringResource(id = uiState.message), onclick = onRefresh
                    )
                }

                DataTableUiState.ShowProgressbar -> {
                    MifosCircularProgress()
                }
            }

            PullToRefreshContainer(
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
            )
        }

        LaunchedEffect(key1 = isRefreshing) {
            if (isRefreshing)
                pullRefreshState.startRefresh()
        }

        LaunchedEffect(key1 = pullRefreshState.isRefreshing) {
            if (pullRefreshState.isRefreshing) {
                if (Network.isOnline(context)) {
                    onRefresh()
                } else {
                    Toast.makeText(
                        context,
                        context.resources.getText(R.string.error_not_connected_internet),
                        Toast.LENGTH_SHORT,
                    ).show()
                }
                pullRefreshState.endRefresh()
            }
        }
    }
}

@Composable
fun DataTableContent(
    dataTable: List<DataTable>,
    onClick: (dataTable: DataTable) -> Unit
) {
    LazyColumn {
        items(dataTable) { dataTable ->
            DataTableItem(
                dataTable = dataTable,
                onClick = onClick
            )
        }
    }
}

@Composable
fun DataTableItem(
    dataTable: DataTable,
    onClick: (dataTable: DataTable) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 4.dp,
                vertical = 4.dp
            ),
        shape = RoundedCornerShape(0.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        onClick = { onClick.invoke(dataTable) }
    ) {
        dataTable.registeredTableName?.let {
            Text(
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 18.dp
                ),
                style = MaterialTheme.typography.bodyLarge,
                text = it
            )
        }
    }
}

class DataTablePreviewProvider : PreviewParameterProvider<DataTableUiState> {
    val dataTable: List<DataTable> = listOf(
        DataTable(
            applicationTableName = "AppTable1",
            columnHeaderData = listOf(),
            registeredTableName = "registered Table Name"
        ),
        DataTable(
            applicationTableName = "AppTable1",
            columnHeaderData = listOf(),
            registeredTableName = "registered Table Name"
        ),
        DataTable(
            applicationTableName = "AppTable1",
            columnHeaderData = listOf(),
            registeredTableName = "registered Table Name"
        )
    )

    override val values: Sequence<DataTableUiState>
        get() = sequenceOf(
            DataTableUiState.ShowEmptyDataTables,
            DataTableUiState.ShowProgressbar,
            DataTableUiState.ShowDataTables(dataTable),
            DataTableUiState.ShowError(R.string.failed_to_fetch_datatable)
        )
}

@Composable
@Preview(showSystemUi = true)
fun PreviewDataTable(
    @PreviewParameter(DataTablePreviewProvider::class) dataTableUiState: DataTableUiState
) {
    DataTableScreen(
        uiState = dataTableUiState,
        navigateBack = { },
        onRefresh = { },
        isRefreshing = false,
        onClick = { }
    )
}

