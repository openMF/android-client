package cmp.navigation.authenticated


import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object AuthenticatedNavbarRoute

internal fun NavController.navigateToAuthenticatedNavBar(navOptions: NavOptions? = null) {
    navigate(route = AuthenticatedNavbarRoute, navOptions = navOptions)
}

internal fun NavGraphBuilder.authenticatedNavbarGraph(
    onDrawerItemClick: (String) -> Unit,
) {
    composable<AuthenticatedNavbarRoute> {
        AuthenticatedNavbarNavigationScreen(
            onDrawerItemClick = onDrawerItemClick
        )
    }
}
