@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.mifos.feature.checker_inbox_task.checker_inbox.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosDialogBox
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.Black
import com.mifos.core.designsystem.theme.White
import com.mifos.core.objects.checkerinboxandtasks.CheckerTask
import com.mifos.feature.checker_inbox_task.R

@Composable
fun CheckerInboxScreen(onBackPressed: () -> Unit) {

    val viewModel: CheckerInboxViewModel = hiltViewModel()
    val state by viewModel.checkerInboxUiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = true) {
        viewModel.loadCheckerTasks()
    }

    CheckerInboxScreen(
        state = state,
        onBackPressed = onBackPressed,
        onApprove = { viewModel.approveCheckerEntry(it) },
        onReject = { viewModel.rejectCheckerEntry(it) },
        onDelete = { viewModel.deleteCheckerEntry(it) },
        onRetry = { viewModel.loadCheckerTasks() }
    )
}

@Composable
fun CheckerInboxScreen(
    state: CheckerInboxUiState,
    onBackPressed: () -> Unit,
    onApprove: (Int) -> Unit,
    onReject: (Int) -> Unit,
    onDelete: (Int) -> Unit,
    onRetry: () -> Unit = {}
) {

    val snackbarHostState = remember { SnackbarHostState() }
    var searchInbox by rememberSaveable { mutableStateOf("") }
    var approveId by rememberSaveable { mutableIntStateOf(0) }
    var showApproveDialog by rememberSaveable { mutableStateOf(false) }
    var rejectId by rememberSaveable { mutableIntStateOf(0) }
    var showRejectDialog by rememberSaveable { mutableStateOf(false) }
    var deleteId by rememberSaveable { mutableIntStateOf(0) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }


    MifosDialogBox(
        showDialogState = showApproveDialog,
        onDismiss = { showApproveDialog = false },
        title = R.string.feature_checker_inbox_task_are_you_sure_you_want_to_approve_this_task,
        confirmButtonText = R.string.feature_checker_inbox_task_yes,
        onConfirm = {
            onApprove(approveId)
            showApproveDialog = false
        },
        dismissButtonText = R.string.feature_checker_inbox_task_no
    )

    MifosDialogBox(
        showDialogState = showRejectDialog,
        onDismiss = { showRejectDialog = false },
        title = R.string.feature_checker_inbox_task_are_you_sure_you_want_to_reject_this_task,
        confirmButtonText = R.string.feature_checker_inbox_task_yes,
        onConfirm = {
            onReject(rejectId)
            showRejectDialog = false
        },
        dismissButtonText = R.string.feature_checker_inbox_task_no
    )


    MifosDialogBox(
        showDialogState = showDeleteDialog,
        onDismiss = { showDeleteDialog = false },
        title = R.string.feature_checker_inbox_task_are_you_sure_you_want_to_delete_this_task,
        confirmButtonText = R.string.feature_checker_inbox_task_yes,
        onConfirm = {
            onDelete(deleteId)
            showDeleteDialog = false
        },
        dismissButtonText = R.string.feature_checker_inbox_task_no
    )

    MifosScaffold(
        icon = MifosIcons.arrowBack,
        title = stringResource(id = R.string.feature_checker_inbox_task_checker_inbox),
        onBackPressed = onBackPressed,
        snackbarHostState = snackbarHostState
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            ElevatedCard(
                modifier = Modifier.padding(8.dp),
                elevation = CardDefaults.elevatedCardElevation(4.dp),
                colors = CardDefaults.elevatedCardColors(White)
            ) {
                Row(
                    modifier = Modifier.padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        modifier = Modifier.weight(1f),
                        imageVector = MifosIcons.search,
                        contentDescription = null
                    )
                    TextField(
                        modifier = Modifier
                            .height(52.dp)
                            .weight(4f),
                        value = searchInbox,
                        onValueChange = {
                            searchInbox = it
                        },
                        placeholder = { Text(stringResource(id = R.string.feature_checker_inbox_task_search_by_user)) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = White,
                            unfocusedContainerColor = White,
                            focusedIndicatorColor = Color.White,
                            unfocusedIndicatorColor = Color.White
                        )
                    )
                    IconButton(modifier = Modifier.weight(1f), onClick = { }) {
                        Icon(
                            imageVector = MifosIcons.filter,
                            contentDescription = null
                        )
                    }
                }
            }

            when (state) {
                is CheckerInboxUiState.CheckerTasksList -> {
                    CheckerInboxContent(
                        checkerTaskList = state.checkerTasks.filter { checkerTask ->
                            checkerTask.maker.contains(searchInbox)
                        },
                        onApprove = {
                            approveId = it
                            showApproveDialog = true
                        },
                        onReject = {
                            rejectId = it
                            showRejectDialog = true
                        },
                        onDelete = {
                            deleteId = it
                            showDeleteDialog = true
                        }
                    )
                }

                is CheckerInboxUiState.Error -> {
                    MifosSweetError(
                        message = stringResource(id = R.string.feature_checker_inbox_task_failed_to_Load_Check_Inbox)
                    ) {
                        onRetry()
                    }
                }

                is CheckerInboxUiState.Loading -> {
                    MifosCircularProgress()
                }

                is CheckerInboxUiState.SuccessResponse -> {
                    val message = stringResource(state.message)
                    LaunchedEffect(key1 = message) {
                        snackbarHostState.showSnackbar(message = message)
                    }
                }
            }

        }
    }

}

