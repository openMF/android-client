@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)

package com.mifos.feature.center.center_list.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosPagingAppendProgress
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.Black
import com.mifos.core.designsystem.theme.BlueSecondary
import com.mifos.core.designsystem.theme.DarkGray
import com.mifos.core.designsystem.theme.LightGray
import com.mifos.core.designsystem.theme.White
import com.mifos.core.objects.group.Center
import com.mifos.core.ui.components.SelectionModeTopAppBar
import com.mifos.feature.center.R
import com.mifos.feature.center.sync_centers_dialog.SyncCenterDialogScreen
import kotlinx.coroutines.flow.flowOf

@Composable
fun CenterListScreen(
    paddingValues: PaddingValues,
    createNewCenter: () -> Unit,
    onCenterSelect: (Int) -> Unit
) {

    val viewModel: CenterListViewModel = hiltViewModel()
    val refreshState by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val state by viewModel.centerListUiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = true) {
        viewModel.getCenterList()
    }

    CenterListScreen(
        paddingValues = paddingValues,
        state = state,
        createNewCenter = createNewCenter,
        onRefresh = {
            viewModel.refreshCenterList()
        },
        refreshState = refreshState,
        onCenterSelect = onCenterSelect,
     //   syncClicked = syncClicked
    )
}

@Composable
fun CenterListScreen(
    paddingValues: PaddingValues,
    state: CenterListUiState,
    createNewCenter: () -> Unit,
    onRefresh: () -> Unit,
    refreshState: Boolean,
    onCenterSelect: (Int) -> Unit
) {

    val snackbarHostState = remember { SnackbarHostState() }
    var isInSelectionMode by rememberSaveable { mutableStateOf(false) }
    val selectedItems = remember { mutableStateListOf<Center>() }
    val resetSelectionMode = {
        isInSelectionMode = false
        selectedItems.clear()
    }
    val sync = rememberSaveable {
        mutableStateOf(false)
    }
    BackHandler(enabled = isInSelectionMode) {
        resetSelectionMode()
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = refreshState,
        onRefresh = onRefresh
    )

    LaunchedEffect(
        key1 = isInSelectionMode,
        key2 = selectedItems.size,
    ) {
        if (isInSelectionMode && selectedItems.isEmpty()) {
            isInSelectionMode = false
        }
    }

    Scaffold(
        modifier = Modifier.padding(paddingValues),
        topBar = {
            if (isInSelectionMode) {
                SelectionModeTopAppBar(
                    itemCount = selectedItems.size,
                    resetSelectionMode = resetSelectionMode,
                    actions = {
                        FilledTonalButton(
                            onClick = {
                                sync.value = true
                            },
                        ) {
                            Icon(
                                imageVector = MifosIcons.sync,
                                contentDescription = "Sync Items",
                            )
                            Text(text = stringResource(id = R.string.feature_center_sync))
                        }
                    }
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { createNewCenter() },
                containerColor = BlueSecondary
            ) {
                Icon(
                    imageVector = MifosIcons.Add,
                    contentDescription = null
                )
            }
        }
    ) { paddingValue ->
        Column(
            modifier = Modifier
                .padding(paddingValue),
            verticalArrangement = Arrangement.Center
        ) {
            Box(modifier = Modifier.pullRefresh(pullRefreshState)) {
                when (state) {
                    is CenterListUiState.Error -> {
                        MifosSweetError(message = stringResource(id = state.message)) {
                            onRefresh()
                        }
                    }

                    is CenterListUiState.Loading -> {
                        MifosCircularProgress()
                    }

                    is CenterListUiState.CenterList -> {
                        CenterListContent(
                            centerPagingList = state.centers.collectAsLazyPagingItems(),
                            isInSelectionMode = isInSelectionMode,
                            selectedItems = selectedItems,
                            onRefresh = {
                                onRefresh()
                            }, onCenterSelect = {
                                onCenterSelect(it)
                            }, selectedMode = {
                                isInSelectionMode = true
                            }
                        )
                    }

                    is CenterListUiState.CenterListDb -> CenterListDbContent(centerList = state.centers)
                }
                if (sync.value) {
                    SyncCenterDialogScreen(
                        dismiss = {
                            sync.value = false
                            selectedItems.clear()
                            resetSelectionMode()
                        },
                        hide = { sync.value = false },
                        centers = selectedItems.toList()
                    )
                }
                PullRefreshIndicator(
                    refreshing = refreshState,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }
}

@Composable
fun CenterListContent(
    centerPagingList: LazyPagingItems<Center>,
    isInSelectionMode: Boolean,
    selectedItems: SnapshotStateList<Center>,
    onRefresh: () -> Unit,
    onCenterSelect: (Int) -> Unit,
    selectedMode: () -> Unit

) {

    when (centerPagingList.loadState.refresh) {
        is LoadState.Error -> {
            MifosSweetError(message = stringResource(id = R.string.feature_center_error_loading_centers)) {
                onRefresh()
            }
        }

        is LoadState.Loading -> MifosCircularProgress()

        is LoadState.NotLoading -> Unit
    }

    LazyColumn {
        items(centerPagingList.itemCount) { index ->

            val isSelected = selectedItems.contains(centerPagingList[index])
            var cardColor by remember { mutableStateOf(White) }

            OutlinedCard(
                modifier = Modifier
                    .padding(6.dp)
                    .combinedClickable(
                        onClick = {
                            if (isInSelectionMode) {
                                cardColor = if (isSelected) {
                                    selectedItems.remove(centerPagingList[index])
                                    White
                                } else {
                                    centerPagingList[index]?.let { selectedItems.add(it) }
                                    LightGray
                                }
                            } else {
                                centerPagingList[index]?.id?.let { onCenterSelect(it) }
                            }
                        },
                        onLongClick = {
                            if (isInSelectionMode) {
                                cardColor = if (isSelected) {
                                    selectedItems.remove(centerPagingList[index])
                                    White
                                } else {
                                    centerPagingList[index]?.let { selectedItems.add(it) }
                                    LightGray
                                }
                            } else {
                                selectedMode()
                                centerPagingList[index]?.let { selectedItems.add(it) }
                                cardColor = LightGray
                            }
                        }
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedItems.isEmpty()) {
                        cardColor = White
                        White
                    } else cardColor,
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = 24.dp,
                            bottom = 24.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Canvas(
                        modifier = Modifier.size(16.dp),
                        onDraw = {
                            drawCircle(
                                color = if (centerPagingList[index]?.active == true) Color.Green else Color.Red
                            )
                        }
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp)
                    ) {
                        centerPagingList[index]?.name?.let {
                            Text(
                                text = it,
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal,
                                    fontStyle = FontStyle.Normal,
                                    color = Black
                                )
                            )
                        }
                        Text(
                            text = centerPagingList[index]?.accountNo.toString(),
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                fontStyle = FontStyle.Normal,
                                color = DarkGray
                            )
                        )
                        Row {
                            Text(
                                text = centerPagingList[index]?.officeName.toString(),
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal,
                                    fontStyle = FontStyle.Normal,
                                    color = DarkGray
                                )
                            )
                            Spacer(modifier = Modifier.width(26.dp))
                            Text(
                                text = centerPagingList[index]?.officeId.toString(),
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal,
                                    fontStyle = FontStyle.Normal,
                                    color = DarkGray
                                )
                            )
                        }
                        Row {
                            Text(
                                text = centerPagingList[index]?.staffName.toString(),
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal,
                                    fontStyle = FontStyle.Normal,
                                    color = DarkGray
                                )
                            )
                            Spacer(modifier = Modifier.width(26.dp))
                            Text(
                                text = centerPagingList[index]?.staffId.toString(),
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal,
                                    fontStyle = FontStyle.Normal,
                                    color = DarkGray
                                )
                            )
                        }
                    }
                    if (centerPagingList[index]?.sync == true) {
                        AsyncImage(
                            modifier = Modifier.size(20.dp),
                            model = R.drawable.feature_center_ic_done_all_black_24dp,
                            contentDescription = null
                        )
                    }
                }
            }
        }

        when (centerPagingList.loadState.append) {
            is LoadState.Error -> {}

            is LoadState.Loading -> {
                item {
                    MifosPagingAppendProgress()
                }
            }

            is LoadState.NotLoading -> Unit
        }
        when (centerPagingList.loadState.append.endOfPaginationReached) {
            true -> {
                item {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp),
                        text = stringResource(id = R.string.feature_center_no_more_centers),
                        style = TextStyle(
                            fontSize = 14.sp
                        ),
                        color = DarkGray,
                        textAlign = TextAlign.Center
                    )
                }
            }

            false -> Unit
        }
    }
}

