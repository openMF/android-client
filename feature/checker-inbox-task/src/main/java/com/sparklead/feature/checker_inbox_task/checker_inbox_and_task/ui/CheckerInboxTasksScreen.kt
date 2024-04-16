@file:OptIn(ExperimentalMaterial3Api::class)

package com.sparklead.feature.checker_inbox_task.checker_inbox_and_task.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.theme.White
import com.sparklead.feature.checker_inbox_task.R

/**
 * Created by Aditya Gupta on 21/03/24.
 */

@Composable
fun CheckerInboxTasksScreen(
    checkerInboxTasksViewModel: CheckerInboxTasksViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
    checkerInbox: () -> Unit
) {

    val state =
        checkerInboxTasksViewModel.checkerInboxTasksUiState.collectAsStateWithLifecycle().value
    val isRefreshing by checkerInboxTasksViewModel.isRefreshing.collectAsStateWithLifecycle()
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)

    LaunchedEffect(key1 = true) {
        checkerInboxTasksViewModel.loadCheckerTasksBadges()
    }

    MifosScaffold(
        icon = Icons.Rounded.ArrowBackIosNew,
        title = stringResource(id = R.string.checker_inbox_and_pending_tasks),
        onBackPressed = { onBackPressed() },
        actions = { },
        snackbarHostState = null,
        bottomBar = { })
    { padding ->
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = { checkerInboxTasksViewModel.loadCheckerTasksBadges() }
        ) {
            when (state) {
                is CheckerInboxTasksUiState.Error -> {
                    MifosSweetError(message = stringResource(id = R.string.failed_to_Load_Check_Inbox)) {
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
                            option = stringResource(id = R.string.checker_Inbox),
                            badge = state.checkerInboxBadge
                        ) {
                            checkerInbox()
                        }
                        TaskOptions(
                            leadingIcon = R.drawable.ic_supervisor_account_24dp,
                            option = stringResource(id = R.string.client_Approval),
                            badge = "0"
                        ) {

                        }
                        TaskOptions(
                            leadingIcon = R.drawable.ic_assignment_black_24dp,
                            option = stringResource(id = R.string.loan_Approval),
                            badge = "0"
                        ) {

                        }
                        TaskOptions(
                            leadingIcon = R.drawable.ic_done_all_24dp,
                            option = stringResource(id = R.string.loan_Disbursal),
                            badge = "0"
                        ) {

                        }
                        TaskOptions(
                            leadingIcon = R.drawable.ic_restore_24dp,
                            option = stringResource(id = R.string.reschedule_Loan),
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
                .padding(24.dp)
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
                    fontWeight = FontWeight.Medium
                )
            )
            Card(
                colors = CardDefaults.cardColors(Color.Red),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    modifier = Modifier.padding(
                        start = 12.dp,
                        end = 12.dp,
                        top = 2.dp,
                        bottom = 2.dp
                    ),
                    text = badge,
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = White
                    )
                )
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PreviewCheckerInboxTaskScreen() {
    CheckerInboxTasksScreen(onBackPressed = { }) {

    }
}