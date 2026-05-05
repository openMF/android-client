/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.passcode.components

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.mifos.authenticator.biometrics.platformAuthenticator.PlatformAuthOptions
import org.mifos.authenticator.biometrics.platformAuthenticator.PlatformAvailableAuthenticationOption
import org.mifos.authenticator.passcode.components.PasscodeKey
import org.mifos.authenticator.passcode.screen.PasscodeKeyConfig
import template.core.base.designsystem.theme.KptTheme

/**
 * Platform-aware biometric unlock button for use inside the passcode screen's
 * `externalAuthButton` slot.
 *
 * Picks an icon based on the device's available authenticator options (read
 * reactively from [systemAvailableAuthOption]):
 *  - [PlatformAuthOptions.Fingerprint] → [Icons.Default.Fingerprint]
 *  - [PlatformAuthOptions.FaceId] → [Icons.Default.Face]
 *  - otherwise → [Icons.Default.Lock]
 *
 * Renders via the passcode library's [PasscodeKey] so the visual style matches
 * the keypad. The composable is intentionally VM-free: callers wire [onClick]
 * to a ViewModel action that invokes
 * `PlatformAuthenticationProvider.onAuthenticatorClick(...)`.
 *
 * @param systemAvailableAuthOption Source of truth for which authenticators
 *        are usable on the current device; drives the icon.
 * @param onClick Invoked on tap. Expected to kick off the biometric prompt.
 * @param modifier Forwarded to the underlying [PasscodeKey].
 */
@Composable
fun BiometricsKey(
    systemAvailableAuthOption: PlatformAvailableAuthenticationOption,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val authOptions by systemAvailableAuthOption.currentAuthOption.collectAsStateWithLifecycle()

    val passcodeKeyConfig = PasscodeKeyConfig(
        shouldShuffleKeys = true,
        keyTextStyle = null,
        keyColor = KptTheme.colorScheme.primary,
        keyShape = CircleShape,
        keyElevation = null,
        keyContainerColor = KptTheme.colorScheme.surface,
        keySize = 60.dp,
    )

    val icon: ImageVector = when {
        authOptions.contains(PlatformAuthOptions.Fingerprint) -> Icons.Default.Fingerprint
        authOptions.contains(PlatformAuthOptions.FaceId) -> Icons.Default.Face
        else -> Icons.Default.Lock
    }

    PasscodeKey(
        modifier = modifier,
        keyIcon = icon,
        onClick = { onClick() },
        keyColor = passcodeKeyConfig.keyColor,
        shape = passcodeKeyConfig.keyShape,
        elevation = passcodeKeyConfig.keyElevation ?: CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp,
        ),
        containerColor = passcodeKeyConfig.keyContainerColor,
        size = passcodeKeyConfig.keySize,
    )
}