@Composable
fun CenterListDbContent(
    centerList: List<Center>
) {
    LazyColumn {
        items(centerList) { center ->

            OutlinedCard(
                modifier = Modifier
                    .padding(6.dp),
                colors = CardDefaults.cardColors(
                    containerColor = White
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = 24.dp,
                            bottom = 24.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Canvas(
                        modifier = Modifier.size(16.dp),
                        onDraw = {
                            drawCircle(
                                color = if (center.active == true) Color.Green else Color.Red
                            )
                        }
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp)
                    ) {
                        center.name?.let {
                            Text(
                                text = it,
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal,
                                    fontStyle = FontStyle.Normal,
                                    color = Black
                                )
                            )
                        }
                        Text(
                            text = center.accountNo.toString(),
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                fontStyle = FontStyle.Normal,
                                color = DarkGray
                            )
                        )
                        Row {
                            Text(
                                text = center.officeName.toString(),
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal,
                                    fontStyle = FontStyle.Normal,
                                    color = DarkGray
                                )
                            )
                            Spacer(modifier = Modifier.width(26.dp))
                            Text(
                                text = center.officeId.toString(),
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal,
                                    fontStyle = FontStyle.Normal,
                                    color = DarkGray
                                )
                            )
                        }
                        Row {
                            Text(
                                text = center.staffName.toString(),
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal,
                                    fontStyle = FontStyle.Normal,
                                    color = DarkGray
                                )
                            )
                            Spacer(modifier = Modifier.width(26.dp))
                            Text(
                                text = center.staffId.toString(),
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal,
                                    fontStyle = FontStyle.Normal,
                                    color = DarkGray
                                )
                            )
                        }
                    }
                    AsyncImage(
                        modifier = Modifier.size(20.dp),
                        model = R.drawable.feature_center_ic_done_all_black_24dp,
                        contentDescription = null
                    )
                }
            }
        }
    }
}


