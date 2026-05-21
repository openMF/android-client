/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
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
 * !! FORK CUSTOMIZATION POINT !!
 *
 * App-wide [ScreenStateDefaults] for `template.core.base.ui.ScreenContent` and
 * `template.core.base.ui.PagingScreenContent`. Wired into [MifosTheme] (follow-up:
 * thread `LocalScreenStateDefaults provides appScreenStateDefaults()` into the existing
 * `core/designsystem` MifosTheme composable) — every screen wrapped by `MifosTheme`
 * then automatically picks up these defaults. No per-screen wiring required.
 *
 * Do NOT edit `core-base/store` or `core-base/ui` — they're framework-shared and sync
 * cleanly from `kmp-project-template`. Customize here instead.
 *
 * Branding hooks for later:
 * - Lottie animations (once `ScreenStateVisual.Lottie(spec = DefaultLottieAnimations.empty)`
 *   bundled JSONs land in `core-base/ui` composeResources)
 * - Telemetry via [ScreenStateError.onShown]
 * - Localized strings via composeResources
 */
@Composable
fun appScreenStateDefaults(): ScreenStateDefaults = remember {
    ScreenStateDefaults(
        loading = ScreenStateLoading.Skeleton(rowCount = 5),
        empty = ScreenStateEmpty(
            title = "Nothing here yet",
            message = "When you have data, it'll show up here.",
        ),
        error = ScreenStateError(
            messageFor = ::mapErrorToUserMessage,
            // Telemetry hook for later, e.g.
            //   onShown = { error -> Analytics.recordError("screen_state_error", error) },
        ),
        noNetwork = ScreenStateNoNetwork(
            message = "You're offline. We'll retry when you reconnect.",
        ),
    )
}
