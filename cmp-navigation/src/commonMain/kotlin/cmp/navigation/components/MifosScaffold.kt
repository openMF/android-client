/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package cmp.navigation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.mifos.core.designsystem.component.MifosNavigationRail
import com.mifos.core.designsystem.component.MifosPullToRefreshState
import com.mifos.core.designsystem.component.rememberMifosPullToRefreshState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Suppress("LongMethod")
@Composable
fun MifosScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = { },
    utilityBar: @Composable () -> Unit = { },
    overlay: @Composable () -> Unit = { },
    snackbarHost: @Composable () -> Unit = { },
    floatingActionButton: @Composable () -> Unit = { },
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    navigationData: ScaffoldNavigationData? = null,
    pullToRefreshState: MifosPullToRefreshState = rememberMifosPullToRefreshState(),
    contentWindowInsets: WindowInsets = ScaffoldDefaults
        .contentWindowInsets
        .only(WindowInsetsSides.Horizontal),
    content: @Composable () -> Unit,
) {
    val windowSize = calculateWindowSizeClass()
    val isCompact = windowSize.widthSizeClass == WindowWidthSizeClass.Compact
    val hasNavigationItems = navigationData?.shouldShowNavigation == true
    val isNavigationRailVisible = !isCompact && hasNavigationItems
    val isNavigationBarVisible = isCompact && hasNavigationItems
    Scaffold(
        modifier = modifier,
        topBar = if (isNavigationBarVisible) {
            topBar
        } else {
            {}
        },
        bottomBar = {
            AnimatedVisibility(
                visible = isNavigationBarVisible,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }),
            ) {
                ScaffoldBottomAppBar(navigationData = requireNotNull(navigationData))
            }
        },
        snackbarHost = {
            Box(modifier = Modifier.imePadding()) {
                snackbarHost()
            }
        },
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        contentWindowInsets = WindowInsets(0.dp),
        content = { paddingValues ->
            Row(
                modifier = Modifier
                    .padding(paddingValues = paddingValues)
                    .consumeWindowInsets(paddingValues = paddingValues)
                    .imePadding(),
            ) {
                if (isNavigationRailVisible) {
                    ScaffoldNavigationRail(navigationData = navigationData)
                }
                Box(
                    modifier = Modifier.run {
                        if (isNavigationBarVisible) {
                            consumeWindowInsets(
                                insets = WindowInsets.navigationBars.only(WindowInsetsSides.Bottom),
                            )
                        } else {
                            this
                        }
                    },
                ) {
                    Column {
                        utilityBar()
                        val internalPullToRefreshState = rememberPullToRefreshState()
                        Box(
                            modifier = Modifier
                                .windowInsetsPadding(insets = contentWindowInsets)
                                .pullToRefresh(
                                    state = internalPullToRefreshState,
                                    isRefreshing = pullToRefreshState.isRefreshing,
                                    onRefresh = pullToRefreshState.onRefresh,
                                    enabled = pullToRefreshState.isEnabled,
                                ),
                        ) {
                            content()

                            PullToRefreshDefaults.Indicator(
                                modifier = Modifier.align(Alignment.TopCenter),
                                isRefreshing = pullToRefreshState.isRefreshing,
                                state = internalPullToRefreshState,
                                containerColor = MaterialTheme.colorScheme.tertiary,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                    overlay()
                }
            }
        },
    )
}

@Composable
private fun ScaffoldBottomAppBar(
    navigationData: ScaffoldNavigationData,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        MifosBottomBar(
            navigationItems = navigationData.navigationItems,
            selectedItem = navigationData.selectedNavigationItem,
            onClick = navigationData.onNavigationClick,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(tag = "NavigationBarContainer"),
        )
    }
}

@Composable
private fun ScaffoldNavigationRail(
    navigationData: ScaffoldNavigationData,
    modifier: Modifier = Modifier,
) {
    // We set the z-index to 1f in order to make sure the content transitions
    // animate in under the navigation rail.
    Box(
        modifier = modifier
            .fillMaxHeight()
            .zIndex(zIndex = 1f),
    ) {
        MifosNavigationRail(
            navigationItems = navigationData.navigationItems,
            selectedItem = navigationData.selectedNavigationItem,
            onClick = navigationData.onNavigationClick,
            modifier = Modifier
                .fillMaxHeight()
                .wrapContentWidth()
                .testTag(tag = "NavigationBarContainer"),
        )
    }
}
