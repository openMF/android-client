@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.mifos.feature.client.clientList.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosPagingAppendProgress
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.theme.Black
import com.mifos.core.designsystem.theme.BlueSecondary
import com.mifos.core.designsystem.theme.DarkGray
import com.mifos.core.designsystem.theme.LightGray
import com.mifos.core.designsystem.theme.White
import com.mifos.core.objects.client.Client
import com.mifos.feature.client.R

/**
 * Created by Aditya Gupta on 21/02/24.
 */

@Composable
fun ClientListScreen(
    createNewClient: () -> Unit,
    syncClicked: (List<Client>) -> Unit,
    onClientSelect: (Client) -> Unit,
) {

    val viewModel: ClientListViewModel = hiltViewModel()

    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)

    val snackbarHostState = remember { SnackbarHostState() }

    val state = viewModel.clientListUiState.collectAsState().value

    val isInSelectionMode = remember { mutableStateOf(false) }
    val selectedItems = remember { mutableStateListOf<Client>() }

    val resetSelectionMode = {
        isInSelectionMode.value = false
        selectedItems.clear()
    }

    BackHandler(enabled = isInSelectionMode.value) {
        resetSelectionMode()
    }


    LaunchedEffect(
        key1 = isInSelectionMode.value,
        key2 = selectedItems.size,
    ) {
        if (isInSelectionMode.value && selectedItems.isEmpty()) {
            isInSelectionMode.value = false
        }
    }

    Scaffold(
        modifier = Modifier
            .padding(),
        topBar = {
            if (isInSelectionMode.value) {
                SelectionModeTopAppBar(
                    selectedItems = selectedItems,
                    syncClicked = { syncClicked(selectedItems.toList()) },
                    resetSelectionMode = resetSelectionMode,
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { createNewClient() },
                containerColor = BlueSecondary
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null
                )
            }
        },
        containerColor = White,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = {
                viewModel.refreshClientList()
            }) {
            Column(
                modifier = Modifier
                    .padding(padding),
                verticalArrangement = Arrangement.Center
            ) {
                when (state) {
                    is ClientListUiState.ClientListApi -> {
                        LazyColumnForClientListApi(
                            clientPagingList = state.list.collectAsLazyPagingItems(),
                            isInSelectionMode,
                            selectedItems,
                            failedRefresh = { viewModel.refreshClientList() }
                        ) {
                            onClientSelect(it)
                        }
                    }

                    is ClientListUiState.ClientListDb -> {
                        LazyColumnForClientListDb(clientList = state.list)
                    }

                    ClientListUiState.Empty -> {

                    }

                    is ClientListUiState.Error -> {
                        MifosSweetError(message = state.message) {
                            viewModel.refreshClientList()
                        }
                    }
                }

            }
        }
    }
}

@Composable
private fun SelectionModeTopAppBar(
    selectedItems: SnapshotStateList<Client>,
    syncClicked: () -> Unit,
    resetSelectionMode: () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = BlueSecondary
        ),
        title = {
            Text(
                text = "${selectedItems.size} selected",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.onBackground,
                ),
            )
        },
        navigationIcon = {
            IconButton(
                onClick = resetSelectionMode,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = null,
                    tint = Black,
                )
            }
        },
        actions = {
            IconButton(
                onClick = {
                    syncClicked()
                    resetSelectionMode()
                },
            ) {
                Icon(
                    imageVector = Icons.Rounded.Sync,
                    contentDescription = null,
                    tint = Black,
                )
            }
        }
    )
}

@Composable
fun LazyColumnForClientListApi(
    clientPagingList: LazyPagingItems<Client>,
    isInSelectionMode: MutableState<Boolean>,
    selectedItems: SnapshotStateList<Client>,
    failedRefresh : () ->Unit,
    onClientSelect: (Client) -> Unit
) {

    when (clientPagingList.loadState.refresh) {
        is LoadState.Error -> {
            MifosSweetError(message = "Failed to Fetch Clients") {
                failedRefresh()
            }
        }

        is LoadState.Loading -> MifosCircularProgress()

        is LoadState.NotLoading -> Unit
    }


    LazyColumn {
        items(clientPagingList.itemCount) { index ->

            val isSelected = selectedItems.contains(clientPagingList[index])
            var cardColor by remember { mutableStateOf(White) }

            OutlinedCard(
                modifier = Modifier
                    .padding(6.dp)
                    .combinedClickable(
                        onClick = {
                            if (isInSelectionMode.value) {
                                cardColor = if (isSelected) {
                                    selectedItems.remove(clientPagingList[index])
                                    White
                                } else {
                                    clientPagingList[index]?.let { selectedItems.add(it) }
                                    LightGray
                                }
                            } else {
                                clientPagingList[index]?.let { onClientSelect(it) }
                            }
                        },
                        onLongClick = {
                            if (isInSelectionMode.value) {
                                cardColor = if (isSelected) {
                                    selectedItems.remove(clientPagingList[index])
                                    White
                                } else {
                                    clientPagingList[index]?.let { selectedItems.add(it) }
                                    LightGray
                                }
                            } else {
                                isInSelectionMode.value = true
                                clientPagingList[index]?.let { selectedItems.add(it) }
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
                    AsyncImage(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .border(width = 1.dp, LightGray, shape = CircleShape),
                        model = R.drawable.ic_dp_placeholder,
                        contentDescription = null
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp)
                    ) {
                        clientPagingList[index]?.displayName?.let {
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
                            text = clientPagingList[index]?.accountNo.toString(),
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                fontStyle = FontStyle.Normal,
                                color = DarkGray
                            )
                        )
                    }
                    if (clientPagingList[index]?.sync == true) {
                        AsyncImage(
                            modifier = Modifier.size(20.dp),
                            model = R.drawable.ic_done_all_black_24dp,
                            contentDescription = null
                        )
                    }
                }
            }
        }

        when (clientPagingList.loadState.append) {
            is LoadState.Error -> {

            }

            is LoadState.Loading -> {
                item {
                    MifosPagingAppendProgress()
                }
            }

            is LoadState.NotLoading -> Unit
        }
        when (clientPagingList.loadState.append.endOfPaginationReached) {
            true -> {
                item {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp),
                        text = "No More Clients Available !", style = TextStyle(
                            fontSize = 14.sp
                        ), color = DarkGray, textAlign = TextAlign.Center
                    )
                }
            }

            false -> Unit
        }
    }
}

@Composable
fun LazyColumnForClientListDb(clientList: List<Client>) {

    LazyColumn {
        items(clientList) { client ->

            OutlinedCard(
                modifier = Modifier.padding(6.dp),
                colors = CardDefaults.cardColors(
                    containerColor = White,
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
                    AsyncImage(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .border(width = 1.dp, LightGray, shape = CircleShape),
                        model = R.drawable.ic_dp_placeholder,
                        contentDescription = null
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp)
                    ) {
                        client.displayName?.let {
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
                            text = client.accountNo.toString(),
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                fontStyle = FontStyle.Normal,
                                color = DarkGray
                            )
                        )
                    }
                    if (client.sync) {
                        AsyncImage(
                            modifier = Modifier.size(20.dp),
                            model = R.drawable.ic_done_all_black_24dp,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}