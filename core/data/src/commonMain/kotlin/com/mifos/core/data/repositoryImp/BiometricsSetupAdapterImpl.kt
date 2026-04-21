/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.repositoryImp

import com.russhwolf.settings.Settings
import org.mifos.authenticator.biometrics.BiometricStorageAdapter

const val REGISTRATION_DATA_KEY = "com.mifos.registration_data"
const val BIOMETRIC_REGISTERED_KEY = "com.mifos.biometric_registered"

/**
 * [BiometricStorageAdapter] impl backed by [Settings] (multiplatform key-value).
 *
 * Uses a two-key layout:
 *  - [BIOMETRIC_REGISTERED_KEY] — boolean flag; `true` iff a registration has
 *    occurred. Checked first by [loadRegistrationData] to distinguish
 *    "registered with an empty blob" (valid on Android, whose
 *    `PlatformAuthenticator.registerUser` returns `Success("")`) from
 *    "never registered."
 *  - [REGISTRATION_DATA_KEY] — the registration blob returned by
 *    `PlatformAuthenticationProvider.registerUser()` on success.
 *
 * **Called by the library only.** App code should not invoke these methods
 * directly; use the provider's `registerUser()`, `onAuthenticatorClick()`,
 * `unregister()` methods, which drive this adapter internally.
 */
class BiometricStorageAdapterImpl(
    private val settings: Settings,
) : BiometricStorageAdapter {
    override fun saveRegistrationData(registrationData: String) {
        settings.putBoolean(BIOMETRIC_REGISTERED_KEY, true)
        settings.putString(REGISTRATION_DATA_KEY, registrationData)
    }

    override fun loadRegistrationData(): String? {
        if (!settings.getBoolean(BIOMETRIC_REGISTERED_KEY, false)) return null
        return settings.getString(REGISTRATION_DATA_KEY, "")
    }

    override fun deleteRegistrationData() {
        settings.remove(BIOMETRIC_REGISTERED_KEY)
        settings.remove(REGISTRATION_DATA_KEY)
    }
}
