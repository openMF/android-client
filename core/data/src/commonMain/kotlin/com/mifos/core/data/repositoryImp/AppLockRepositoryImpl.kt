package com.mifos.core.data.repositoryImp

import com.mifos.core.data.repository.AppLockRepository
import com.russhwolf.settings.Settings


const val APP_LOCK_FLAG = "com.mifos.app_lock_flag"

class AppLockRepositoryImpl(
    private val settings: Settings,
) : AppLockRepository {
    override fun lockApp() {
        settings.putBoolean(APP_LOCK_FLAG, true)
    }

    override fun unlockApp() {
        settings.putBoolean(APP_LOCK_FLAG, false)
    }

    override fun deleteLock() {
        settings.remove(APP_LOCK_FLAG)
    }

    override fun isAppLocked(): Boolean {
        return settings.getBoolean(APP_LOCK_FLAG, true)
    }
}
