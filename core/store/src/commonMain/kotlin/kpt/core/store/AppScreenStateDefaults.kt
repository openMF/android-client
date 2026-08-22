/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package kpt.core.store

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kpt.core.base.ui.screen.DefaultLottieAnimations
import kpt.core.base.ui.screen.ScreenStateDefaults
import kpt.core.base.ui.screen.ScreenStateEmpty
import kpt.core.base.ui.screen.ScreenStateError
import kpt.core.base.ui.screen.ScreenStateLoading
import kpt.core.base.ui.screen.ScreenStateNoNetwork
import kpt.core.base.ui.screen.ScreenStateVisual
import kpt.core.store.generated.resources.Res
import kpt.core.store.generated.resources.screenstate_captive_action
import kpt.core.store.generated.resources.screenstate_captive_message
import kpt.core.store.generated.resources.screenstate_empty_message
import kpt.core.store.generated.resources.screenstate_empty_title
import kpt.core.store.generated.resources.screenstate_error_retry
import kpt.core.store.generated.resources.screenstate_error_title
import kpt.core.store.generated.resources.screenstate_nonet_message
import kpt.core.store.generated.resources.screenstate_nonet_retry
import org.jetbrains.compose.resources.stringResource

/**
 * !! THIS IS THE FORK CUSTOMIZATION POINT !!
 *
 * Mirrors the template's `kpt.core.store.AppScreenStateDefaults` shape verbatim
 * (offline-first-template-migration 02-store-infra-screenstate — surfaced as a
 * compile-blocking gap alongside T3: `kpt.core.designsystem.theme.KptTheme` calls this
 * function directly, so `core/designsystem` cannot compile without it), kept at the
 * template's own `kpt.core.store` package (infra/framework-level seam — kpt is the
 * fixed base namespace, not the fork's `com.mifos.*` business-logic namespace).
 *
 * Edit this file to brand field-officer-app's empty / error / no-network / loading
 * visuals. Do NOT edit `core-base/store` or `core-base/ui` — they're framework-shared.
 *
 * App-wide [ScreenStateDefaults] for `kpt.core.base.ui.ScreenContent` and
 * `kpt.core.base.ui.PagingScreenContent`. Wired into `KptTheme` (core/designsystem) —
 * every screen wrapped by `KptTheme` automatically picks up these defaults via
 * `LocalScreenStateDefaults`. No per-screen wiring required.
 *
 * Reuses `core/store`'s own `AppErrorMapper.rememberAppErrorMessageFor()` (T3) for
 * localized, per-category error copy — same `strings.xml` resource keys the template
 * ships (already synced into `core/store`'s composeResources in Phase 1).
 */
@Composable
fun appScreenStateDefaults(): ScreenStateDefaults {
    val emptyTitle = stringResource(Res.string.screenstate_empty_title)
    val emptyMessage = stringResource(Res.string.screenstate_empty_message)
    val errorTitle = stringResource(Res.string.screenstate_error_title)
    val errorRetry = stringResource(Res.string.screenstate_error_retry)
    val nonetMessage = stringResource(Res.string.screenstate_nonet_message)
    val nonetRetry = stringResource(Res.string.screenstate_nonet_retry)
    val captiveMessage = stringResource(Res.string.screenstate_captive_message)
    val captiveAction = stringResource(Res.string.screenstate_captive_action)

    val errorMessageFor = rememberAppErrorMessageFor()

    return remember(
        emptyTitle,
        emptyMessage,
        errorTitle,
        errorRetry,
        nonetMessage,
        nonetRetry,
        captiveMessage,
        captiveAction,
        errorMessageFor,
    ) {
        ScreenStateDefaults(
            loading = ScreenStateLoading.Skeleton(rowCount = 5),
            empty = ScreenStateEmpty(
                visual = ScreenStateVisual.Lottie(spec = DefaultLottieAnimations.empty),
                title = emptyTitle,
                message = emptyMessage,
            ),
            error = ScreenStateError(
                visual = ScreenStateVisual.Lottie(spec = DefaultLottieAnimations.error),
                title = errorTitle,
                messageFor = errorMessageFor,
                retryText = errorRetry,
                // TODO(fork): wire telemetry, e.g.
                //   onShown = { error -> AppTelemetry.recordError("screen_state_error", error) },
            ),
            noNetwork = ScreenStateNoNetwork(
                message = nonetMessage,
                captivePortalMessage = captiveMessage,
                captivePortalActionText = captiveAction,
                retryText = nonetRetry,
            ),
        )
    }
}
