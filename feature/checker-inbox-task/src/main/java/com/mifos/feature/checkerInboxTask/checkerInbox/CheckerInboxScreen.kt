/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
@file:OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class,
)

package com.mifos.feature.checkerInboxTask.checkerInbox

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.mifos.core.objects.checkerinboxandtasks.CheckerTask
import com.mifos.core.ui.components.SelectionModeTopAppBar
import com.mifos.feature.checkerInboxTask.checkerInboxDialog.CheckerInboxTasksFilterDialog
import com.mifos.feature.checker_inbox_task.R
import java.sql.Timestamp

@Composable
internal fun CheckerInboxScreen(
    onBackPressed: () -> Unit,
    viewModel: CheckerInboxViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val state by viewModel.checkerInboxUiState.collectAsStateWithLifecycle()
    var isDialogBoxActive by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var checkerList by rememberSaveable { mutableStateOf(listOf<CheckerTask>()) }
    var filterList by rememberSaveable { mutableStateOf(listOf<CheckerTask>()) }
    var isFiltered by rememberSaveable { mutableStateOf(false) }
    var isSearching by rememberSaveable { mutableStateOf(false) }

    var fromDate by rememberSaveable { mutableStateOf<Timestamp?>(null) }
    var toDate by rememberSaveable { mutableStateOf<Timestamp?>(null) }
    var action: String? by rememberSaveable { mutableStateOf(null) }
    var entity: String? by rememberSaveable { mutableStateOf(null) }
    var resourceId: String? by rememberSaveable { mutableStateOf(null) }

    LaunchedEffect(key1 = true) {
        viewModel.loadCheckerTasks()
    }

    if (isDialogBoxActive) {
        CheckerInboxTasksFilterDialog(
            closeDialog = { isDialogBoxActive = false },
            fromDate = fromDate,
            toDate = toDate,
            action = action,
            entity = entity,
            resourceId = resourceId,
            filter = { newAction, newEntity, newResourceId, newFromDate, newToDate ->
                try {
                    action = newAction
                    entity = newEntity
                    resourceId = newResourceId
                    fromDate = newFromDate
                    toDate = newToDate
                    isFiltered = true
                    filterList = getFilteredList(
                        searchQuery,
                        fromDate,
                        toDate,
                        action,
                        entity,
                        resourceId,
                        checkerList,
                    )
                } catch (e: Exception) {
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }
                isDialogBoxActive = false
            },
            clearFilter = {
                isFiltered = false
                isDialogBoxActive = false
                action = null
                entity = null
                resourceId = null
                fromDate = null
                toDate = null
            },
        )
    }

    CheckerInboxScreen(
        state = state,
        onBackPressed = onBackPressed,
        onApprove = { viewModel.approveCheckerEntry(it) },
        onReject = { viewModel.rejectCheckerEntry(it) },
        onDelete = { viewModel.deleteCheckerEntry(it) },
        onRetry = { viewModel.loadCheckerTasks() },
        onApproveList = { list ->
            list.forEach {
                viewModel.approveCheckerEntry(it)
            }
        },
        onRejectList = { list ->
            list.forEach {
                viewModel.rejectCheckerEntry(it)
            }
        },
        onDeleteList = { list ->
            list.forEach {
                viewModel.deleteCheckerEntry(it)
            }
        },
        search = { query ->
            isSearching = query.isNotEmpty()
            searchQuery = query
            filterList = getFilteredList(
                searchQuery,
                fromDate,
                toDate,
                action,
                entity,
                resourceId,
                checkerList,
            )
        },
        filter = {
            isDialogBoxActive = true
        },
        isFiltered = isFiltered,
        isSearching = isSearching,
        filteredList = filterList,
        setList = { checkerList = it },
    )
}

