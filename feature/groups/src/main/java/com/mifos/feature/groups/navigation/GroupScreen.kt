package com.mifos.feature.groups.navigation

import com.mifos.core.common.utils.Constants

/**
 * Created by Pronay Sarker on 13/08/2024 (10:39 AM)
 */
sealed class GroupScreen(val route: String) {

    data object GroupListScreen : GroupScreen("group_list_screen")

    data object CreateNewGroupScreen : GroupScreen("create_new_group")

    data object GroupDetailsScreen : GroupScreen("group_details_screen/{${Constants.GROUP_ID}}"){
        fun argument(groupId : Int) = "group_details_screen/${groupId}"
    }
}

