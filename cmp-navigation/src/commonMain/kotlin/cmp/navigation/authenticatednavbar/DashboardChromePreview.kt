/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package cmp.navigation.authenticatednavbar

import androidx.compose.runtime.Composable
import cmp.navigation.components.DashboardTopBar
import cmp.navigation.navigation.HomeDestinationsScreen
import kpt.core.designsystem.theme.KptTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/*
 * @Preview siblings for the device-free CMP render tier (SCREENSHOT_TEST.md CMP-PRIMARY).
 *
 * The dashboard chrome is exactly the kind of surface this tier exists for. Both of its defects are
 * RENDER defects — a bar drawn twice because the tab root already had one, a drawer divider landing
 * one row off — and neither is visible to a state assertion. Each was found by looking at a picture,
 * so each gets a committed picture to compare against.
 *
 * The stateless halves are previewed; the shell that resolves a ViewModel through Koin is not.
 */

@Preview
@Composable
internal fun DashboardTopBarPreview() {
    KptTheme {
        // No `title` argument: the bar reads the fork's own display name from AppInfo, so the
        // preview renders exactly what ships rather than a fixture string.
        DashboardTopBar(
            onNavigationIconClick = {},
            onSearchIconClick = {},
            onNotificationIconClick = {},
        )
    }
}

@Preview
@Composable
internal fun DashboardDrawerSheetPreview() {
    KptTheme {
        DashboardDrawerSheet(
            // Settings is the one migrated entry and the only one that can be selected today —
            // previewing it selected pins both the row styling and that the divider sits ABOVE the
            // last entry rather than beside the selection.
            selected = HomeDestinationsScreen.SettingsScreen,
            onEntryClick = {},
            onHeaderClick = {},
        )
    }
}

@Preview
@Composable
internal fun DashboardDrawerSheetNoSelectionPreview() {
    KptTheme {
        DashboardDrawerSheet(selected = null, onEntryClick = {}, onHeaderClick = {})
    }
}

@Preview
@Composable
internal fun PendingFeatureScreenPreview() {
    KptTheme {
        // i18n:skip — preview fixture; the real value is a HomeDestinationsScreen.featureName
        PendingFeatureScreen(featureName = "Centers")
    }
}
