@file:OptIn(ExperimentalMaterial3Api::class)

package com.sparklead.feature.checker_inbox_task.checker_inbox_and_task.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.theme.Black
import com.mifos.core.designsystem.theme.White
import com.sparklead.feature.checker_inbox_task.R

/**
 * Created by Aditya Gupta on 21/03/24.
 */

@Composable
fun CheckerInboxTasksScreen(onBackPressed: () -> Unit, checkerInbox: () -> Unit) {

    val checkerInboxTasksViewModel: CheckerInboxTasksViewModel = hiltViewModel()
    val state = checkerInboxTasksViewModel.checkerInboxTasksUiState.collectAsState().value
    val isRefreshing by checkerInboxTasksViewModel.isRefreshing.collectAsState()
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)

    LaunchedEffect(key1 = true) {
        checkerInboxTasksViewModel.loadCheckerTasksBadges()
    }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = White,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = White),
                navigationIcon = {
                    IconButton(
                        onClick = { onBackPressed() },
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBackIosNew,
                            contentDescription = null,
                            tint = Black,
                        )
                    }
                },
                title = {
                    Text(
                        text = stringResource(id = R.string.checker_inbox_and_pending_tasks),
                        style = TextStyle(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Medium,
                            fontStyle = FontStyle.Normal,
                            fontFamily = FontFamily(Font(R.font.outfit_medium))
                        ),
                        color = Black,
                        textAlign = TextAlign.Start
                    )
                },
            )
        }
    ) { padding ->
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = { checkerInboxTasksViewModel.loadCheckerTasksBadges() }
        ) {
            when (state) {
                is CheckerInboxTasksUiState.Error -> {
                    MifosSweetError(message = "Failed to Load Check Inbox") {
                        checkerInboxTasksViewModel.loadCheckerTasksBadges()
                    }
                }

                is CheckerInboxTasksUiState.Loading -> {
                    MifosCircularProgress()
                }

                is CheckerInboxTasksUiState.Success -> {
                    Column(modifier = Modifier.padding(padding)) {
                        TaskOptions(
                            leadingIcon = R.drawable.ic_mail_outline_24dp,
                            option = "Checker Inbox",
                            badge = state.checkerInboxBadge
                        ) {
                            checkerInbox()
                        }
                        TaskOptions(
                            leadingIcon = R.drawable.ic_supervisor_account_24dp,
                            option = "Client Approval",
                            badge = "0"
                        ) {

                        }
                        TaskOptions(
                            leadingIcon = R.drawable.ic_assignment_black_24dp,
                            option = "Loan Approval",
                            badge = "0"
                        ) {

                        }
                        TaskOptions(
                            leadingIcon = R.drawable.ic_done_all_24dp,
                            option = "Loan Disbursal",
                            badge = "0"
                        ) {

                        }
                        TaskOptions(
                            leadingIcon = R.drawable.ic_restore_24dp,
                            option = "Reschedule Loan",
                            badge = state.rescheduleLoanBadge
                        ) {

                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TaskOptions(leadingIcon: Int, option: String, badge: String, onClick: () -> Unit) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(White),
        onClick = {
            onClick()
        }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(24.dp),
                model = leadingIcon,
                contentDescription = null
            )
            Text(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f),
                text = option,
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily(Font(R.font.outfit_medium))
                )
            )
            Card(
                colors = CardDefaults.cardColors(Color.Red)
            ) {
                Text(
                    modifier = Modifier.padding(
                        start = 12.dp,
                        end = 12.dp,
                        top = 4.dp,
                        bottom = 4.dp
                    ),
                    text = badge,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.outfit_medium)),
                        color = White
                    )
                )
            }
        }
    }
}