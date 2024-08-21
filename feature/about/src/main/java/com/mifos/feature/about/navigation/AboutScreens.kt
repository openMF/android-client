package com.mifos.feature.about.navigation

/**
 * Created by Pronay Sarker on 18/08/2024 (2:42 PM)
 */
sealed class AboutScreens(val route: String) {
    data object AboutScreen :  AboutScreens(route = "about_screen_route")
}