/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package cmp.navigation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cmp.navigation.rootnav.RootNavScreen
import com.mifos.core.datastore.model.DarkThemeConfig
import com.mifos.core.designsystem.theme.MifosTheme
import com.mifos.core.ui.util.EventsEffect
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.mifos.authenticator.biometrics.BiometricStorageAdapter
import org.mifos.authenticator.biometrics.PlatformAuthenticatorCompositionProvider

/**
 * App-level composition entry point.
 *
 * Wraps the nav graph in [PlatformAuthenticatorCompositionProvider] so every
 * descendant can resolve the `platformAuthenticationProvider` /
 * `platformAvailableAuthenticationOption` CompositionLocals used by
 * `MifosPasscode`, `BiometricsKey`, `BiometricSetupScreen`, and the Settings
 * biometric toggles. The [BiometricStorageAdapter] is injected here once so
 * the library can build a single scoped `PlatformAuthenticationProvider`.
 */
@Composable
fun ComposeApp(
    handleThemeMode: (osValue: Int) -> Unit,
    handleAppLocale: (locale: String?) -> Unit,
    onSplashScreenRemoved: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ComposeAppViewModel = koinViewModel(),
) {
    val uiState by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(eventFlow = viewModel.eventFlow) { event ->
        when (event) {
            is AppEvent.ShowToast -> {}
            is AppEvent.UpdateAppLocale -> handleAppLocale(event.localeName)
            is AppEvent.UpdateAppTheme -> handleThemeMode(event.osValue)
        }
    }

    val darkTheme = when (uiState.darkThemeConfig) {
        DarkThemeConfig.DARK -> true
        DarkThemeConfig.LIGHT -> false
        DarkThemeConfig.FOLLOW_SYSTEM -> isSystemInDarkTheme()
    }

    PlatformAuthenticatorCompositionProvider(
        biometricStorageAdapter = koinInject<BiometricStorageAdapter>(),
    ) {
        MifosTheme(
            darkTheme = darkTheme,
            androidTheme = uiState.isAndroidTheme,
            shouldDisplayDynamicTheming = uiState.isDynamicColorsEnabled,
        ) {
            RootNavScreen(
                modifier = modifier,
                onSplashScreenRemoved = onSplashScreenRemoved,
            )
        }
    }
}
