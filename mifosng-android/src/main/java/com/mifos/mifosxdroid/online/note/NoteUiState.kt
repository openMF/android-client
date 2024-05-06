package com.mifos.mifosxdroid.online.note

import com.mifos.core.objects.noncore.Note

/**
 * Created by Aditya Gupta on 08/08/23.
 */
sealed class NoteUiState {

    data object ShowProgressbar : NoteUiState()

    data object ShowResetVisibility : NoteUiState()

    data class ShowError(val message: Int) : NoteUiState()

    data class ShowNote(val note: List<Note>) : NoteUiState()

    data object ShowEmptyNotes : NoteUiState()
}
