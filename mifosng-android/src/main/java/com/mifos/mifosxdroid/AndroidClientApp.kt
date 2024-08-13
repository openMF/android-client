@file:OptIn(ExperimentalMaterial3Api::class)

package com.mifos.mifosxdroid

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mifos.core.designsystem.theme.Black
import com.mifos.core.designsystem.theme.White
import com.mifos.mifosxdroid.components.MifosNavigationBar
import com.mifos.mifosxdroid.components.Navigation
import com.mifos.mifosxdroid.components.NavigationConstants
import kotlinx.coroutines.launch

@Composable
fun AndroidClientApp() {

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
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
            HomeDestinationsScreen.OfflineSyncScreen
        )
    }

    var selectedItemIndex by rememberSaveable { mutableStateOf<Int?>(null) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.requiredWidth(320.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        painter = painterResource(id = R.drawable.drawer_profile_header),
                        contentDescription = null
                    )
                    Column(modifier = Modifier.padding(32.dp)) {
                        Image(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape),
                            painter = painterResource(id = R.drawable.ic_dp_placeholder),
                            contentDescription = null
                        )
                        Text(
                            text = "Mifos", color = Color.White,
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                fontStyle = FontStyle.Normal
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Offline Mode", color = Color.White,
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    fontStyle = FontStyle.Normal
                                )
                            )
                            Switch(
                                checked = offline,
                                onCheckedChange = {
                                    offline = it
                                }
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
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    fontStyle = FontStyle.Normal
                                ),
                                color = Black,
                            )
                        },
                        selected = index == selectedItemIndex,
                        onClick = {
                            selectedItemIndex = index
                            selectedItemIndex?.let {
                                navController.apply {
                                    navigate(navigationDrawerTabs[it].route) {
                                        restoreState = true
                                        launchSingleTop = true
                                        graph.startDestinationRoute?.let {
                                            popUpTo(route = HomeDestinationsScreen.SearchScreen.route) {
                                                saveState = true
                                            }
                                        }
                                    }
                                }
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
                                    } else it,
                                    contentDescription = item.title
                                )
                            }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                    if (index == (navigationDrawerTabs.size - 2)) {
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    }
                }
            }
        },
        gesturesEnabled = isNavScreen
    ) {
        Scaffold(
            topBar = {
                if (isNavScreen) {
                    TopAppBar(
                        title = {
                            Text("Dashboard")
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch {
                                    drawerState.apply {
                                        if (isClosed) open() else close()
                                    }
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Menu"
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = { }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.Logout,
                                    contentDescription = null
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(White)
                    )
                }
            },
            bottomBar = {
                if (isNavScreen) {
                    Column {
                        route?.let {
                            MifosNavigationBar(route = it) { target ->
                                navController.apply {
                                    navigate(target) {
                                        restoreState = true
                                        launchSingleTop = true
                                        graph.startDestinationRoute?.let {
                                            popUpTo(route = HomeDestinationsScreen.SearchScreen.route) {
                                                saveState = true
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            containerColor = White
        ) { paddingValues ->
            Navigation(navController = navController, padding = paddingValues)
        }
    }
}