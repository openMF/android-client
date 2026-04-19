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

import android.content.Context
import androidx.core.content.edit
import com.mifos.library.passcode.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PasscodeManager @Inject constructor(
    @ApplicationContext
    private val context: Context,
) {
    private val sharedPreference = context.getSharedPreferences(
        R.string.lib_mifos_passcode_pref_name.toString(),
        Context.MODE_PRIVATE,
    )

    fun savePasscode(passcode: String) {
        sharedPreference.edit {
            putString(R.string.lib_mifos_passcode.toString(), passcode)
        }
    }

    fun getSavedPasscode(): String {
        return sharedPreference.getString(
            R.string.lib_mifos_passcode.toString(),
            "",
        ).toString()
    }

    fun clearPasscode() {
        sharedPreference.edit { clear() }
    }
}
