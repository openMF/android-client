package com.mifos.feature.note

import android.widget.Toast
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
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.common.utils.Network
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.objects.noncore.Note
import com.mifos.core.ui.components.MifosEmptyUi

@Composable
fun NoteScreen(
    entityId: Int,
    entityType: String?,
    viewModel: NoteViewModel = hiltViewModel(),
    onBackPressed: () -> Unit
) {
    val uiState by viewModel.noteUiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.loadNote(
            type = entityType,
            id = entityId
        )
    }

    NoteScreen(
        uiState = uiState,
        onBackPressed = onBackPressed,
        refresh = { viewModel.refresh(entityType, entityId) },
        isRefreshing = isRefreshing
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteScreen(
    isRefreshing: Boolean,
    refresh: () -> Unit,
    uiState: NoteUiState,
    onBackPressed: () -> Unit,
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val pullRefreshState = rememberPullToRefreshState()
    val context = LocalContext.current

    MifosScaffold(
        icon = MifosIcons.arrowBack,
        title = stringResource(id = R.string.feature_note_Note),
        onBackPressed = onBackPressed,
        snackbarHostState = snackBarHostState
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .nestedScroll(pullRefreshState.nestedScrollConnection)
        ) {
            when (uiState) {
                NoteUiState.ShowProgressbar -> {
                    MifosCircularProgress()
                }

                NoteUiState.ShowEmptyNotes -> {
                    MifosEmptyUi(text = stringResource(id = R.string.feature_note_no_notes_found))
                }

                is NoteUiState.ShowError -> {
                    MifosSweetError(
                        message = stringResource(id = uiState.message),
                        onclick = refresh
                    )
                }

                is NoteUiState.ShowNote -> {
                    NoteContent(uiState.note)
                }
            }

            PullToRefreshContainer(
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }

        LaunchedEffect(key1 = isRefreshing) {
            if (isRefreshing)
                pullRefreshState.startRefresh()
        }

        LaunchedEffect(key1 = pullRefreshState.isRefreshing) {
            if (pullRefreshState.isRefreshing) {
                if (Network.isOnline(context)) {
                    refresh()
                } else {
                    Toast.makeText(
                        context,
                        context.resources.getText(R.string.feature_note_internet_not_connected),
                        Toast.LENGTH_SHORT,
                    ).show()
                }
                pullRefreshState.endRefresh()
            }
        }

    }
}


@Composable
fun NoteContent(
    notes: List<Note>
) {
    LazyColumn {
        items(notes) { note ->
            note.noteContent?.let { NoteItem(noteTitle = it) }
        }
    }
}

@Composable
fun NoteItem(
    noteTitle: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 4.dp,
                vertical = 4.dp
            ),
        shape = RoundedCornerShape(0.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Text(
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 16.dp
            ),
            style = MaterialTheme.typography.bodyLarge,
            text = noteTitle
        )
    }
}

class NoteScreenPreviewProvider : PreviewParameterProvider<NoteUiState> {
    val demoNotes = listOf(
        Note(
            id = 1,
            clientId = 101,
            noteContent = "This is the first demo note.",
            createdById = 1001,
            createdByUsername = "creator_1",
            createdOn = System.currentTimeMillis(),
            updatedById = 1002,
            updatedByUsername = "updater_1",
            updatedOn = System.currentTimeMillis()
        ),
        Note(
            id = 2,
            clientId = 102,
            noteContent = "This is the second demo note.",
            createdById = 1003,
            createdByUsername = "creator_2",
            createdOn = System.currentTimeMillis(),
            updatedById = 1004,
            updatedByUsername = "updater_2",
            updatedOn = System.currentTimeMillis()
        ),
        Note(
            id = 3,
            clientId = 103,
            noteContent = "This is the third demo note.",
            createdById = 1005,
            createdByUsername = "creator_3",
            createdOn = System.currentTimeMillis(),
            updatedById = 1006,
            updatedByUsername = "updater_3",
            updatedOn = System.currentTimeMillis()
        )
    )

    override val values: Sequence<NoteUiState>
        get() = sequenceOf(
            NoteUiState.ShowEmptyNotes,
            NoteUiState.ShowNote(demoNotes),
            NoteUiState.ShowProgressbar,
            NoteUiState.ShowError(R.string.feature_note_failed_to_fetch_notes)
        )
}

@Composable
@Preview(showSystemUi = true)
fun PreviewNoteScreen(
    @PreviewParameter(NoteScreenPreviewProvider::class) noteUiState: NoteUiState
) {
    NoteScreen(
        isRefreshing = false,
        refresh = { },
        uiState = noteUiState,
        onBackPressed = {}
    )
}
