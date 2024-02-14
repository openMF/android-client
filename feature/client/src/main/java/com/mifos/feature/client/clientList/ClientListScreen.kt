package com.mifos.feature.client.clientList

import androidx.compose.foundation.border
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.mifos.core.designsystem.theme.Black
import com.mifos.core.designsystem.theme.DarkGray
import com.mifos.core.designsystem.theme.LightGray
import com.mifos.core.designsystem.theme.White
import com.mifos.feature.client.R

@Composable
fun ClientListScreen() {

    val viewModel: ClientListViewModel = hiltViewModel()

    val array = listOf("", "", "", "", "", "", "", "", "", "")

    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)

    Scaffold(
        modifier = Modifier
            .padding(),
        floatingActionButton = {
            FloatingActionButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null
                )
            }
        },
        containerColor = White
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
        ) {
            SwipeRefresh(
                state = swipeRefreshState,
                onRefresh = {
                    viewModel.refreshClientList()
                }) {
                LazyColumn {
                    items(array) {
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
                                    model =
//                        if(client.imagePresent) client.imageId else
                                    R.drawable.ic_dp_placeholder,
                                    contentDescription = null
                                )
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 16.dp)
                                ) {
                                    Text(
                                        text = "client Name",
                                        style = TextStyle(
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Normal,
                                            fontStyle = FontStyle.Normal,
                                            color = Black
                                        )
                                    )
                                    Text(
                                        text = "client id",
                                        style = TextStyle(
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Normal,
                                            fontStyle = FontStyle.Normal,
                                            color = DarkGray
                                        )
                                    )
                                }
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
    }
}

@Preview
@Composable
fun ClientListScreenPreview() {
    ClientListScreen()
}