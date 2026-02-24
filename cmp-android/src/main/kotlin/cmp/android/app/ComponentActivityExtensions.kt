/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package cmp.android.app

import android.content.res.Configuration
import android.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.util.Consumer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.mifos.core.datastore.model.DarkThemeConfig
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@ColorInt
private val SCRIM_COLOR: Int = Color.TRANSPARENT

/**
 * Helper method to handle edge-to-edge logic for dark mode.
 *
 * This logic is from the Now-In-Android app found
 * [here](https://github.com/android/nowinandroid/blob/689ef92e41427ab70f82e2c9fe59755441deae92/app/src/main/kotlin/com/google/samples/apps/nowinandroid/MainActivity.kt#L94).
 */
fun ComponentActivity.setupEdgeToEdge(appThemeFlow: Flow<DarkThemeConfig>) {
    lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(
            state = Lifecycle.State.STARTED,
        ) {
            combine(
                isSystemInDarkModeFlow(),
                appThemeFlow,
            ) { _, appTheme ->
                AppCompatDelegate.setDefaultNightMode(appTheme.osValue)
            }
                .distinctUntilChanged()
                .collect {
                    val style =
                        SystemBarStyle
                            .auto(
                                darkScrim = SCRIM_COLOR,
                                lightScrim = SCRIM_COLOR,
                                detectDarkMode = { false },
                            )

                    enableEdgeToEdge(
                        statusBarStyle = style,
                        navigationBarStyle = style,
                    )
                }
        }
    }
}

/**
 * Emits whether the system is currently in dark mode.
 */
private fun ComponentActivity.isSystemInDarkModeFlow(): Flow<Boolean> =
    callbackFlow {
        trySend(resources.configuration.isSystemInDarkMode)

        val listener =
            Consumer<Configuration> { configuration ->
                trySend(
                    configuration.isSystemInDarkMode,
                )
            }

        addOnConfigurationChangedListener(listener)

        awaitClose {
            removeOnConfigurationChangedListener(listener)
        }
    }
        .distinctUntilChanged()
        .conflate()
