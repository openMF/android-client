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

import com.mifos.core.data.repository.AppLockRepository
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

const val APP_LOCK_FLAG = "com.mifos.app_lock_flag"

class AppLockRepositoryImpl(
    private val settings: Settings,
) : AppLockRepository {

    private val _isAppLocked = MutableStateFlow(settings.getBoolean(APP_LOCK_FLAG, true))
    override val isAppLocked: StateFlow<Boolean> = _isAppLocked.asStateFlow()

    override fun lockApp() {
        settings.putBoolean(APP_LOCK_FLAG, true)
        _isAppLocked.value = true
    }

    override fun unlockApp() {
        settings.putBoolean(APP_LOCK_FLAG, false)
        _isAppLocked.value = false
    }

    override fun deleteLock() {
        settings.remove(APP_LOCK_FLAG)
        _isAppLocked.value = true
    }
}
