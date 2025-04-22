/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.about.navigation

/**
 * Created by Pronay Sarker on 18/08/2024 (2:42 PM)
 */
sealed class AboutScreens(val route: String) {
    data object AboutScreen : AboutScreens(route = "about_screen_route")
}
