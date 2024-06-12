@file:OptIn(ExperimentalFoundationApi::class)

package com.mifos.core.designsystem.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.mifos.core.designsystem.theme.BluePrimary
import com.mifos.core.designsystem.utility.TabContent
import kotlinx.coroutines.launch

@Composable
fun MifosTabRow(
    tabContents: List<TabContent>,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.White,
    selectedContentColor: Color = BluePrimary,
    unselectedContentColor: Color = Color.LightGray
) {
    val scope = rememberCoroutineScope()

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
                }
            )
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = modifier
    ) { page ->
        tabContents.getOrNull(page)?.content?.invoke() ?: Text("Page $page")
    }
}