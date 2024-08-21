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