@Composable
fun CheckerInboxContent(
    checkerTaskList: List<CheckerTask>,
    onApprove: (Int) -> Unit,
    onReject: (Int) -> Unit,
    onDelete: (Int) -> Unit
) {
    LazyColumn {
        items(checkerTaskList.size) { index ->
            CheckerInboxItem(
                checkerTask = checkerTaskList[index],
                onApprove = onApprove,
                onReject = onReject,
                onDelete = onDelete
            )
        }
    }
}


@Composable
fun CheckerInboxItem(
    checkerTask: CheckerTask,
    onApprove: (Int) -> Unit,
    onReject: (Int) -> Unit,
    onDelete: (Int) -> Unit
) {

    var expendCheckerTask by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                expendCheckerTask = expendCheckerTask.not()
            },
        colors = CardDefaults.cardColors(White)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                modifier = Modifier
                    .width(8.dp)
                    .height(60.dp), colors = CardDefaults.cardColors(Color.Yellow)
            ) {
            }
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "# ${checkerTask.id} ${checkerTask.actionName} ${checkerTask.entityName}",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Black
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = checkerTask.processingResult,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row {
                        Text(
                            text = stringResource(id = R.string.feature_checker_inbox_task_create_by),
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = checkerTask.maker,
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                            )
                        )
                    }
                    Text(
                        text = checkerTask.getDate(),
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    )
                }
            }
        }
        HorizontalDivider()
        if (expendCheckerTask) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = checkerTask.entityName,
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Black
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = { onApprove(checkerTask.id) }) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            tint = Color.Green,
                            contentDescription = null
                        )
                    }
                    IconButton(onClick = { onReject(checkerTask.id) }) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            tint = Color.Yellow,
                            contentDescription = null
                        )
                    }
                    IconButton(onClick = { onDelete(checkerTask.id) }) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            tint = Color.Red,
                            contentDescription = null
                        )
                    }
                }
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = checkerTask.getDate(),
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                    textAlign = TextAlign.Center
                )
            }
            HorizontalDivider()
        }
    }
}

class CheckerInboxUiStateProvider : PreviewParameterProvider<CheckerInboxUiState> {

    override val values: Sequence<CheckerInboxUiState>
        get() = sequenceOf(
            CheckerInboxUiState.Loading,
            CheckerInboxUiState.Error(R.string.feature_checker_inbox_task_failed_to_Load_Check_Inbox),
            CheckerInboxUiState.CheckerTasksList(sampleCheckerTaskList),
            CheckerInboxUiState.SuccessResponse(R.string.feature_checker_inbox_task_client_Approval)
        )
}

@Preview
@Composable
private fun CheckerInboxItemPreview() {
    CheckerInboxItem(
        checkerTask = sampleCheckerTaskList[0],
        onApprove = {},
        onReject = {},
        onDelete = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun CheckerInboxContentPreview() {
    CheckerInboxContent(
        checkerTaskList = sampleCheckerTaskList,
        onApprove = {},
        onReject = {},
        onDelete = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun CheckerInboxScreenPreview(
    @PreviewParameter(CheckerInboxUiStateProvider::class) state: CheckerInboxUiState
) {
    CheckerInboxScreen(
        state = state,
        onBackPressed = {},
        onApprove = {},
        onReject = {},
        onDelete = {}
    )
}

val sampleCheckerTaskList = List(10) {
    CheckerTask(
        id = it,
        madeOnDate = it.toLong(),
        processingResult = (it % 2 == 0).toString(),
        maker = "maker $it",
        actionName = "action $it",
        entityName = "entity $it",
        resourceId = "resourceId $it"
    )
}