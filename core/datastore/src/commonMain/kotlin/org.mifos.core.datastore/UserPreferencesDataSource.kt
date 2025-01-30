///*
// * Copyright 2024 Mifos Initiative
// *
// * This Source Code Form is subject to the terms of the Mozilla Public
// * License, v. 2.0. If a copy of the MPL was not distributed with this
// * file, You can obtain one at https://mozilla.org/MPL/2.0/.
// *
// * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
// */
//@file:OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
//
package org.mifos.core.datastore
//
//import com.russhwolf.settings.ExperimentalSettingsApi
//import com.russhwolf.settings.Settings
//import com.russhwolf.settings.serialization.decodeValue
//import com.russhwolf.settings.serialization.decodeValueOrNull
//import com.russhwolf.settings.serialization.encodeValue
//import kotlinx.coroutines.CoroutineDispatcher
//
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.map
//import kotlinx.coroutines.withContext
//import kotlinx.serialization.ExperimentalSerializationApi
//
//
//private const val USER_INFO_KEY = "userInfo"
//
//
//@OptIn(ExperimentalSerializationApi::class)
//class UserPreferencesDataSource(
//    private val settings: Settings,
//    private val dispatcher: CoroutineDispatcher,
//) {
//    private val _userInfo = MutableStateFlow(
//        settings.decodeValue(
//            key = USER_INFO_KEY,
//            serializer = UserInfoPreferences.serializer(),
//            defaultValue = settings.decodeValueOrNull(
//                key = USER_INFO_KEY,
//                serializer = UserInfoPreferences.serializer(),
//            ) ?: UserInfoPreferences.DEFAULT,
//        ),
//    )
//
//
//    val userInfo = _userInfo.map(UserInfoPreferences::toUserInfo)
//
//    suspend fun updateUserInfo(userInfo: UserInfo) {
//        withContext(dispatcher) {
//            settings.putUserInfoPreference(userInfo.toUserInfoPreferences())
//            _userInfo.value = userInfo.toUserInfoPreferences()
//        }
//    }
//
//
//
//
//    suspend fun clearInfo() {
//        withContext(dispatcher) {
//            settings.clear()
//        }
//    }
//
//
//}
//
//
//
//private fun Settings.putUserInfoPreference(preference: UserInfoPreferences) {
//    encodeValue(
//        key = USER_INFO_KEY,
//        serializer = UserInfoPreferences.serializer(),
//        value = preference,
//    )
//}
//
//

import com.russhwolf.settings.serialization.decodeValue
import com.russhwolf.settings.serialization.decodeValueOrNull
import kotlinx.coroutines.withContext
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings
import com.russhwolf.settings.serialization.encodeValue
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.ExperimentalSerializationApi
import org.mifos.core.datastore.model.UserData

private const val USER_DATA = "userData"
class UserPreferencesDataSource(
    private val settings: Settings,
    private val dispatcher: CoroutineDispatcher,
) {
    @OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
    private val _userInfo = MutableStateFlow(
        settings.decodeValue(
            key = USER_DATA,
            serializer = UserData.serializer(),
            defaultValue = settings.decodeValueOrNull(
                key = USER_DATA,
                serializer = UserData.serializer(),
            ) ?: UserData.DEFAULT,
        ),
    )
    val userInfo = _userInfo
    suspend fun updateUserInfo(user: UserData) {
        withContext(dispatcher) {
            settings.putUserPreference(user)
            _userInfo.value = user
        }
    }
    suspend fun clearInfo() {
        withContext(dispatcher) {
            settings.clear()
        }
    }
}
@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
private fun Settings.putUserPreference(user: UserData) {
    encodeValue(
        key = USER_DATA,
        serializer = UserData.serializer(),
        value = user,
    )
}