/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanLookup

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.github.takahirom.roborazzi.captureRoboImage
import org.junit.Test
import sergio.sastre.composable.preview.scanner.common.CommonComposablePreviewScanner

/**
 * Device-free CMP render tier (SCREENSHOT_TEST.md CMP-PRIMARY) for the loan-account lookup — the
 * officer home's entry into the loan vertical (pilot 04). Discovers [LoanAccountLookupScreenPreview]
 * via `CommonComposablePreviewScanner`, renders it off `desktopTest` (JVM — no emulator, no
 * Robolectric) and captures via roborazzi-compose-desktop. Drives `recordRoborazziDesktop` /
 * `verifyRoborazziDesktop`. Scoped to the `loanLookup` package so it renders only this screen,
 * not the whole (large) feature/loan tree.
 */
@OptIn(ExperimentalTestApi::class)
class LoanLookupPreviewScreenshotTest {

    @Test
    fun captureAllPreviews() {
        CommonComposablePreviewScanner()
            .scanPackageTrees("com.mifos.feature.loan.loanLookup")
            .getPreviews()
            .forEachIndexed { index, preview ->
                runDesktopComposeUiTest {
                    setContent { preview() }
                    onRoot().captureRoboImage(
                        "src/desktopTest/resources/screenshots/loanLookup/preview_$index.png",
                    )
                }
            }
    }
}
