package com.mifos.core.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface AppLockRepository {
    val isAppLocked: StateFlow<Boolean>

    fun lockApp()

    fun unlockApp()

    fun deleteLock()
}