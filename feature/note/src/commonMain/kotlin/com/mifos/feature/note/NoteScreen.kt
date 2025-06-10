/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.note

import androidclient.feature.note.generated.resources.Res
import androidclient.feature.note.generated.resources.feature_note_Note
import androidclient.feature.note.generated.resources.feature_note_no_notes_found
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.model.objects.Note
import com.mifos.core.ui.components.MifosEmptyUi
import com.mifos.core.ui.util.DevicePreview
import kotlinx.datetime.Clock
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun NoteScreen(
    onBackPressed: () -> Unit,
    viewModel: NoteViewModel = koinViewModel(),
) {
    val uiState by viewModel.noteUiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.loadNote()
    }

    NoteScreen(
        uiState = uiState,
        onBackPressed = onBackPressed,
        refresh = { viewModel.refresh() },
        isRefreshing = isRefreshing,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NoteScreen(
    isRefreshing: Boolean,
    refresh: () -> Unit,
    uiState: NoteUiState,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val pullRefreshState = rememberPullToRefreshState()

    MifosScaffold(
        title = stringResource(Res.string.feature_note_Note),
        onBackPressed = onBackPressed,
        snackbarHostState = snackBarHostState,
        modifier = modifier,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            PullToRefreshBox(
                state = pullRefreshState,
                modifier = Modifier.fillMaxSize(),
                isRefreshing = isRefreshing,
                onRefresh = refresh,
            ) {
                when (uiState) {
                    NoteUiState.ShowProgressbar -> {
                        MifosCircularProgress()
                    }

                    NoteUiState.ShowEmptyNotes -> {
                        MifosEmptyUi(text = stringResource(Res.string.feature_note_no_notes_found))
                    }

                    is NoteUiState.ShowError -> {
                        MifosSweetError(
                            message = stringResource(uiState.message),
                            onclick = refresh,
                        )
                    }

                    is NoteUiState.ShowNote -> {
                        NoteContent(uiState.note)
                    }
                }
            }
        }
    }
}

@Composable
private fun NoteContent(
    notes: List<Note>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
        items(notes) { note ->
            note.note?.let { NoteItem(noteTitle = it) }
        }
    }
}

@Composable
private fun NoteItem(
    noteTitle: String,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 4.dp,
                vertical = 4.dp,
            ),
        shape = RoundedCornerShape(0.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
    ) {
        Text(
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 16.dp,
            ),
            style = MaterialTheme.typography.bodyLarge,
            text = noteTitle,
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
        isRefreshing = false,
        refresh = {},
        uiState = NoteUiState.ShowNote(demoNotes),
        onBackPressed = {},
    )
}
