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
import androidclient.feature.note.generated.resources.feature_note_add_item
import androidclient.feature.note.generated.resources.feature_note_createdBy
import androidclient.feature.note.generated.resources.feature_note_date
import androidclient.feature.note.generated.resources.feature_note_delete
import androidclient.feature.note.generated.resources.feature_note_delete_note
import androidclient.feature.note.generated.resources.feature_note_delete_note_confirmation
import androidclient.feature.note.generated.resources.feature_note_edit_note
import androidclient.feature.note.generated.resources.feature_note_item
import androidclient.feature.note.generated.resources.feature_note_no_item_found
import androidclient.feature.note.generated.resources.feature_note_note
import androidclient.feature.note.generated.resources.feature_note_notes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.designsystem.component.MifosCard
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.model.objects.notes.Note
import com.mifos.core.ui.components.MifosAlertDialog
import com.mifos.core.ui.components.MifosErrorComponent
import com.mifos.core.ui.util.DevicePreview
import com.mifos.core.ui.util.EventsEffect
import kotlinx.datetime.Clock
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun NoteScreen(
    onNavigateBack: () -> Unit,
    onNavigateNext: (Int, String?) -> Unit,
    viewModel: NoteViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            NoteEvent.NavigateBack -> onNavigateBack()
            NoteEvent.NavigateNext -> onNavigateNext(state.entityId, state.entityType)
        }
    }

    if (!state.isDeleteError) {
        NoteScreen(
            state = state,
            onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
        )
    }

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
            MifosCircularProgress()
        }

        null -> Unit
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NoteScreen(
    onAction: (NoteAction) -> Unit,
    state: NoteState,
    modifier: Modifier = Modifier,
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
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
        ) {
            PullToRefreshBox(
                state = pullRefreshState,
                modifier = Modifier.fillMaxSize(),
                isRefreshing = state.isRefreshing,
                onRefresh = { onAction(NoteAction.OnRefresh) },
            ) {
                NoteContent(
                    state = state,
                    onAction = onAction,
                )
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
                    onAction(NoteAction.OnNext)
                }.size(DesignToken.sizes.iconAverage),
            )
        }

        LazyColumn(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.small),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = DesignToken.spacing.largeIncreased),
        ) {
            if (state.notes.isEmpty()) {
                item {
                    MifosCard(
                        modifier = Modifier.fillMaxWidth().border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = DesignToken.shapes.medium,
                        ),
                        elevation = DesignToken.spacing.none,
                        shape = DesignToken.shapes.medium,
                    ) {
                        Column(
                            modifier = Modifier.padding(DesignToken.spacing.large),
                            verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.medium),
                        ) {
                            Text(
                                text = stringResource(Res.string.feature_note_no_item_found),
                                style = MifosTypography.titleSmallEmphasized,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = stringResource(Res.string.feature_note_add_item),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }
            } else {
                items(state.notes) { note ->
                    NoteItem(
                        id = note.id,
                        note = note.note,
                        createdByUsername = note.createdByUsername,
                        createdOn = note.createdOn,
                        onAction = onAction,
                        state = state,
                    )
                }
            }
        }
    }
}