@Composable
private fun CheckerInboxScreen(
    state: CheckerInboxUiState,
    onBackPressed: () -> Unit,
    onApprove: (Int) -> Unit,
    onReject: (Int) -> Unit,
    onDelete: (Int) -> Unit,
    onApproveList: (List<Int>) -> Unit,
    onRejectList: (List<Int>) -> Unit,
    onDeleteList: (List<Int>) -> Unit,
    search: (String) -> Unit,
    filter: () -> Unit,
    isFiltered: Boolean,
    isSearching: Boolean,
    filteredList: List<CheckerTask>,
    setList: (List<CheckerTask>) -> Unit,
    onRetry: () -> Unit = {},
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var searchInbox by rememberSaveable { mutableStateOf("") }
    var approveId by rememberSaveable { mutableIntStateOf(0) }
    var showApproveDialog by rememberSaveable { mutableStateOf(false) }
    var rejectId by rememberSaveable { mutableIntStateOf(0) }
    var showRejectDialog by rememberSaveable { mutableStateOf(false) }
    var deleteId by rememberSaveable { mutableIntStateOf(0) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var isInSelectionMode by rememberSaveable { mutableStateOf(false) }
    val selectedItemsState = remember { SelectedItemsState() }
    var fetchedList: List<CheckerTask> = listOf()
    val resetSelectionMode = {
        isInSelectionMode = false
        selectedItemsState.clear()
    }
    BackHandler(enabled = isInSelectionMode) {
        resetSelectionMode()
    }
    LaunchedEffect(
        key1 = isInSelectionMode,
        key2 = selectedItemsState.size(),
    ) {
        if (isInSelectionMode && selectedItemsState.size() == 0) {
            isInSelectionMode = false
        }
    }

    MifosDialogBox(
        showDialogState = showApproveDialog,
        onDismiss = { showApproveDialog = false },
        title = R.string.feature_checker_inbox_task_are_you_sure_you_want_to_approve_this_task,
        confirmButtonText = R.string.feature_checker_inbox_task_yes,
        onConfirm = {
            onApprove(approveId)
            showApproveDialog = false
        },
        dismissButtonText = R.string.feature_checker_inbox_task_no,
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
        dismissButtonText = R.string.feature_checker_inbox_task_no,
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
        dismissButtonText = R.string.feature_checker_inbox_task_no,
    )

    MifosScaffold(
        topBar = {
            if (isInSelectionMode) {
                SelectionModeTopAppBar(
                    itemCount = selectedItemsState.size(),
                    resetSelectionMode = resetSelectionMode,
                    actions = {
                        IconButton(
                            onClick = {
                                onApproveList(selectedItemsState.selectedItems.value)
                                resetSelectionMode()
                            },
                        ) {
                            Icon(
                                imageVector = MifosIcons.check,
                                contentDescription = null,
                            )
                        }
                        IconButton(
                            onClick = {
                                onRejectList(selectedItemsState.selectedItems.value)
                                resetSelectionMode()
                            },
                        ) {
                            Icon(
                                imageVector = MifosIcons.close,
                                contentDescription = null,
                            )
                        }
                        IconButton(
                            onClick = {
                                onDeleteList(selectedItemsState.selectedItems.value)
                                resetSelectionMode()
                            },
                        ) {
                            Icon(
                                imageVector = MifosIcons.delete,
                                contentDescription = null,
                            )
                        }
                    },
                )
            } else {
                TopAppBar(
                    navigationIcon = {
                        IconButton(
                            onClick = { onBackPressed() },
                        ) {
                            Icon(
                                imageVector = MifosIcons.arrowBack,
                                contentDescription = null,
                            )
                        }
                    },
                    title = {
                        Text(text = stringResource(id = R.string.feature_checker_inbox_task_checker_inbox))
                    },
                    actions = { },
                )
            }
        },
        snackbarHostState = snackbarHostState,
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            ElevatedCard(
                modifier = Modifier.padding(8.dp),
                elevation = CardDefaults.elevatedCardElevation(4.dp),
            ) {
                Row(
                    modifier = Modifier.padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        modifier = Modifier.weight(1f),
                        imageVector = MifosIcons.search,
                        contentDescription = null,
                    )
                    TextField(
                        modifier = Modifier
                            .height(52.dp)
                            .weight(4f),
                        value = searchInbox,
                        onValueChange = {
                            searchInbox = it
                            search.invoke(it)
                        },
                        placeholder = { Text(stringResource(id = R.string.feature_checker_inbox_task_search_by_user)) },
                        colors = TextFieldDefaults.colors().copy(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                        ),
                    )
                    IconButton(
                        modifier = Modifier.weight(1f),
                        onClick = { filter.invoke() },
                    ) {
                        Icon(
                            imageVector = MifosIcons.filter,
                            contentDescription = null,
                        )
                    }
                }
            }

            when (state) {
                is CheckerInboxUiState.CheckerTasksList -> {
                    fetchedList = state.checkerTasks
                    setList.invoke(fetchedList)
                    CheckerInboxContent(
                        isInSelectionMode = isInSelectionMode,
                        selectedItemsState = selectedItemsState,
                        checkerTaskList = if (isFiltered || isSearching) filteredList else fetchedList,
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
                        },
                    ) {
                        isInSelectionMode = true
                    }
                }

                is CheckerInboxUiState.Error -> {
                    MifosSweetError(
                        message = stringResource(id = R.string.feature_checker_inbox_task_failed_to_Load_Check_Inbox),
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
private fun CheckerInboxContent(
    isInSelectionMode: Boolean,
    selectedItemsState: SelectedItemsState,
    checkerTaskList: List<CheckerTask>,
    onApprove: (Int) -> Unit,
    onReject: (Int) -> Unit,
    onDelete: (Int) -> Unit,
    modifier: Modifier = Modifier,
    selectedMode: () -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(
            count = checkerTaskList.size,
            key = { index -> checkerTaskList[index].id },
        ) { index ->
            CheckerInboxItem(
                checkerTask = checkerTaskList[index],
                onApprove = onApprove,
                onReject = onReject,
                onDelete = onDelete,
                isInSelectionMode = isInSelectionMode,
                selectedItemsState = selectedItemsState,
                selectedMode = selectedMode,
            )
        }
    }
}

class SelectedItemsState(initialSelectedItems: List<Int> = emptyList()) {
    private val _selectedItems = mutableStateListOf<Int>().also { it.addAll(initialSelectedItems) }
    val selectedItems: State<List<Int>> = derivedStateOf { _selectedItems }

    fun add(itemId: Int) {
        _selectedItems.add(itemId)
    }

    fun remove(itemId: Int) {
        _selectedItems.remove(itemId)
    }

    fun contains(itemId: Int): Boolean {
        return _selectedItems.contains(itemId)
    }

    fun clear() {
        _selectedItems.clear()
    }

    fun size(): Int {
        return _selectedItems.size
    }
}

@Composable
private fun CheckerInboxItem(
    checkerTask: CheckerTask,
    onApprove: (Int) -> Unit,
    onReject: (Int) -> Unit,
    onDelete: (Int) -> Unit,
    isInSelectionMode: Boolean,
    selectedItemsState: SelectedItemsState,
    selectedMode: () -> Unit,
) {
    val selectedColor = MaterialTheme.colorScheme.primaryContainer
    val unselectedColor = MaterialTheme.colorScheme.surface

    val selectedItems by selectedItemsState.selectedItems
    val isSelected = selectedItemsState.contains(checkerTask.id)

    var cardColor by remember { mutableStateOf(unselectedColor) }

    var expendCheckerTask by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    if (isInSelectionMode) {
                        cardColor = if (isSelected) {
                            selectedItemsState.remove(checkerTask.id)
                            unselectedColor
                        } else {
                            selectedItemsState.add(checkerTask.id)
                            selectedColor
                        }
                    } else {
                        expendCheckerTask = expendCheckerTask.not()
                    }
                },
                onLongClick = {
                    if (isInSelectionMode) {
                        cardColor = if (isSelected) {
                            selectedItemsState.remove(checkerTask.id)
                            unselectedColor
                        } else {
                            selectedItemsState.add(checkerTask.id)
                            selectedColor
                        }
                    } else {
                        selectedMode()
                        selectedItemsState.add(checkerTask.id)
                        cardColor = selectedColor
                    }
                },
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (selectedItems.isEmpty()) {
                cardColor = unselectedColor
                unselectedColor
            } else {
                cardColor
            },
        ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
            ) {
                Text(
                    text = "# ${checkerTask.id} ${checkerTask.actionName} ${checkerTask.entityName}",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = checkerTask.processingResult,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                    ),
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row {
                        Text(
                            text = stringResource(id = R.string.feature_checker_inbox_task_create_by),
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                            ),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = checkerTask.maker,
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                            ),
                        )
                    }
                    Text(
                        text = checkerTask.getDate(),
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                        ),
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
                        color = Black,
                    ),
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    IconButton(onClick = { onApprove(checkerTask.id) }) {
                        Icon(
                            imageVector = MifosIcons.check,
                            tint = Color.Green,
                            contentDescription = null,
                        )
                    }
                    IconButton(onClick = { onReject(checkerTask.id) }) {
                        Icon(
                            imageVector = MifosIcons.close,
                            tint = Color.Yellow,
                            contentDescription = null,
                        )
                    }
                    IconButton(onClick = { onDelete(checkerTask.id) }) {
                        Icon(
                            imageVector = MifosIcons.delete,
                            tint = Color.Red,
                            contentDescription = null,
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
                    textAlign = TextAlign.Center,
                )
            }
            HorizontalDivider()
        }
    }
}

