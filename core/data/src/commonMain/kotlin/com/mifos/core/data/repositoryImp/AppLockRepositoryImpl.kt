package com.mifos.core.data.repositoryImp

import com.mifos.core.data.repository.AppLockRepository
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow


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
    }

}
