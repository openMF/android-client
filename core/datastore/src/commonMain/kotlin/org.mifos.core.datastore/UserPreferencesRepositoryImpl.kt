/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifos.core.datastore


import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import org.mifos.core.datastore.model.UserData

class UserPreferencesRepositoryImpl(
    private val preferenceManager: UserPreferencesDataSource,
    private val ioDispatcher: CoroutineDispatcher,
    unconfinedDispatcher: CoroutineDispatcher,
) : UserPreferencesRepository {
    private val unconfinedScope = CoroutineScope(unconfinedDispatcher)
    override val userInfo: Flow<UserData>
        get() = preferenceManager.userInfo
    override suspend fun updateUser(user: UserData): Result<Unit> {
        return try {
            val result = preferenceManager.updateUserInfo(user)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun logOut() {
        preferenceManager.clearInfo()
    }
}