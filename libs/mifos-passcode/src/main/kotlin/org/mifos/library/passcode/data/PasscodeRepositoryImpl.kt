/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package org.mifos.library.passcode.data

import javax.inject.Inject

class PasscodeRepositoryImpl @Inject constructor(
    private val preferenceManager: PasscodeManager,
) : PasscodeRepository {

    override val hasPasscode: Boolean
        get() = preferenceManager.getSavedPasscode().isNotEmpty()

    override fun getSavedPasscode(): String {
        return preferenceManager.getSavedPasscode()
    }

    override fun savePasscode(passcode: String) {
        preferenceManager.savePasscode(passcode)
    }
}
