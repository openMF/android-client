/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.feature.note.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import kpt.core.designsystem.theme.KptTheme
import kotlin.test.Test

/**
 * Compose Multiplatform UI test for [AddEditNoteContent] (RULE-KMP-COMPOSE-UITEST-001 CU-1..CU-3).
 *
 * Renders the stateless half directly — no Koin, no ViewModel. The state machine is covered by
 * [AddEditNoteViewModelTest]; these pin what the user can SEE and TOUCH in each state.
 */
@OptIn(ExperimentalTestApi::class)
class AddEditNoteScreenUiTest {

    @Test
    fun aBlankNoteCannotBeSubmitted() = runComposeUiTest {
        setContent {
            KptTheme { AddEditNoteContent(state = AddEditNoteState(), onAction = {}) }
        }
        // Disabled, not hidden. The user must be able to see what the screen is FOR before they
        // have typed anything.
        onNodeWithTag(TestTags.AddEditNote.SUBMIT).assertIsNotEnabled()
        onNodeWithTag(TestTags.AddEditNote.FIELD).assertIsDisplayed()
    }

    @Test
    fun typingEnablesSubmit() = runComposeUiTest {
        setContent {
            KptTheme {
                AddEditNoteContent(
                    state = AddEditNoteState(text = "visited the shop"), // i18n:skip — test fixture
                    onAction = {},
                )
            }
        }
        onNodeWithTag(TestTags.AddEditNote.SUBMIT).assertIsEnabled()
    }

    @Test
    fun submitIsDisabledWhileAWriteIsInFlight() = runComposeUiTest {
        setContent {
            KptTheme {
                AddEditNoteContent(
                    state = AddEditNoteState(
                        text = "sending", // i18n:skip — test fixture
                        isSubmitting = true,
                    ),
                    onAction = {},
                )
            }
        }
        // A double-tap must not send the note twice.
        onNodeWithTag(TestTags.AddEditNote.SUBMIT).assertIsNotEnabled()
    }

    @Test
    fun aFailedWriteKeepsTheUsersTextOnScreen() = runComposeUiTest {
        setContent {
            KptTheme {
                AddEditNoteContent(
                    state = AddEditNoteState(
                        text = "do not lose me", // i18n:skip — test fixture
                        writeError = NoteWriteError.Offline,
                    ),
                    onAction = {},
                )
            }
        }
        // THE ONE THAT MATTERS, as a render. The note is not on the server and not in the cache —
        // if the error clears the field it is simply gone. The error and the text must coexist.
        onNodeWithTag(TestTags.AddEditNote.WRITE_ERROR).assertIsDisplayed()
        // Both values: the field's semantics MERGE its label with its editable text, so asserting
        // the text alone fails against `[Write a Note, do not lose me]`.
        onNodeWithTag(TestTags.AddEditNote.FIELD)
            .assertTextEquals("Write a Note", "do not lose me") // i18n:skip — test fixture
    }

    @Test
    fun theDiscardWarningRendersWhenThereIsSomethingToLose() = runComposeUiTest {
        setContent {
            KptTheme {
                AddEditNoteContent(
                    state = AddEditNoteState(
                        text = "half a thought", // i18n:skip — test fixture
                        loadedText = "",
                        showDiscardConfirmation = true,
                    ),
                    onAction = {},
                )
            }
        }
        onNodeWithTag(TestTags.AddEditNote.DISCARD_DIALOG).assertIsDisplayed()
    }
}
