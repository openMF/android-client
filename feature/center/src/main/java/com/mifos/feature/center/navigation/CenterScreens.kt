/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.center.navigation

import com.mifos.core.common.utils.Constants
sealed class CenterScreens(val route: String) {

    data object CenterListScreen : CenterScreens("center_list_screen")

    data object CenterDetailScreen : CenterScreens("center_detail_screen/{${Constants.CENTER_ID}}") {
        fun argument(centerId: Int) = "center_detail_screen/$centerId"
    }

    data object CenterGroupListScreen : CenterScreens("center_group_list_screen/{${Constants.CENTER_ID}}") {
        fun arguments(centerId: Int) = "center_group_list_screen/$centerId"
    }

    data object CreateCenterScreen : CenterScreens("create_center_screen/{${Constants.CENTER_ID}}")
}
