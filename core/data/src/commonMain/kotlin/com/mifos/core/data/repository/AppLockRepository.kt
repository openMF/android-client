package com.mifos.core.data.repository

interface AppLockRepository {

    fun lockApp()

    fun unlockApp()

    fun deleteLock()

    fun isAppLocked(): Boolean

}