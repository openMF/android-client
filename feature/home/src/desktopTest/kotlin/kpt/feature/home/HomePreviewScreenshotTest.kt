/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package kpt.feature.home

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.github.takahirom.roborazzi.captureRoboImage
import org.junit.Test
import sergio.sastre.composable.preview.scanner.common.CommonComposablePreviewScanner

/**
 * Device-free CMP render tier (SCREENSHOT_TEST.md CMP-PRIMARY) for `feature/home`. Auto-discovers
 * every commonMain `@Preview` (incl. [HomeDashboardPreview] — the officer board with the LOANS
 * entry tile) via `CommonComposablePreviewScanner`, renders each off `desktopTest` (JVM — no
 * emulator, no Robolectric) and captures via roborazzi-compose-desktop. Drives
 * `recordRoborazziDesktop` / `verifyRoborazziDesktop`.
 *
 * Mirrors `feature/settings`'s `SettingsPreviewScreenshotTest` (the template's demonstrator): the
 * `roborazzi-compose-preview-scanner-support` bridge is Android/Robolectric-only, so the desktop
 * path renders each discovered preview manually inside its own `runDesktopComposeUiTest` block.
 */
@OptIn(ExperimentalTestApi::class)
class HomePreviewScreenshotTest {

    @Test
    fun captureAllPreviews() {
        CommonComposablePreviewScanner()
            .scanPackageTrees("kpt.feature.home")
            .getPreviews()
            .forEachIndexed { index, preview ->
                runDesktopComposeUiTest {
                    setContent { preview() }
                    onRoot().captureRoboImage(
                        "src/desktopTest/resources/screenshots/home/preview_$index.png",
                    )
                }
            }
    }
}
