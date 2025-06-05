/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.checker.inbox.task.checkerInboxTasks

import androidclient.feature.checker_inbox_task.generated.resources.Res
import androidclient.feature.checker_inbox_task.generated.resources.feature_checker_inbox_task_checker_Inbox
import androidclient.feature.checker_inbox_task.generated.resources.feature_checker_inbox_task_client_Approval
import androidclient.feature.checker_inbox_task.generated.resources.feature_checker_inbox_task_failed_to_Load_Checker_Inbox
import androidclient.feature.checker_inbox_task.generated.resources.feature_checker_inbox_task_ic_assignment_black_24dp
import androidclient.feature.checker_inbox_task.generated.resources.feature_checker_inbox_task_ic_done_all_24dp
import androidclient.feature.checker_inbox_task.generated.resources.feature_checker_inbox_task_ic_mail_outline_24dp
import androidclient.feature.checker_inbox_task.generated.resources.feature_checker_inbox_task_ic_restore_24dp
import androidclient.feature.checker_inbox_task.generated.resources.feature_checker_inbox_task_ic_supervisor_account_24dp
import androidclient.feature.checker_inbox_task.generated.resources.feature_checker_inbox_task_loan_Approval
import androidclient.feature.checker_inbox_task.generated.resources.feature_checker_inbox_task_loan_Disbursal
import androidclient.feature.checker_inbox_task.generated.resources.feature_checker_inbox_task_reschedule_Loan
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

/**
 * Created by Aditya Gupta on 21/03/24.
 */

@OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
@Composable
internal fun CheckerInboxTasksScreen(
    onBackPressed: () -> Unit,
    checkerInbox: () -> Unit,
    onRefresh: () -> Unit,
    checkerInboxTasksViewModel: CheckerInboxTasksViewModel = koinViewModel(),
) {
    val state =
        checkerInboxTasksViewModel.checkerInboxTasksUiState.collectAsStateWithLifecycle().value
    val isRefreshing by checkerInboxTasksViewModel.isRefreshing.collectAsStateWithLifecycle()
    val pullRefreshState = rememberPullToRefreshState()

    LaunchedEffect(key1 = true) {
        checkerInboxTasksViewModel.loadCheckerTasksBadges()
    }

    MifosScaffold(
        title = stringResource(Res.string.feature_checker_inbox_task_checker_Inbox),
        onBackPressed = onBackPressed,
    ) { padding ->

        PullToRefreshBox(
            state = pullRefreshState,
            onRefresh = onRefresh,
            isRefreshing = isRefreshing,
        ) {
            when (state) {
                is CheckerInboxTasksUiState.Error -> {
                    MifosSweetError(message = stringResource(Res.string.feature_checker_inbox_task_failed_to_Load_Checker_Inbox)) {
                        checkerInboxTasksViewModel.loadCheckerTasksBadges()
                    }
                }

                is CheckerInboxTasksUiState.Loading -> {
                    MifosCircularProgress()
                }

                is CheckerInboxTasksUiState.Success -> {
                    Column(modifier = Modifier.padding(padding)) {
                        TaskOptions(
                            leadingIcon = painterResource(Res.drawable.feature_checker_inbox_task_ic_mail_outline_24dp),
                            option = stringResource(Res.string.feature_checker_inbox_task_checker_Inbox),
                            badge = state.checkerInboxBadge,
                        ) {
                            checkerInbox()
                        }
                        TaskOptions(
                            leadingIcon = painterResource(Res.drawable.feature_checker_inbox_task_ic_supervisor_account_24dp),
                            option = stringResource(Res.string.feature_checker_inbox_task_client_Approval),
                            badge = "0",
                        ) {
                        }
                        TaskOptions(
                            leadingIcon = painterResource(Res.drawable.feature_checker_inbox_task_ic_assignment_black_24dp),
                            option = stringResource(Res.string.feature_checker_inbox_task_loan_Approval),
                            badge = "0",
                        ) {
                        }
                        TaskOptions(
                            leadingIcon = painterResource(Res.drawable.feature_checker_inbox_task_ic_done_all_24dp),
                            option = stringResource(Res.string.feature_checker_inbox_task_loan_Disbursal),
                            badge = "0",
                        ) {
                        }
                        TaskOptions(
                            leadingIcon = painterResource(Res.drawable.feature_checker_inbox_task_ic_restore_24dp),
                            option = stringResource(Res.string.feature_checker_inbox_task_reschedule_Loan),
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
private fun TaskOptions(
    leadingIcon: Painter,
    option: String,
    badge: String,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
            Icon(
                modifier = Modifier
                    .size(24.dp),
                painter = leadingIcon,
                contentDescription = null,
            )
            Text(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f),
                text = option,
                style = MaterialTheme.typography.labelLarge,
            )
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                ),
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
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Preview
@Composable
private fun TaskOptionsPreview() {
    TaskOptions(
        leadingIcon = painterResource(Res.drawable.feature_checker_inbox_task_ic_mail_outline_24dp),
        option = "Checker Inbox",
        badge = "5",
        onClick = { },
    )
}

@Preview
@Composable
private fun PreviewCheckerInboxTaskScreen() {
    CheckerInboxTasksScreen(
        onBackPressed = { },
        checkerInbox = { },
        onRefresh = { },
    )
}