class CenterListUiStateProvider :
    PreviewParameterProvider<CenterListUiState> {

    override val values: Sequence<CenterListUiState>
        get() = sequenceOf(
            CenterListUiState.Loading,
            CenterListUiState.Error(R.string.feature_center_error_loading_centers),
            CenterListUiState.CenterListDb(sampleCenterListDb),
            CenterListUiState.CenterList(sampleCenterList)
        )
}


@Preview(showBackground = true)
@Composable
private fun CenterListContentPreview() {
    CenterListContent(
        centerPagingList = sampleCenterList.collectAsLazyPagingItems(),
        isInSelectionMode = false,
        selectedItems = rememberSaveable { mutableStateListOf() },
        onRefresh = {},
        onCenterSelect = {},
        selectedMode = {}
    )
}


@Preview(showBackground = true)
@Composable
private fun CenterListDbContentPreview() {
    CenterListDbContent(sampleCenterListDb)
}

@Preview(showBackground = true)
@Composable
private fun CenterListScreenPreview(
    @PreviewParameter(CenterListUiStateProvider::class) centerListUiState: CenterListUiState
) {
    CenterListScreen(
        paddingValues = PaddingValues(),
        state = centerListUiState,
        createNewCenter = {},
        onRefresh = {},
        refreshState = false,
        onCenterSelect = {}
    )
}

val sampleCenterListDb = List(10) {
    Center(
        name = "Center $it",
        officeId = it,
        officeName = "Office $it",
        staffId = it,
        staffName = "Staff $it",
        active = it % 2 == 0
    )
}

val sampleCenterList = flowOf(PagingData.from(sampleCenterListDb))
