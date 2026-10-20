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

import androidx.compose.runtime.Composable
import kpt.core.designsystem.theme.KptTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/*
 * @Preview siblings for the device-free CMP render tier (SCREENSHOT_TEST.md CMP-PRIMARY).
 *
 * `AddEditNoteContent` is the stateless half; the `*Screen` entry composable resolves its
 * ViewModel through Koin and is therefore not previewed.
 *
 * Every literal here is PREVIEW FIXTURE DATA carrying `// i18n:skip`. `AddEditNoteState.text`
 * holds what a field officer TYPED — note content, not app copy — so a fixture for it is sample
 * user input, and translating sample input would be meaningless.
 *
 * These five cover the states the screen can actually be in, and three are states a single
 * "happy" fixture would never render.
 */

@Preview
@Composable
internal fun AddEditNoteContentAddPreview() {
    // Add mode with an empty field: submit must be DISABLED. The original let a blank note through
    // and the server rejected it — a round trip to be told nothing was typed.
    KptTheme {
        AddEditNoteContent(state = AddEditNoteState(), onAction = {})
    }
}

@Preview
@Composable
internal fun AddEditNoteContentEditPreview() {
    // Edit mode: title, label and button text all change off `editEnabled`. The original carried
    // three StringResource fields in its STATE to do this; here the screen picks its own copy.
    KptTheme {
        AddEditNoteContent(
            state = AddEditNoteState(
                text = "Follow up on the missed repayment.", // i18n:skip — preview fixture
                loadedText = "Follow up on the missed repayment.", // i18n:skip — preview fixture
                editEnabled = true,
            ),
            onAction = {},
        )
    }
}

@Preview
@Composable
internal fun AddEditNoteContentSubmittingPreview() {
    // In flight: submit disabled so a double-tap cannot send the note twice.
    KptTheme {
        AddEditNoteContent(
            state = AddEditNoteState(
                text = "Saving this one.", // i18n:skip — preview fixture
                isSubmitting = true,
            ),
            onAction = {},
        )
    }
}

@Preview
@Composable
internal fun AddEditNoteContentOfflineKeepsTheTextPreview() {
    // THE ONE WORTH LOOKING AT. The user's text is the only thing that exists nowhere else — not
    // on the server, not in the cache. The error renders UNDER the field with the text still above
    // it, and the offline wording says "try again", not "unexpected error".
    KptTheme {
        AddEditNoteContent(
            state = AddEditNoteState(
                text = "Client was not at the shop; will revisit Thursday.", // i18n:skip — preview fixture
                writeError = NoteWriteError.Offline,
            ),
            onAction = {},
        )
    }
}

/*
 * NOT PREVIEWED: the discard-confirmation dialog.
 *
 * An `AlertDialog` composes into its own window, so the render tier sees TWO roots and
 * `onRoot()` in `NotePreviewScreenshotTest` is ambiguous — it fails with "Expected exactly 1
 * node but found 2". This is a property of the device-free tier, not of this screen: the
 * template has zero dialog previews across its 38 preview files for the same reason.
 *
 * The state is NOT uncovered. `AddEditNoteScreenUiTest.theDiscardWarningRendersWhenThereIsSomethingToLose`
 * asserts it through `runComposeUiTest`, which addresses nodes across roots happily. Dialogs
 * belong to the UI-test tier; the screenshot tier covers the screen body.
 */