private fun getFilteredList(
    searchQuery: String,
    fromDate: Timestamp?,
    toDate: Timestamp?,
    action: String?,
    entity: String?,
    resourceId: String?,
    list: List<CheckerTask>,
): List<CheckerTask> {
    var checkerList = list
    if (searchQuery.isNotEmpty()) {
        checkerList = checkerList.filter {
            it.maker.contains(searchQuery, true)
        }
    }

    if (!resourceId.isNullOrEmpty()) {
        return checkerList.filter { resourceId == it.resourceId }
    }

    if (fromDate == null && toDate == null) {
        return checkerList
    }

    val aLL = "ALL"
    return checkerList.filter { checkerTask ->
        val isDateInRange = if (fromDate == null) {
            !checkerTask.getTimeStamp().after(toDate)
        } else {
            checkerTask.getTimeStamp().after(fromDate) && checkerTask.getTimeStamp().before(toDate)
        }

        val isActionMatch = action == aLL || action.equals(checkerTask.actionName, true)
        val isEntityMatch = entity == aLL || entity.equals(checkerTask.entityName, true)

        isDateInRange && isActionMatch && isEntityMatch
    }
}

class CheckerInboxUiStateProvider : PreviewParameterProvider<CheckerInboxUiState> {

    override val values: Sequence<CheckerInboxUiState>
        get() = sequenceOf(
            CheckerInboxUiState.Loading,
            CheckerInboxUiState.Error(R.string.feature_checker_inbox_task_failed_to_Load_Check_Inbox),
            CheckerInboxUiState.CheckerTasksList(sampleCheckerTaskList),
            CheckerInboxUiState.SuccessResponse(R.string.feature_checker_inbox_task_client_Approval),
        )
}

@Preview
@Composable
private fun CheckerInboxItemPreview() {
    CheckerInboxItem(
        checkerTask = sampleCheckerTaskList[0],
        onApprove = {},
        onReject = {},
        onDelete = {},
        isInSelectionMode = false,
        selectedItemsState = SelectedItemsState(),
        selectedMode = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun CheckerInboxContentPreview() {
    CheckerInboxContent(
        isInSelectionMode = false,
        selectedItemsState = SelectedItemsState(),
        checkerTaskList = sampleCheckerTaskList,
        onApprove = {},
        onReject = {},
        onDelete = {},
    ) {}
}

@Preview(showBackground = true)
@Composable
private fun CheckerInboxScreenPreview(
    @PreviewParameter(CheckerInboxUiStateProvider::class) state: CheckerInboxUiState,
) {
    CheckerInboxScreen(
        state = state,
        onBackPressed = {},
        onApprove = {},
        onReject = {},
        onDelete = {},
        onApproveList = {},
        onRejectList = {},
        onDeleteList = {},
        filter = {},
        setList = {},
        filteredList = sampleCheckerTaskList,
        isFiltered = false,
        isSearching = false,
        search = {},
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
        resourceId = "resourceId $it",
    )
}
