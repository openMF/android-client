package com.mifos.feature.center.navigation

import com.mifos.core.common.utils.Constants
import com.mifos.core.objects.group.Center

sealed class CenterScreens(val route: String) {

    data object CenterListScreen : CenterScreens("center_list_screen")

    data object CenterDetailScreen : CenterScreens("center_detail_screen/{${Constants.CENTER_ID}}") {
        fun argument(centerId: Int) = "center_detail_screen/${centerId}"
    }

    data object CenterGroupListScreen : CenterScreens("center_group_list_screen/{${Constants.CENTER_ID}}") {
        fun arguments(centerId: Int) = "center_group_list_screen/${centerId}"
    }
    data object SyncCenterPayloadsScreen : CenterScreens("sync_center_payloads_screen"){
        fun arguments(centers :List<Center>) = "sync_center_payloads_screen/${centers}"
    }
    data object CreateCenterScreen : CenterScreens("create_center_screen/{${Constants.CENTER_ID}}")
}