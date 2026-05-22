/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.store

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import template.core.base.ui.screen.ScreenStateDefaults
import template.core.base.ui.screen.ScreenStateEmpty
import template.core.base.ui.screen.ScreenStateError
import template.core.base.ui.screen.ScreenStateLoading
import template.core.base.ui.screen.ScreenStateNoNetwork

/**
 * App-wide [ScreenStateDefaults] for `ScreenContent` / `PagingScreenContent`.
 * Wired into [MifosTheme] via `CompositionLocalProvider(LocalScreenStateDefaults)`
 * so every themed screen inherits these defaults.
 *
 * Customize visuals, copy, and telemetry hooks here.
 */
@Composable
fun appScreenStateDefaults(): ScreenStateDefaults = remember {
    ScreenStateDefaults(
        loading = ScreenStateLoading.Skeleton(rowCount = 5),
        empty = ScreenStateEmpty(
            title = "Nothing here yet",
            message = "When you have data, it'll show up here.",
        ),
        error = ScreenStateError(messageFor = ::mapErrorToUserMessage),
        noNetwork = ScreenStateNoNetwork(
            message = "You're offline. We'll retry when you reconnect.",
        ),
    )
}