@Composable
private fun NoteItem(
    id: Long?,
    note: String?,
    onAction: (NoteAction) -> Unit,
    createdByUsername: String?,
    createdOn: Long?,
    state: NoteState,
) {
    var shape by remember { mutableStateOf(RoundedCornerShape(0.dp)) }

    shape = if (state.expandedNoteId == id) {
        DesignToken.shapes.topMedium as RoundedCornerShape
    } else {
        DesignToken.shapes.medium as RoundedCornerShape
    }

    Column {
        MifosCard(
            modifier = Modifier.fillMaxWidth().border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = shape,
            ),
            shape = shape,
            elevation = DesignToken.spacing.none,
        ) {
            Column(
                modifier = Modifier.padding(DesignToken.spacing.large),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(Res.string.feature_note_createdBy) + " " + createdByUsername,
                        style = MifosTypography.titleSmallEmphasized,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    Icon(
                        imageVector = MifosIcons.MoreHoriz,
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            id?.let { onAction(NoteAction.OnToggleExpanded(id)) }
                        }.size(DesignToken.sizes.iconAverage),
                    )
                }

                Spacer(
                    modifier = Modifier.height(DesignToken.spacing.large),
                )

                Column {
                    Text(
                        text = stringResource(Res.string.feature_note_date),
                        style = MifosTypography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary,
                    )

                    Text(
                        text = DateHelper.getDateAsStringFromLong(createdOn!!),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }

                Spacer(
                    modifier = Modifier.height(DesignToken.spacing.medium),
                )

                Column {
                    Text(
                        text = stringResource(Res.string.feature_note_note),
                        style = MifosTypography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary,
                    )

                    if (note != null) {
                        Text(
                            text = note,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }
        }
        AnimatedVisibility(
            visible = state.expandedNoteId == id,
        ) {
            ContextualActions(
                id = id,
                onAction = onAction,
                state = state,
            )
        }
    }
}

@Composable
private fun ContextualActions(
    state: NoteState,
    id: Long?,
    onAction: (NoteAction) -> Unit,
) {
    MifosCard(
        modifier = Modifier.fillMaxWidth(),
        shape = DesignToken.shapes.bottomMedium,
        colors = CardDefaults.cardColors(
            MaterialTheme.colorScheme.surfaceContainer,
        ),
        elevation = DesignToken.spacing.none,
    ) {
        Column(
            modifier = Modifier.padding(DesignToken.spacing.large),
            verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.medium),
        ) {
            Row(
                modifier = Modifier.clickable {},
                horizontalArrangement = Arrangement.spacedBy(
                    DesignToken.spacing.medium,
                ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = MifosIcons.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(DesignToken.sizes.iconMedium),
                )
                Text(
                    text = stringResource(Res.string.feature_note_edit_note),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            Row(
                modifier = Modifier.clickable {
                    onAction(NoteAction.ShowDialog)
                },
                horizontalArrangement = Arrangement.spacedBy(
                    DesignToken.spacing.medium,
                ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = MifosIcons.Delete,
                    contentDescription = null,
                    modifier = Modifier.size(DesignToken.sizes.iconMedium),
                )

                Text(
                    text = stringResource(Res.string.feature_note_delete_note),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }

    if (state.showDialog) {
        MifosAlertDialog(
            onDismissRequest = {
                onAction(NoteAction.DismissDialog)
            },
            onConfirmation = {
                onAction(NoteAction.DeleteNote(id))
            },
            confirmationText = stringResource(Res.string.feature_note_delete),
            dialogTitle = stringResource(Res.string.feature_note_delete_note),
            dialogText = stringResource(Res.string.feature_note_delete_note_confirmation),
        )
    }
}

internal val demoNotes = listOf(
    Note(
        id = 1,
        clientId = 101,
        note = "This is the first demo note.",
        createdById = 1001,
        createdByUsername = "creator_1",
        createdOn = Clock.System.now().toEpochMilliseconds(),
        updatedById = 1002,
        updatedByUsername = "updater_1",
        updatedOn = Clock.System.now().toEpochMilliseconds(),
    ),
    Note(
        id = 2,
        clientId = 102,
        note = "This is the second demo note.",
        createdById = 1003,
        createdByUsername = "creator_2",
        createdOn = Clock.System.now().toEpochMilliseconds(),
        updatedById = 1004,
        updatedByUsername = "updater_2",
        updatedOn = Clock.System.now().toEpochMilliseconds(),
    ),
    Note(
        id = 3,
        clientId = 103,
        note = "This is the third demo note.",
        createdById = 1005,
        createdByUsername = "creator_3",
        createdOn = Clock.System.now().toEpochMilliseconds(),
        updatedById = 1006,
        updatedByUsername = "updater_3",
        updatedOn = Clock.System.now().toEpochMilliseconds(),
    ),
)

@DevicePreview
@Composable
fun PreviewSuccessNoteScreen() {
    NoteScreen(
        onAction = {},
        state = NoteState(notes = demoNotes),
    )
}
