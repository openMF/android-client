/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.activate.navigation

import com.mifos.core.common.utils.Constants

/**
 * Created by Pronay Sarker on 18/08/2024 (1:40 PM)
 */
sealed class ActivateScreens(val route: String) {
    data object ActivateScreen : ActivateScreens("activate_screen/{${Constants.ACTIVATE_ID}}/{${Constants.ACTIVATE_TYPE}}") {
        fun argument(id: Int, type: String) = "activate_screen/$id/$type"
    }
}
