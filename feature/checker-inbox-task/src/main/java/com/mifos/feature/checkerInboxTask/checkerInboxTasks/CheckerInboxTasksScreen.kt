/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.checkerInboxTask.checkerInboxTasks

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.White
import com.mifos.feature.checker_inbox_task.R

/**
 * Created by Aditya Gupta on 21/03/24.
 */

@Composable
internal fun CheckerInboxTasksScreen(
    onBackPressed: () -> Unit,
    checkerInbox: () -> Unit,
    checkerInboxTasksViewModel: CheckerInboxTasksViewModel = hiltViewModel(),
) {
    val state =
        checkerInboxTasksViewModel.checkerInboxTasksUiState.collectAsStateWithLifecycle().value
    val isRefreshing by checkerInboxTasksViewModel.isRefreshing.collectAsStateWithLifecycle()
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)

    LaunchedEffect(key1 = true) {
        checkerInboxTasksViewModel.loadCheckerTasksBadges()
    }

    MifosScaffold(
        icon = MifosIcons.arrowBack,
        title = stringResource(id = R.string.feature_checker_inbox_task_checker_Inbox),
        onBackPressed = onBackPressed,
        snackbarHostState = null,
        bottomBar = { },
    ) { padding ->
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = { checkerInboxTasksViewModel.loadCheckerTasksBadges() },
        ) {
            when (state) {
                is CheckerInboxTasksUiState.Error -> {
                    MifosSweetError(message = stringResource(id = R.string.feature_checker_inbox_task_failed_to_Load_Check_Inbox)) {
                        checkerInboxTasksViewModel.loadCheckerTasksBadges()
                    }
                }

                is CheckerInboxTasksUiState.Loading -> {
                    MifosCircularProgress()
                }

                is CheckerInboxTasksUiState.Success -> {
                    Column(modifier = Modifier.padding(padding)) {
                        TaskOptions(
                            leadingIcon = R.drawable.feature_checker_inbox_task_ic_mail_outline_24dp,
                            option = stringResource(id = R.string.feature_checker_inbox_task_checker_Inbox),
                            badge = state.checkerInboxBadge,
                        ) {
                            checkerInbox()
                        }
                        TaskOptions(
                            leadingIcon = R.drawable.feature_checker_inbox_task_ic_supervisor_account_24dp,
                            option = stringResource(id = R.string.feature_checker_inbox_task_client_Approval),
                            badge = "0",
                        ) {
                        }
                        TaskOptions(
                            leadingIcon = R.drawable.feature_checker_inbox_task_ic_assignment_black_24dp,
                            option = stringResource(id = R.string.feature_checker_inbox_task_loan_Approval),
                            badge = "0",
                        ) {
                        }
                        TaskOptions(
                            leadingIcon = R.drawable.feature_checker_inbox_task_ic_done_all_24dp,
                            option = stringResource(id = R.string.feature_checker_inbox_task_loan_Disbursal),
                            badge = "0",
                        ) {
                        }
                        TaskOptions(
                            leadingIcon = R.drawable.feature_checker_inbox_task_ic_restore_24dp,
                            option = stringResource(id = R.string.feature_checker_inbox_task_reschedule_Loan),
                            badge = state.rescheduleLoanBadge,
                        ) {
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskOptions(leadingIcon: Int, option: String, badge: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(White),
        onClick = {
            onClick()
        },
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(24.dp),
                model = leadingIcon,
                contentDescription = null,
            )
            Text(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f),
                text = option,
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                ),
            )
            Card(
                colors = CardDefaults.cardColors(Color.Red),
                shape = RoundedCornerShape(10.dp),
            ) {
                Text(
                    modifier = Modifier.padding(
                        start = 12.dp,
                        end = 12.dp,
                        top = 2.dp,
                        bottom = 2.dp,
                    ),
                    text = badge,
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = White,
                    ),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskOptionsPreview() {
    TaskOptions(
        leadingIcon = R.drawable.feature_checker_inbox_task_ic_mail_outline_24dp,
        // Replace with your actual drawable
        option = "Checker Inbox",
        badge = "5",
        onClick = { /*TODO*/ },
    )
}

@Preview(showSystemUi = true)
@Composable
private fun PreviewCheckerInboxTaskScreen() {
    CheckerInboxTasksScreen(
        onBackPressed = { },
        checkerInbox = { },

    )
}
