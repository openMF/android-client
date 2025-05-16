package cmp.navigation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.mifos.core.data.util.NetworkMonitor
import cmp.navigation.navigation.NavGraphRoute.MAIN_GRAPH
import androidx.navigation.compose.composable
import cmp.navigation.App

@Composable
fun RootNavGraph(
    networkMonitor: NetworkMonitor,
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navHostController,
        startDestination = MAIN_GRAPH,
        route = NavGraphRoute.ROOT_GRAPH,
        modifier = modifier,
    ) {
        composable(MAIN_GRAPH) {
            App(
                modifier = modifier,
                networkMonitor = networkMonitor,
                onClickLogout = {}
            )
        }
    }
}