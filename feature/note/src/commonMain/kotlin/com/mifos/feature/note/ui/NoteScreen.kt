/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.note.ui

import androidclient.feature.note.generated.resources.Res
import androidclient.feature.note.generated.resources.delete_document
import androidclient.feature.note.generated.resources.edit
import androidclient.feature.note.generated.resources.feature_note_cd_add_note
import androidclient.feature.note.generated.resources.feature_note_delete
import androidclient.feature.note.generated.resources.feature_note_delete_note
import androidclient.feature.note.generated.resources.feature_note_delete_note_confirmation
import androidclient.feature.note.generated.resources.feature_note_empty_text_fallback
import androidclient.feature.note.generated.resources.feature_note_empty_user_fallback
import androidclient.feature.note.generated.resources.feature_note_failed_to_delete
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
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.data.store.DataFreshness
import com.mifos.core.data.store.ScreenState
import com.mifos.core.data.store.SubmitState
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.model.objects.note.Note
import com.mifos.core.ui.components.Actions
import com.mifos.core.ui.components.MifosActionsNoteListingComponent
import com.mifos.core.ui.components.MifosAlertDialog
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.util.DevicePreview
import com.mifos.core.ui.util.EventsEffect
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import template.core.base.designsystem.theme.KptTheme
import template.core.base.ui.screen.ScreenContent
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
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()
    val deleteState by viewModel.deleteState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

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

    // Surface delete failures as a snackbar; on success the VM auto-reloads the list.
    LaunchedEffect(deleteState) {
        if (deleteState is SubmitState.Failed) {
            snackbarHostState.showSnackbar(message = getString(Res.string.feature_note_failed_to_delete))
            viewModel.onDeleteConsumed()
        }
    }

    NoteScreenContent(
        state = state,
        screenState = screenState,
        snackbarHostState = snackbarHostState,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
        navController = navController,
    )

    if (state.showDeleteDialog) {
        MifosAlertDialog(
            onDismissRequest = { viewModel.trySendAction(NoteAction.DismissDeleteDialog) },
            onConfirmation = { viewModel.trySendAction(NoteAction.DeleteNote) },
            confirmationText = stringResource(Res.string.feature_note_delete),
            dialogTitle = stringResource(Res.string.feature_note_delete_note),
            dialogText = stringResource(Res.string.feature_note_delete_note_confirmation),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NoteScreenContent(
    state: NoteState,
    screenState: ScreenState<List<Note>>,
    snackbarHostState: SnackbarHostState,
    onAction: (NoteAction) -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val pullRefreshState = rememberPullToRefreshState()
    // Refresh indicator shows while Store5 is fetching the latest. `Loading` means
    // initial-load (no cache); fresh content with a network refetch in flight is
    // expressed by the `freshness == UPDATING` flag on `ScreenState.Content`.
    val isRefreshing = screenState is ScreenState.Loading ||
        (screenState is ScreenState.Content<*> && screenState.freshness == DataFreshness.UPDATING)

    MifosScaffold(
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            MifosBreadcrumbNavBar(navController)
            PullToRefreshBox(
                state = pullRefreshState,
                modifier = Modifier.fillMaxSize(),
                isRefreshing = isRefreshing,
                onRefresh = { onAction(NoteAction.OnRefresh) },
            ) {
                ScreenContent(
                    state = screenState,
                    onRetry = { onAction(NoteAction.OnRetry) },
                    modifier = Modifier.fillMaxSize(),
                ) { notes, _ ->
                    NoteListContent(notes = notes, state = state, onAction = onAction)
                }
            }
        }
    }
}

@Composable
private fun NoteListContent(
    notes: List<Note>,
    state: NoteState,
    onAction: (NoteAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(horizontal = KptTheme.spacing.md)
            .padding(bottom = KptTheme.spacing.md),
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
                    color = KptTheme.colorScheme.onSurface,
                )

                Text(
                    text = "${notes.size} ${stringResource(Res.string.feature_note_item)}",
                    style = KptTheme.typography.labelMedium,
                    color = KptTheme.colorScheme.secondary,
                )
            }

            Icon(
                imageVector = MifosIcons.Add,
                contentDescription = stringResource(Res.string.feature_note_cd_add_note),
                modifier = Modifier
                    .clickable { onAction(NoteAction.OnClickAddScreen) }
                    .size(DesignToken.sizes.iconAverage),
            )
        }

        Spacer(modifier = Modifier.height(KptTheme.spacing.md))

        val emptyTextFallback = stringResource(Res.string.feature_note_empty_text_fallback)
        val emptyUserFallback = stringResource(Res.string.feature_note_empty_user_fallback)

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm),
        ) {
            items(notes.reversed()) { note ->
                MifosActionsNoteListingComponent(
                    notes = note.note ?: emptyTextFallback,
                    createdBy = note.createdByUsername ?: emptyUserFallback,
                    date = DateHelper.formatIsoDateToDdMmYyyy(note.createdOn.orEmpty()),
                    isExpanded = state.expandedNoteId == note.id,
                    onExpand = { onAction(NoteAction.OnToggleExpanded(note.id)) },
                    menuList = listOf(
                        Actions.Edit(vectorResource(Res.drawable.edit)),
                        Actions.Delete(vectorResource(Res.drawable.delete_document)),
                    ),
                    onActionClicked = { actions ->
                        when (actions) {
                            is Actions.Edit -> onAction(NoteAction.OnClickEditScreen)
                            is Actions.Delete -> onAction(NoteAction.ShowDeleteDialog)
                            else -> Unit
                        }
                    },
                )
            }
        }
    }
}

// region Previews

@OptIn(ExperimentalTime::class)
private val demoNotes: List<Note> = listOf(
    Note(
        id = 1,
        clientId = 101,
        note = "First demo note.",
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
        note = "Second demo note.",
        createdById = 1003,
        createdByUsername = "creator_2",
        createdOn = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString(),
        updatedById = 1004,
        updatedByUsername = "updater_2",
        updatedOn = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString(),
    ),
)

@DevicePreview
@Composable
private fun PreviewNoteScreenContent() {
    NoteScreenContent(
        state = NoteState(),
        screenState = ScreenState.Content(data = demoNotes, freshness = DataFreshness.FRESH),
        snackbarHostState = remember { SnackbarHostState() },
        onAction = {},
        navController = rememberNavController(),
    )
}

// endregion
