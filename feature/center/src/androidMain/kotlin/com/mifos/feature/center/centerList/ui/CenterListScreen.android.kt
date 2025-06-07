package com.mifos.feature.center.centerList.ui

import androidclient.feature.center.generated.resources.Res
import androidclient.feature.center.generated.resources.feature_center_error_loading_centers
import androidclient.feature.center.generated.resources.feature_center_ic_done_all_black_24dp
import androidclient.feature.center.generated.resources.feature_center_no_more_centers
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosPagingAppendProgress
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.room.entities.group.CenterEntity
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalFoundationApi::class)
@Composable
actual fun CenterListContent(
    state: CenterListUiState,
    isInSelectionMode: Boolean,
    selectedItems: SelectedItemsState,
    onRefresh: () -> Unit,
    onCenterSelect: (Int) -> Unit,
    selectedMode: () -> Unit,
    modifier: Modifier
) {

    if(state is CenterListUiState.CenterList) {
        val centerPagingList = state.centers.collectAsLazyPagingItems()
        when (centerPagingList.loadState.refresh) {
            is LoadState.Error -> {
                MifosSweetError(message = stringResource(Res.string.feature_center_error_loading_centers)) {
                    onRefresh()
                }
            }

            is LoadState.Loading -> MifosCircularProgress()

            is LoadState.NotLoading -> Unit
        }

        LazyColumn(
            modifier = modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(
                count = centerPagingList.itemCount,
                key = {
                    centerPagingList[it]?.id ?: it
                },
            ) { index ->
                val center = centerPagingList[index]!!

                CenterCard(
                    center = center,
                    selected = selectedItems.contains(center),
                    isInSelectionMode = selectedItems.size() > 0,
                    onSelect = {
                        selectedItems.add(it)
                    },
                    onClick = {
                        onCenterSelect(it.id ?: 0)
                    },
                )
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
                            text = stringResource(Res.string.feature_center_no_more_centers),
                            style = TextStyle(
                                fontSize = 14.sp,
                            ),
                            color = DarkGray,
                            textAlign = TextAlign.Center,
                        )
                    }
                }

                false -> Unit
            }
        }
    }
}
