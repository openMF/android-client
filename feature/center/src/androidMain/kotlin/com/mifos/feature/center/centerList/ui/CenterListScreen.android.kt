package com.mifos.feature.center.centerList.ui

import androidclient.feature.center.generated.resources.Res
import androidclient.feature.center.generated.resources.feature_center_error_loading_centers
import androidclient.feature.center.generated.resources.feature_center_ic_done_all_black_24dp
import androidclient.feature.center.generated.resources.feature_center_no_more_centers
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalFoundationApi::class)
@Composable
actual fun CenterListContent(
    state: CenterListUiState,
    isInSelectionMode: Boolean,
    selectedItems: SelectedItemsState,
    onRefresh: () -> Unit,
    onCenterSelect: (Int) -> Unit,
    selectedMode: () -> Unit
) {

    if(state is CenterListUiState.CenterList){
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

        LazyColumn {
            items(centerPagingList.itemCount) { index ->

                val isSelected = selectedItems.contains(centerPagingList[index]!!)
                var cardColor by remember { mutableStateOf(White) }

                OutlinedCard(
                    modifier = Modifier
                        .padding(6.dp)
                        .combinedClickable(
                            onClick = {
                                if (isInSelectionMode) {
                                    cardColor = if (isSelected) {
                                        centerPagingList[index]?.let { selectedItems.remove(it) }
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
                                        centerPagingList[index]?.let { selectedItems.remove(it) }
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
                            },
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedItems.isEmpty()) {
                            cardColor = White
                            White
                        } else {
                            cardColor
                        },
                    ),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal =  16.dp,
                                vertical = 24.dp,
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Canvas(
                            modifier = Modifier.size(16.dp),
                            onDraw = {
                                drawCircle(
                                    color = if (centerPagingList[index]?.active == true) Color.Green else Color.Red,
                                )
                            },
                        )
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 16.dp),
                        ) {
                            centerPagingList[index]?.name?.let {
                                Text(
                                    text = it,
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Normal,
                                        fontStyle = FontStyle.Normal,
                                        color = Black,
                                    ),
                                )
                            }
                            Text(
                                text = centerPagingList[index]?.accountNo.toString(),
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal,
                                    fontStyle = FontStyle.Normal,
                                    color = DarkGray,
                                ),
                            )
                            Row {
                                Text(
                                    text = centerPagingList[index]?.officeName.toString(),
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal,
                                        fontStyle = FontStyle.Normal,
                                        color = DarkGray,
                                    ),
                                )
                                Spacer(modifier = Modifier.width(26.dp))
                                Text(
                                    text = centerPagingList[index]?.officeId.toString(),
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal,
                                        fontStyle = FontStyle.Normal,
                                        color = DarkGray,
                                    ),
                                )
                            }
                            Row {
                                Text(
                                    text = centerPagingList[index]?.staffName.toString(),
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal,
                                        fontStyle = FontStyle.Normal,
                                        color = DarkGray,
                                    ),
                                )
                                Spacer(modifier = Modifier.width(26.dp))
                                Text(
                                    text = centerPagingList[index]?.staffId.toString(),
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal,
                                        fontStyle = FontStyle.Normal,
                                        color = DarkGray,
                                    ),
                                )
                            }
                        }
                        if (centerPagingList[index]?.sync == true) {
                            AsyncImage(
                                modifier = Modifier.size(20.dp),
                                model = Res.drawable.feature_center_ic_done_all_black_24dp,
                                contentDescription = null,
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