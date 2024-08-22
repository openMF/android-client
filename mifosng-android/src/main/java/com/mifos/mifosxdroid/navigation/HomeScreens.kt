package com.mifos.mifosxdroid.navigation

sealed class HomeScreens(val route: String) {

    data object HomeScreen : HomeScreens("home_screen")

}