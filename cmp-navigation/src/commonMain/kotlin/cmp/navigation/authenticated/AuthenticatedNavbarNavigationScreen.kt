/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package cmp.navigation.authenticated

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.SnackbarDuration.Indefinite
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navOptions
import cmp.navigation.components.MifosScaffold
import cmp.navigation.components.ScaffoldNavigationData
import cmp.navigation.navigation.HomeDestinationsScreen
import cmp.navigation.ui.rememberMifosNavController
import com.mifos.core.common.utils.Constants
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.ui.RootTransitionProviders
import com.mifos.core.ui.util.EventsEffect
import com.mifos.feature.activate.navigateToActivateRoute
import com.mifos.feature.center.navigation.centerNavGraph
import com.mifos.feature.center.navigation.navigateCenterDetailsScreenRoute
import com.mifos.feature.center.navigation.navigateCreateCenterScreenRoute
import com.mifos.feature.center.navigation.navigateToCenterListScreenRoute
import com.mifos.feature.client.navigation.clientNavGraph
import com.mifos.feature.client.navigation.navigateClientDetailsScreen
import com.mifos.feature.client.navigation.navigateCreateClientScreen
import com.mifos.feature.client.navigation.navigateToClientListScreen
import com.mifos.feature.dataTable.navigation.navigateDataTableList
import com.mifos.feature.dataTable.navigation.navigateToDataTable
import com.mifos.feature.groups.navigation.groupNavGraph
import com.mifos.feature.groups.navigation.navigateToCreateNewGroupScreen
import com.mifos.feature.groups.navigation.navigateToGroupDetailsScreen
import com.mifos.feature.groups.navigation.navigateToGroupListScreen
import com.mifos.feature.loan.groupLoanAccount.navigateToGroupLoanScreen
import com.mifos.feature.loan.loanAccount.navigateToLoanAccountScreen
import com.mifos.feature.loan.loanAccountSummary.navigateToLoanAccountSummaryScreen
import com.mifos.feature.note.notes.navigateToNoteScreen
import com.mifos.feature.savings.navigation.navigateToAddSavingsAccount
import com.mifos.feature.savings.navigation.navigateToSavingsAccountSummaryScreen
import com.mifos.feature.search.navigation.SearchScreenRoute
import com.mifos.feature.search.navigation.navigateToSearchScreen
import com.mifos.feature.search.navigation.searchNavGraph
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifos.navigation.generated.resources.Res
import org.mifos.navigation.generated.resources.cmp_navigation_mifos
import org.mifos.navigation.generated.resources.cmp_navigation_no_internet
import org.mifos.navigation.generated.resources.drawer_profile_header
import org.mifos.navigation.generated.resources.ic_dp_placeholder

