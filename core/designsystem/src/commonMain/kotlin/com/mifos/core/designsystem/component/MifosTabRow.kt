/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
@file:OptIn(ExperimentalFoundationApi::class)

package com.mifos.core.designsystem.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.mifos.core.designsystem.theme.primaryLight
import com.mifos.core.designsystem.utils.TabContent
import kotlinx.coroutines.launch

@Composable
fun MifosTabRow(
    tabContents: List<TabContent>,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.White,
    selectedContentColor: Color = primaryLight,
    unselectedContentColor: Color = Color.LightGray,
) {
    val scope = rememberCoroutineScope()

    Column(modifier = modifier) {
        TabRow(
            containerColor = containerColor,
            selectedTabIndex = pagerState.currentPage,
            indicator = {},
            divider = {},
        ) {
            tabContents.forEachIndexed { index, currentTab ->
                Tab(
                    text = { Text(text = currentTab.tabName) },
                    selected = pagerState.currentPage == index,
                    selectedContentColor = selectedContentColor,
                    unselectedContentColor = unselectedContentColor,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            tabContents.getOrNull(page)?.content?.invoke() ?: Text("Page $page")
        }
    }
}
