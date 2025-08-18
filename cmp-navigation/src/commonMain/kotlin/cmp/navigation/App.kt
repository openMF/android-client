/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package cmp.navigation

/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-mobile/blob/master/LICENSE.md
 */

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration.Indefinite
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
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
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import cmp.navigation.components.MifosNavigationBar
import cmp.navigation.components.NavigationConstants
import cmp.navigation.navigation.FeatureNavHost
import cmp.navigation.navigation.HomeDestinationsScreen
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.designsystem.component.MifosDialogBox
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.MifosBackground
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.mifos.navigation.generated.resources.Res
import org.mifos.navigation.generated.resources.cmp_navigation_no_internet
import org.mifos.navigation.generated.resources.drawer_profile_header
import org.mifos.navigation.generated.resources.ic_dp_placeholder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    networkMonitor: NetworkMonitor,
    modifier: Modifier = Modifier,
    navigateToLogin: () -> Unit,
    onClickLogout: () -> Unit,
    onClickUpdateConfig: () -> Unit,
) {
    val appState = rememberAppState(
        networkMonitor = networkMonitor,
    )

    val navBackStackEntry by appState.navController.currentBackStackEntryAsState()
    val route = navBackStackEntry?.destination?.route
    val isNavScreen = NavigationConstants.isNavScreen(route)

    var offline by rememberSaveable { mutableStateOf(false) }

    val navigationDrawerTabs = remember {
        listOf(
            HomeDestinationsScreen.CheckerInboxAndTasksScreen,
            HomeDestinationsScreen.IndividualCollectionSheetScreen,
            HomeDestinationsScreen.CollectionSheetScreen,
            HomeDestinationsScreen.RunReportsScreen,
            HomeDestinationsScreen.PathTrackerScreen,
            HomeDestinationsScreen.SettingsScreen,
            HomeDestinationsScreen.AboutScreen,
            HomeDestinationsScreen.OfflineSyncScreen,
        )
    }

    var selectedItemIndex by rememberSaveable { mutableStateOf<Int?>(null) }
    var dialogState by rememberSaveable { mutableStateOf(false) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    MifosBackground(modifier) {
        val snackbarHostState = remember { SnackbarHostState() }

        val isOffline by appState.isOffline.collectAsStateWithLifecycle()

        val notConnectedMessage = stringResource(Res.string.cmp_navigation_no_internet)
        LaunchedEffect(isOffline) {
            if (isOffline) {
                snackbarHostState.showSnackbar(
                    message = notConnectedMessage,
                    duration = Indefinite,
                )
            }
        }

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
                                        text = "Mifos",
                                        color = Color.White,
                                        style = MaterialTheme.typography.titleMedium,
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = "Offline Mode",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White,
                                    )
                                    Switch(
                                        checked = offline,
                                        onCheckedChange = {
                                            offline = it
                                        },
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        navigationDrawerTabs.forEachIndexed { index, item ->
                            NavigationDrawerItem(
                                label = {
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                },
                                selected = index == selectedItemIndex,
                                onClick = {
                                    selectedItemIndex = index
                                    appState.navController.navigate(navigationDrawerTabs[index].route) {
                                        launchSingleTop = true
                                    }
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
                                            contentDescription = item.title,
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
            gesturesEnabled = isNavScreen,
        ) {
            Scaffold(
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                topBar = {
                    if (isNavScreen) {
                        TopAppBar(
                            title = {
                                Text(NavigationConstants.getTitleForRoute(route))
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
                            actions = {
                                IconButton(onClick = { dialogState = true }) {
                                    Icon(
                                        imageVector = MifosIcons.Logout,
                                        contentDescription = "Log out icon",
                                    )
                                }
                            },
                        )
                    }
                },
                bottomBar = {
                    if (isNavScreen) {
                        Column {
                            route?.let {
                                MifosNavigationBar(route = it) { target ->
                                    appState.navController.apply {
                                        navigate(target) {
                                            restoreState = true
                                            launchSingleTop = true
                                            popUpTo(route = graph.findStartDestination().route.toString()) {
                                                saveState = true
                                                inclusive = false
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
            ) { paddingValues ->
                FeatureNavHost(
                    appState = appState,
                    padding = paddingValues,
                    navigateToLogin = navigateToLogin,
                    onClickUpdateConfig = onClickUpdateConfig,
                )
                if (dialogState) {
                    MifosDialogBox(
                        title = "Are you Sure you want to Logout?",
                        showDialogState = dialogState,
                        confirmButtonText = "LogOut",
                        onDismiss = { dialogState = false },
                        onConfirm = {
                            dialogState = false
                            onClickLogout()
                        },
                        dismissButtonText = "Cancel",
                    )
                }
            }
        }
    }
}