@Composable
internal fun AuthenticatedNavbarNavigationScreen(
    navigateToDocumentScreen: (Int, String) -> Unit,
    navigateToNoteScreen: (Int, String) -> Unit,
    onDrawerItemClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberMifosNavController(
        name = "AuthenticatedNavbarScreen",
    ),
    navigateToNewLoanAccountScreen: (Int) -> Unit,
    navigateToNewSavingsAccountScreen: (Int) -> Unit,
    viewModel: AuthenticatedNavbarNavigationViewModel = koinViewModel(),
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val isOffline by viewModel.isOffline.collectAsStateWithLifecycle()

    EventsEffect(eventFlow = viewModel.eventFlow) { event ->
        navController.apply {
            when (event) {
                // TODO Add navigation to respective screens
                AuthenticatedNavBarEvent.NavigateToSearchScreen -> {
                    navigateToTabOrRoot(tabToNavigateTo = event.tab) {
                        navigateToSearchScreen()
                    }
                }

                AuthenticatedNavBarEvent.NavigateToClientScreen -> {
                    navigateToTabOrRoot(tabToNavigateTo = event.tab) {
                        navigateToClientListScreen()
                    }
                }

                AuthenticatedNavBarEvent.NavigateToCenterScreen -> {
                    navigateToTabOrRoot(tabToNavigateTo = event.tab) {
                        navigateToCenterListScreenRoute()
                    }
                }
                AuthenticatedNavBarEvent.NavigateToGroupScreen -> {
                    navigateToTabOrRoot(tabToNavigateTo = event.tab) {
                        navigateToGroupListScreen()
                    }
                }
            }
        }
    }

    val message = stringResource(Res.string.cmp_navigation_no_internet)
    LaunchedEffect(isOffline) {
        if (isOffline) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = message,
                    duration = Indefinite,
                )
            }
        } else {
            snackbarHostState.currentSnackbarData?.dismiss()
        }
    }

    AuthenticatedNavbarNavigationScreenContent(
        navController = navController,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
        onAction = remember(viewModel) {
            { viewModel.trySendAction(it) }
        },
        onDrawerItemClick = onDrawerItemClick,
        navigateToDocumentScreen = navigateToDocumentScreen,
        navigateToNoteScreen = navigateToNoteScreen,
        navigateToNewLoanAccountScreen = navigateToNewLoanAccountScreen,
        navigateToNewSavingsAccountScreen = navigateToNewSavingsAccountScreen,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AuthenticatedNavbarNavigationScreenContent(
    navController: NavHostController,
    onDrawerItemClick: (String) -> Unit,
    navigateToDocumentScreen: (Int, String) -> Unit,
    navigateToNoteScreen: (Int, String) -> Unit,
    navigateToNewLoanAccountScreen: (Int) -> Unit,
    navigateToNewSavingsAccountScreen: (Int) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onAction: (AuthenticatedNavBarAction) -> Unit,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    var selectedItemIndex by rememberSaveable { mutableStateOf<Int?>(null) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navigationItems = persistentListOf<NavigationItem>(
        AuthenticatedNavBarTabItem.SearchTab,
        AuthenticatedNavBarTabItem.ClientTab,
        AuthenticatedNavBarTabItem.CentersTab,
        AuthenticatedNavBarTabItem.GroupsTab,
    )

    val navigationDrawerTabs = persistentListOf(
        HomeDestinationsScreen.CheckerInboxAndTasksScreen,
        HomeDestinationsScreen.CollectionSheetScreen,
        HomeDestinationsScreen.RunReportsScreen,
        HomeDestinationsScreen.PathTrackerScreen,
        HomeDestinationsScreen.SettingsScreen,
        HomeDestinationsScreen.AboutScreen,
        HomeDestinationsScreen.OfflineSyncScreen,
    )

    ModalNavigationDrawer(
        modifier = modifier,
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.requiredWidth(320.dp)) {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                    ) {
                        Image(
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            painter = painterResource(Res.drawable.drawer_profile_header),
                            contentDescription = "Profile header",
                        )
                        Column(modifier = Modifier.padding(32.dp)) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Image(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape),
                                    painter = painterResource(Res.drawable.ic_dp_placeholder),
                                    contentDescription = "DP place holder",
                                )
                                Text(
                                    text = stringResource(Res.string.cmp_navigation_mifos),
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleMedium,
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(DesignToken.padding.small))
                    navigationDrawerTabs.forEachIndexed { index, item ->
                        NavigationDrawerItem(
                            label = {
                                Text(
                                    text = stringResource(item.title),
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            },
                            selected = index == selectedItemIndex,
                            onClick = {
                                selectedItemIndex = index
                                onDrawerItemClick(navigationDrawerTabs[index].route)
                                scope.launch {
                                    drawerState.close()
                                }
                            },
                            icon = {
                                item.icon?.let {
                                    Icon(
                                        imageVector = if (index == selectedItemIndex) {
                                            it
                                        } else {
                                            it
                                        },
                                        contentDescription = stringResource(item.title),
                                    )
                                }
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                        )
                        if (index == (navigationDrawerTabs.size - 2)) {
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        }
                    }
                }
            }
        },
    ) {
        MifosScaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(stringResource(Res.string.cmp_navigation_mifos))
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    drawerState.apply {
                                        if (isClosed) open() else close()
                                    }
                                }
                            },
                        ) {
                            Icon(
                                imageVector = MifosIcons.Menu,
                                contentDescription = "Menu",
                            )
                        }
                    },
                )
            },
            contentWindowInsets = WindowInsets(0.dp),
            navigationData = ScaffoldNavigationData(
                navigationItems = navigationItems,
                selectedNavigationItem = navigationItems.find {
                    navBackStackEntry.isCurrentRoute(route = it.graphRoute)
                },
                onNavigationClick = { navigationItem ->
                    // TODO navigate to respective screens
                    when (navigationItem) {
                        is AuthenticatedNavBarTabItem.SearchTab -> {
                            onAction(AuthenticatedNavBarAction.SearchTabClick)
                        }

                        is AuthenticatedNavBarTabItem.ClientTab -> {
                            onAction(AuthenticatedNavBarAction.ClientTabClick)
                        }

                        is AuthenticatedNavBarTabItem.CentersTab -> {
                            onAction(AuthenticatedNavBarAction.CenterTabClick)
                        }
                        is AuthenticatedNavBarTabItem.GroupsTab -> {
                            onAction(AuthenticatedNavBarAction.GroupTabClick)
                        }
                    }
                },
                shouldShowNavigation = navigationItems.any {
                    navBackStackEntry.isCurrentRoute(route = it.startDestinationRoute)
                },
            ),
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            modifier = modifier,
        ) {
            // Because this Scaffold has a bottom navigation bar, the NavHost will:
            // - consume the vertical navigation bar insets.
            // - consume the IME insets.
            NavHost(
                navController = navController,
                startDestination = SearchScreenRoute,
                enterTransition = RootTransitionProviders.Enter.fadeIn,
                exitTransition = RootTransitionProviders.Exit.fadeOut,
                popEnterTransition = RootTransitionProviders.Enter.fadeIn,
                popExitTransition = RootTransitionProviders.Exit.fadeOut,
            ) {
                // TODO Add top level destination screens
                searchNavGraph(
                    onCreateClient = navController::navigateCreateClientScreen,
                    onCreateCenter = navController::navigateCreateCenterScreenRoute,
                    onCreateGroup = navController::navigateToCreateNewGroupScreen,
                    onCenter = navController::navigateCenterDetailsScreenRoute,
                    onClient = navController::navigateClientDetailsScreen,
                    onGroup = navController::navigateToGroupDetailsScreen,
                    onLoan = navController::navigateToLoanAccountSummaryScreen,
                    onSavings = navController::navigateClientDetailsScreen,
                )

                centerNavGraph(
                    navController = navController,
                    onActivateCenter = navController::navigateToActivateRoute,
                    addSavingsAccount = { centerId ->
                        navController.navigateToAddSavingsAccount(0, centerId, false)
                    },
                )

                groupNavGraph(
                    navController = navController,
                    addGroupLoanAccount = navController::navigateToGroupLoanScreen,
                    addSavingsAccount = navController::navigateToAddSavingsAccount,
                    loadDocumentList = navigateToDocumentScreen,
                    loadClientList = navController::navigateToClientListScreen,
                    loadSavingsAccountSummary = navController::navigateToSavingsAccountSummaryScreen,
                    loadGroupDataTables = navController::navigateToDataTable,
                    loadNotes = navController::navigateToNoteScreen,
                    loadLoanAccountSummary = navController::navigateToLoanAccountSummaryScreen,
                    activateGroup = navController::navigateToActivateRoute,
                )

                clientNavGraph(
                    navController = navController,
                    addLoanAccount = navController::navigateToLoanAccountScreen,
                    addSavingsAccount = { clientId ->
                        navController.navigateToAddSavingsAccount(0, clientId, false)
                    },
                    documents = { clientId ->
                        navigateToDocumentScreen(
                            clientId,
                            Constants.ENTITY_TYPE_CLIENTS,
                        )
                    },
                    moreClientInfo = { clientId ->
                        navController.navigateToDataTable(
                            Constants.DATA_TABLE_NAME_CLIENT,
                            clientId,
                        )
                    },
                    notes = { clientId ->
                        navigateToNoteScreen(
                            clientId,
                            Constants.ENTITY_TYPE_CLIENTS,
                        )
                    },
                    loanAccountSelected = { loanAccountNumber ->
                        navController.navigateToLoanAccountSummaryScreen(loanAccountNumber)
                    },
                    savingsAccountSelected = { clientId, depositType ->
                        navController.navigateToSavingsAccountSummaryScreen(clientId, depositType)
                    },
                    activateClient = { clientId ->
                        navController.navigateToActivateRoute(
                            clientId,
                            Constants.ACTIVATE_CLIENT,
                        )
                    },
                    hasDatatables = navController::navigateDataTableList,
                    onDocumentClicked = navigateToDocumentScreen,
                    navigateToNewLoanAccount = navigateToNewLoanAccountScreen,
                    navigateToNewSavingsAccount = navigateToNewSavingsAccountScreen,
                )
            }
        }
    }
}

private fun NavController.navigateToTabOrRoot(
    tabToNavigateTo: AuthenticatedNavBarTabItem,
    navigate: (NavOptions) -> Unit,
) {
    if (tabToNavigateTo.startDestinationRoute == currentDestination?.route) {
        // We are at the start destination already, so nothing to do.
        return
    } else if (currentDestination?.parent?.route == tabToNavigateTo.graphRoute) {
        // We are not at the start destination but we are in the correct graph,
        // so lets pop up to the start destination.
        popBackStack(route = tabToNavigateTo.startDestinationRoute, inclusive = false)
    } else {
        // We are not in correct graph at all, so navigate there.
        navigate(
            navOptions {
                popUpTo(graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            },
        )
    }
}

private fun NavBackStackEntry?.isCurrentRoute(route: String): Boolean =
    this
        ?.destination
        ?.hierarchy
        ?.any { it.route == route } == true
