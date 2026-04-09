/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package cmp.navigation.rootnav

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import cmp.navigation.authenticated.authenticatedGraph
import cmp.navigation.authenticated.navigateToAuthenticatedGraph
import cmp.navigation.splash.SplashRoute
import cmp.navigation.splash.navigateToSplash
import cmp.navigation.splash.splashDestination
import cmp.navigation.ui.rememberMifosNavController
import cmp.navigation.utils.toObjectNavigationRoute
import co.touchlab.kermit.Logger
import com.mifos.core.data.repository.AppLockRepository
import com.mifos.core.ui.NonNullEnterTransitionProvider
import com.mifos.core.ui.NonNullExitTransitionProvider
import com.mifos.core.ui.RootTransitionProviders
import com.mifos.feature.auth.navigation.LoginRoute
import com.mifos.feature.auth.navigation.authNavGraph
import com.mifos.feature.auth.navigation.navigateToLogin
import com.mifos.feature.passcode.biometricsSetup.biometricSetupScreen
import com.mifos.feature.passcode.biometricsSetup.navigateToBiometricSetupScreen
import com.mifos.feature.passcode.mifosPasscode.RootPasscodeRoute
import com.mifos.feature.passcode.mifosPasscode.navigateToReAuthMifosPasscodeScreen
import com.mifos.feature.passcode.mifosPasscode.navigateToRootMifosPasscodeScreen
import com.mifos.feature.passcode.mifosPasscode.reAuthMifosPasscodeScreen
import com.mifos.feature.passcode.mifosPasscode.rootMifosPasscodeScreen
import com.mifos.feature.settings.navigation.navigateToServerConfigGraph
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock

@Suppress("LongMethod", "CyclomaticComplexMethod")
@Composable
fun RootNavScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberMifosNavController(name = "RootNavScreen"),
    viewModel: RootNavViewModel = koinViewModel(),
    appLockRepository: AppLockRepository = koinInject(),
    onSplashScreenRemoved: () -> Unit = {},
) {
    val lockTimeOut = 15_000L

    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val isAppLocked by appLockRepository.isAppLocked.collectAsStateWithLifecycle()
    val lifeCycleObserver = LocalLifecycleOwner.current.lifecycle

    val previousStateReference = remember { mutableStateOf(state) }
    val onStopTime = remember { mutableStateOf(Long.MAX_VALUE) }

    val isNotSplashScreen = state != RootNavState.Splash

    // When state changes, navigate to different root navigation state
    fun rootNavOptions() = navOptions {
        // When changing root navigation state, pop everything else off the back stack:
        popUpTo(navController.graph.id) {
            inclusive = false
            saveState = false
        }
        launchSingleTop = true
        restoreState = false
    }

    DisposableEffect(lifeCycleObserver) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    val inactiveTime = Clock.System.now().toEpochMilliseconds() - onStopTime.value
                    Logger.a { "inactiveTime: ${inactiveTime / 1000}s" }
                    if (inactiveTime > lockTimeOut && !isAppLocked) {
                        navController.navigateToReAuthMifosPasscodeScreen()
                    }
                    onStopTime.value = Long.MAX_VALUE
                }

                Lifecycle.Event.ON_STOP -> {
                    onStopTime.value = Clock.System.now().toEpochMilliseconds()
                }

                else -> {}
            }
        }
        lifeCycleObserver.addObserver(observer)
        onDispose { lifeCycleObserver.removeObserver(observer) }
    }


    LaunchedEffect(isNotSplashScreen) {
        if (isNotSplashScreen) onSplashScreenRemoved()
    }

    NavHost(
        navController = navController,
        startDestination = SplashRoute,
        modifier = modifier,
        enterTransition = { toEnterTransition()(this) },
        exitTransition = { toExitTransition()(this) },
        popEnterTransition = { toEnterTransition()(this) },
        popExitTransition = { toExitTransition()(this) },
    ) {
        splashDestination()
        authenticatedGraph(navController)
        authNavGraph(
            navigatePasscode = navController::navigateToRootMifosPasscodeScreen,
            updateServerConfig = navController::navigateToServerConfigGraph,
        )

        rootMifosPasscodeScreen(
            navigateToLogin = {
                viewModel.trySendAction(RootNavAction.LogOutUser)
                navController.popBackStack()
            },
            onAuthenticationSuccess = {
                navController.popBackStack()
                navController.navigateToAuthenticatedGraph(rootNavOptions())
            },
            onPasscodeCreation = {
                navController.popBackStack()
                navController.navigateToBiometricSetupScreen(rootNavOptions())
            },
        )

        reAuthMifosPasscodeScreen(
            navigateToLogin = {
                viewModel.trySendAction(RootNavAction.LogOutUser)
                navController.popBackStack()
            },
            onAuthenticationSuccess = {
                navController.popBackStack()
            },
        )

        biometricSetupScreen(
            onBiometricsRegistrationSuccess = {
                appLockRepository.unlockApp()
                navController.popBackStack()
                navController.navigateToAuthenticatedGraph(rootNavOptions())
            },
            onSkipBiometricSetup = {
                appLockRepository.unlockApp()
                navController.popBackStack()
                navController.navigateToAuthenticatedGraph(rootNavOptions())
            },
        )
    }

    val targetRoute = when (state) {
        RootNavState.Splash -> SplashRoute
        is RootNavState.UserAuthenticated -> RootPasscodeRoute
        is RootNavState.AuthenticateUser -> LoginRoute
        else -> LoginRoute
    }

    val currentRoute = navController.currentDestination?.rootLevelRoute()

    // Don't navigate if we are already at the correct root. This notably happens during process
    // death. In this case, the NavHost already restores state, so we don't have to navigate.
    // However, if the route is correct but the underlying state is different, we should still
    // proceed in order to get a fresh version of that route.
    if (currentRoute == targetRoute.toObjectNavigationRoute() &&
        previousStateReference.value == state
    ) {
        previousStateReference.value == state
        return
    }
    previousStateReference.value = state


    // Use a LaunchedEffect to ensure we don't navigate too soon when the app first opens. This
    // avoids a bug that first appeared in Compose Material3 1.2.0-rc01 that causes the initial
    // transition to appear corrupted.
    LaunchedEffect(state) {
        when (state) {
            RootNavState.Splash -> navController.navigateToSplash(rootNavOptions())
            RootNavState.AuthenticateUser -> { navController.navigateToLogin() }
            RootNavState.UserAuthenticated -> {
                navController.navigateToRootMifosPasscodeScreen(rootNavOptions())
            }
            else -> {}
        }
    }
}

private fun NavDestination?.rootLevelRoute(): String? = when {
    this == null -> null
    parent?.route == null -> route
    else -> parent.rootLevelRoute()
}

@Suppress("MaxLineLength")
private fun AnimatedContentTransitionScope<NavBackStackEntry>.toEnterTransition(): NonNullEnterTransitionProvider =
    when (targetState.destination.rootLevelRoute()) {
        SplashRoute.toObjectNavigationRoute() -> RootTransitionProviders.Enter.none
        else -> RootTransitionProviders.Enter.fadeIn
    }

@Suppress("MaxLineLength")
private fun AnimatedContentTransitionScope<NavBackStackEntry>.toExitTransition(): NonNullExitTransitionProvider {
    return when (initialState.destination.rootLevelRoute()) {
        // Disable transitions when coming from the splash screen
        SplashRoute.toObjectNavigationRoute() -> RootTransitionProviders.Exit.none
        else -> RootTransitionProviders.Exit.fadeOut
    }
}
