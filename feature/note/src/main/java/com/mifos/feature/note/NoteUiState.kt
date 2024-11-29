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

import com.mifos.core.objects.noncore.Note

/**
 * Created by Aditya Gupta on 08/08/23.
 */
sealed class NoteUiState {

    data object ShowProgressbar : NoteUiState()

    data class ShowError(val message: Int) : NoteUiState()

    data class ShowNote(val note: List<Note>) : NoteUiState()

    data object ShowEmptyNotes : NoteUiState()
}
