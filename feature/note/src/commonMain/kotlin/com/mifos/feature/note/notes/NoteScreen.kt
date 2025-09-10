/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.note.notes

import androidclient.feature.note.generated.resources.Res
import androidclient.feature.note.generated.resources.delete_document
import androidclient.feature.note.generated.resources.edit
import androidclient.feature.note.generated.resources.feature_note_add_item
import androidclient.feature.note.generated.resources.feature_note_delete
import androidclient.feature.note.generated.resources.feature_note_delete_note
import androidclient.feature.note.generated.resources.feature_note_delete_note_confirmation
import androidclient.feature.note.generated.resources.feature_note_item
import androidclient.feature.note.generated.resources.feature_note_notes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.model.objects.notes.Note
import com.mifos.core.ui.components.Actions
import com.mifos.core.ui.components.MifosActionsNoteListingComponent
import com.mifos.core.ui.components.MifosAlertDialog
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.components.MifosEmptyCard
import com.mifos.core.ui.components.MifosErrorComponent
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.util.DevicePreview
import com.mifos.core.ui.util.EventsEffect
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
internal fun NoteScreen(
    onNavigateBack: () -> Unit,
    onNavigateAddEditNote: (Int, String?, Long?) -> Unit,
    navController: NavController,
    viewModel: NoteViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            NoteEvent.NavigateBack -> onNavigateBack()
            NoteEvent.NavigateAddNote -> onNavigateAddEditNote(
                state.resourceId,
                state.resourceType,
                null,
            )

            NoteEvent.NavigateEditNote -> onNavigateAddEditNote(
                state.resourceId,
                state.resourceType,
                state.expandedNoteId,
            )
        }
    }

    NoteScreenScaffold(
        state = state,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
        navController = navController,
    )

    NoteScreenDialog(
        state = state,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
    )
}

@Composable
private fun NoteScreenDialog(
    state: NoteState,
    onAction: (NoteAction) -> Unit,
) {
    when (state.dialogState) {
        is NoteState.DialogState.Error -> {
            MifosErrorComponent(
                isNetworkConnected = state.networkConnection,
                message = state.dialogState.message,
                isRetryEnabled = true,
                onRetry = { onAction(NoteAction.OnRetry) },
            )
        }

        NoteState.DialogState.Loading -> {
            MifosProgressIndicator()
        }

        NoteState.DialogState.ShowDialog -> {
            MifosAlertDialog(
                onDismissRequest = {
                    onAction(NoteAction.DismissDialog)
                },
                onConfirmation = {
                    onAction(NoteAction.DeleteNote)
                },
                confirmationText = stringResource(Res.string.feature_note_delete),
                dialogTitle = stringResource(Res.string.feature_note_delete_note),
                dialogText = stringResource(Res.string.feature_note_delete_note_confirmation),
            )
        }
        null -> Unit
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NoteScreenScaffold(
    state: NoteState,
    navController: NavController,
    modifier: Modifier = Modifier,
    onAction: (NoteAction) -> Unit,
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val pullRefreshState = rememberPullToRefreshState()

    MifosScaffold(
        title = stringResource(Res.string.feature_note_notes),
        onBackPressed = {
            onAction(NoteAction.NavigateBack)
        },
        snackbarHostState = snackBarHostState,
        modifier = modifier,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
        ) {
            MifosBreadcrumbNavBar(navController)
            PullToRefreshBox(
                state = pullRefreshState,
                modifier = Modifier.fillMaxSize(),
                isRefreshing = state.isRefreshing,
                onRefresh = { onAction(NoteAction.OnRefresh) },
            ) {
                if (state.dialogState !is NoteState.DialogState.Loading &&
                    state.dialogState !is NoteState.DialogState.Error
                ) {
                    NoteContent(
                        state = state,
                        onAction = onAction,
                    )
                }
            }
        }
    }
}

@Composable
private fun NoteContent(
    onAction: (NoteAction) -> Unit,
    state: NoteState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = Modifier.padding(horizontal = DesignToken.spacing.large),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = stringResource(Res.string.feature_note_notes),
                    style = MifosTypography.titleMediumEmphasized,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Text(
                    text = "${state.notes.size} ${stringResource(Res.string.feature_note_item)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }

            Icon(
                imageVector = MifosIcons.Add,
                contentDescription = null,
                modifier.clickable {
                    onAction(NoteAction.OnClickAddScreen)
                }.size(DesignToken.sizes.iconAverage),
            )
        }

        Spacer(modifier = Modifier.height(DesignToken.spacing.large))

        if (state.notes.isEmpty()) {
            MifosEmptyCard(
                msg = stringResource(Res.string.feature_note_add_item),
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.small),
            ) {
                items(state.notes.reversed()) { note ->

                    MifosActionsNoteListingComponent(
                        notes = note.note ?: "Empty",
                        createdBy = note.createdByUsername ?: "Empty",
                        date = DateHelper.formatIsoDateToDdMmYyyy(note.createdOn ?: "Not found"),
                        isExpanded = state.expandedNoteId == note.id,
                        onExpand = {
                            onAction(NoteAction.OnToggleExpanded(note.id))
                        },
                        menuList = listOf(
                            Actions.Edit(
                                vectorResource(Res.drawable.edit),
                            ),
                            Actions.Delete(
                                vectorResource(Res.drawable.delete_document),
                            ),
                        ),
                        onActionClicked = { actions ->
                            when (actions) {
                                is Actions.Edit -> {
                                    onAction(NoteAction.OnClickEditScreen)
                                }

                                is Actions.Delete -> {
                                    onAction(NoteAction.ShowDialog)
                                }

                                else -> null
                            }
                        },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
internal val demoNotes = listOf(
    Note(
        id = 1,
        clientId = 101,
        note = "This is the first demo note.",
        createdById = 1001,
        createdByUsername = "creator_1",
        createdOn = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString(),
        updatedById = 1002,
        updatedByUsername = "updater_1",
        updatedOn = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString(),
    ),
    Note(
        id = 2,
        clientId = 102,
        note = "This is the second demo note.",
        createdById = 1003,
        createdByUsername = "creator_2",
        createdOn = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString(),
        updatedById = 1004,
        updatedByUsername = "updater_2",
        updatedOn = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString(),
    ),
    Note(
        id = 3,
        clientId = 103,
        note = "This is the third demo note.",
        createdById = 1005,
        createdByUsername = "creator_3",
        createdOn = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString(),
        updatedById = 1006,
        updatedByUsername = "updater_3",
        updatedOn = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString(),
    ),
)

@DevicePreview
@Composable
fun PreviewSuccessNoteScreen() {
    NoteScreenScaffold(
        onAction = {},
        state = NoteState(notes = demoNotes),
        navController = rememberNavController(),
    )
}
