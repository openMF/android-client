/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.groups.navigation

import com.mifos.core.common.utils.Constants

/**
 * Created by Pronay Sarker on 13/08/2024 (10:39 AM)
 */
sealed class GroupScreen(val route: String) {

    data object GroupListScreen : GroupScreen("group_list_screen")

    data object CreateNewGroupScreen : GroupScreen("create_new_group")

    data object GroupDetailsScreen : GroupScreen("group_details_screen/{${Constants.GROUP_ID}}") {
        fun argument(groupId: Int) = "group_details_screen/$groupId"
    }
}
