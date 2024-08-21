package com.mifos.feature.search.navigation

sealed class SearchScreens(val route: String) {
    data object SearchScreenRoute : SearchScreens("search_screen_route")

    data object SearchScreen : SearchScreens("search_screen")
}