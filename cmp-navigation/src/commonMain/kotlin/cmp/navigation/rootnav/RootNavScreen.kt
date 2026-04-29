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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.mifos.feature.auth.navigation.authNavGraph
import com.mifos.feature.auth.navigation.navigateToLogin
import com.mifos.feature.passcode.biometricsSetup.biometricSetupScreen
import com.mifos.feature.passcode.biometricsSetup.navigateToBiometricSetupScreen
import com.mifos.feature.passcode.mifosPasscode.navigateToReAuthMifosPasscodeScreen
import com.mifos.feature.passcode.mifosPasscode.navigateToRootMifosPasscodeScreen
import com.mifos.feature.passcode.mifosPasscode.reAuthMifosPasscodeScreen
import com.mifos.feature.passcode.mifosPasscode.rootMifosPasscodeScreen
import com.mifos.feature.settings.navigation.navigateToServerConfigGraph
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.mifos.authenticator.passcode.PasscodeManager
import org.mifos.authenticator.passcode.PasscodeStep
import kotlin.time.Clock

@Suppress("LongMethod", "CyclomaticComplexMethod")
@Composable
fun RootNavScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberMifosNavController(name = "RootNavScreen"),
    viewModel: RootNavViewModel = koinViewModel(),
    passcodeManager: PasscodeManager = koinInject(),
    appLockRepository: AppLockRepository = koinInject(),
    onSplashScreenRemoved: () -> Unit = {},
) {
    val lockTimeOut = 15_000L

    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val isAppLocked by appLockRepository.isAppLocked.collectAsStateWithLifecycle()
    val lifeCycleObserver = LocalLifecycleOwner.current.lifecycle

    val onStopTime: MutableState<Long?> = remember { mutableStateOf(null) }

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

    // Use a LaunchedEffect to ensure we don't navigate too soon when the app first opens. This
    // avoids a bug that first appeared in Compose Material3 1.2.0-rc01 that causes the initial
    // transition to appear corrupted.
    LaunchedEffect(state) {
        when (state) {
            RootNavState.Splash -> navController.navigateToSplash(rootNavOptions())
            RootNavState.AuthenticateUser -> navController.navigateToLogin(rootNavOptions())
            RootNavState.UserAuthenticated -> {
                navController.navigateToRootMifosPasscodeScreen(rootNavOptions())
            }
        }
    }

    // Background-timeout re-auth: when the app resumes after being backgrounded
    // longer than `lockTimeOut` (15 s), and the passcode manager is currently
    // on its Enter step (i.e. the user had previously unlocked), push the
    // re-auth passcode screen. Skips if the app is already locked (avoids
    // doubling up on nav destinations) or if we're mid-change/mid-create.
    DisposableEffect(lifeCycleObserver) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    onStopTime.value?.let { time ->
                        val inactiveTime = Clock.System.now().toEpochMilliseconds() - time
                        Logger.a { "inactiveTime: ${inactiveTime / 1000}s" }
                        if (inactiveTime > lockTimeOut && !isAppLocked) {
                            if (passcodeManager.state.value.passcodeStep == PasscodeStep.Enter) {
                                navController.navigateToReAuthMifosPasscodeScreen()
                            }
                        }
                    }

                    onStopTime.value = null
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
        authenticatedGraph(
            navController = navController,
            onClickLogout = {
                viewModel.trySendAction(RootNavAction.LogOutUser)
            },
        )
        authNavGraph(
            navigatePasscode = navController::navigateToRootMifosPasscodeScreen,
            updateServerConfig = navController::navigateToServerConfigGraph,
        )

        // Root passcode destination — shown when launching into an existing
        // session. `Verified` advances to the authenticated graph;
        // `Forgotten`/Login navigates out; `Created` flows into biometric setup.
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

        // Re-auth destination — pushed when the app returns from background
        // past the lock timeout. Pops back on verification success.
        reAuthMifosPasscodeScreen(
            navigateToLogin = {
                viewModel.trySendAction(RootNavAction.LogOutUser)
                navController.popBackStack()
            },
            onAuthenticationSuccess = {
                navController.popBackStack()
            },
        )

        // First-time biometric setup — shown after passcode creation. Either
        // path (register or skip) unlocks the app and advances to the
        // authenticated graph.
        biometricSetupScreen(
            onBiometricsRegistrationSuccess = {
                viewModel.trySendAction(RootNavAction.UnlockApp)
                navController.popBackStack()
                navController.navigateToAuthenticatedGraph(rootNavOptions())
            },
            onSkipBiometricSetup = {
                viewModel.trySendAction(RootNavAction.UnlockApp)
                navController.popBackStack()
                navController.navigateToAuthenticatedGraph(rootNavOptions())
            },
        )
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
