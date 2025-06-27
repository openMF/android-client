/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.settings.navigation

/**
 * Created by Pronay Sarker on 22/08/2024 (4:31 PM)
 */
sealed class SettingsScreens(val route: String) {
    data object SettingsScreen : SettingsScreens("settings_screen")
}
