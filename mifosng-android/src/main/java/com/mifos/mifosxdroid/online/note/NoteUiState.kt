package com.mifos.mifosxdroid.online.note

import com.mifos.objects.noncore.Note

/**
 * Created by Aditya Gupta on 08/08/23.
 */
sealed class NoteUiState {

    object ShowProgressbar : NoteUiState()

    object ShowResetVisibility : NoteUiState()

    data class ShowError(val message: Int) : NoteUiState()

    data class ShowNote(val note: List<Note>) : NoteUiState()

    object ShowEmptyNotes : NoteUiState()
}
