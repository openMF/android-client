package com.mifos.feature.groups

import androidx.activity.ComponentActivity
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertContentDescriptionContains
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onLast
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.mifos.core.designsystem.theme.BlueSecondary
import com.mifos.core.domain.use_cases.GroupsListPagingDataSource
import com.mifos.core.objects.group.Group
import com.mifos.core.testing.repository.TestGroupsListRepository
import com.mifos.core.testing.repository.sampleGroups
import com.mifos.feature.groups.group_list.GroupsListScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals


class GroupListScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val repository = TestGroupsListRepository()
    private val pageSize = 10

    private fun createPager(
        config: PagingConfig = PagingConfig(
            pageSize = pageSize,
            enablePlaceholders = true,
            initialLoadSize = pageSize,
            prefetchDistance = 0,
        ),
        pagingSourceFactory: () -> PagingSource<Int, Group> = {
            GroupsListPagingDataSource(repository, pageSize)
        }
    ): Pager<Int, Group> {
        return Pager(config = config, pagingSourceFactory = pagingSourceFactory)
    }

    private fun createPagerWithoutPlaceholder(
        config: PagingConfig = PagingConfig(
            pageSize = pageSize,
            enablePlaceholders = false,
            initialLoadSize = pageSize,
            prefetchDistance = 1,
        ),
        pagingSourceFactory: () -> PagingSource<Int, Group> = {
            GroupsListPagingDataSource(repository, pageSize)
        }
    ): Pager<Int, Group> {
        return Pager(config = config, pagingSourceFactory = pagingSourceFactory)
    }

    @Test
    fun checkForInitialLoadingState() {
        val pager = createPager()
        val loadStates: MutableList<CombinedLoadStates> = mutableListOf()
        composeTestRule.setContent {
            val lazyPagingItems = pager.flow.collectAsLazyPagingItems()
            loadStates.add(lazyPagingItems.loadState)
        }

        composeTestRule.waitForIdle()

        val expected = CombinedLoadStates(
            refresh = LoadState.Loading,
            prepend = LoadState.NotLoading(false),
            append = LoadState.NotLoading(false),
            source = LoadStates(
                LoadState.Loading,
                LoadState.NotLoading(false),
                LoadState.NotLoading(false)
            ),
            mediator = null
        )
        assert(loadStates.isNotEmpty())
        assertEquals(loadStates.first(), expected)
    }

    @Test
    fun checkForEmptyDataState() {
        val pager = createPager()

        repository.setGroupsData(emptyList())
        lateinit var lazyPagingItems: LazyPagingItems<Group>

        composeTestRule.setContent {
            lazyPagingItems = pager.flow.collectAsLazyPagingItems()

            GroupsListScreen(
                lazyListState = rememberLazyListState(),
                swipeRefreshState = rememberSwipeRefreshState(isRefreshing = false),
                data = lazyPagingItems,
                selectedItems = emptyList(),
                onGroupClick = {},
                onSelectItem = {},
                onAddGroupClick = {},
                onSyncClick = {},
                resetSelectionMode = {},
            )
        }

        composeTestRule.waitForIdle()

        assertEquals(LoadState.NotLoading(false), lazyPagingItems.loadState.refresh)

        composeTestRule
            .onNodeWithContentDescription("MifosEmptyUi")
            .assertExists()
            .onChildren()
            .onLast()
            .assertTextContains(composeTestRule.activity.resources.getString(R.string.feature_groups_no_more_groups_available))

    }

    @Test
    fun checkForRefreshState() {
        val pager = createPager()

        lateinit var lazyPagingItems: LazyPagingItems<Group>
        lateinit var refreshState: SwipeRefreshState

        composeTestRule.setContent {
            // Adding some delay to test refresh state
            lazyPagingItems = pager.flow.onStart { delay(5000) }.collectAsLazyPagingItems()
            refreshState = rememberSwipeRefreshState(
                isRefreshing = lazyPagingItems.loadState.refresh is LoadState.Loading
            )

            GroupsListScreen(
                lazyListState = rememberLazyListState(),
                swipeRefreshState = refreshState,
                data = lazyPagingItems,
                selectedItems = emptyList(),
                onGroupClick = {},
                onSelectItem = {},
                onAddGroupClick = {},
                onSyncClick = {},
                resetSelectionMode = {},
            )
        }

        // Check SwipeRefresh is displayed in screen
        composeTestRule
            .onNodeWithContentDescription("SwipeRefresh::GroupList")
            .assertIsDisplayed()

        // Check initial refresh state is true
        assertEquals(true, refreshState.isRefreshing)

        //Perform refresh and check refresh state is true
        lazyPagingItems.refresh()

        assertEquals(true, refreshState.isRefreshing)
    }

    @Test
    fun checkForErrorStateAndRetryFunction() {
        val pager = createPager()

        repository.setGroupsData(null)
        lateinit var lazyPagingItems: LazyPagingItems<Group>

        composeTestRule.setContent {
            lazyPagingItems = pager.flow.collectAsLazyPagingItems()

            GroupsListScreen(
                lazyListState = rememberLazyListState(),
                swipeRefreshState = rememberSwipeRefreshState(isRefreshing = false),
                data = lazyPagingItems,
                selectedItems = emptyList(),
                onGroupClick = {},
                onSelectItem = {},
                onAddGroupClick = {},
                onSyncClick = {},
                resetSelectionMode = {},
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithContentDescription("MifosSweetError")
            .assertIsDisplayed()
            .onChildren()[1]
            .assertTextContains(composeTestRule.activity.resources.getString(R.string.feature_groups_failed_to_fetch_groups))

        composeTestRule
            .onNodeWithContentDescription("MifosSweetError")
            .assertIsDisplayed()
            .onChildren()[2]
            .assertHasClickAction()
            .assertTextContains("Try Again")

        repository.setGroupsData(sampleGroups)

        composeTestRule
            .onNodeWithContentDescription("MifosSweetError")
            .assertIsDisplayed()
            .onChildren()[2]
            .assertHasClickAction()
            .assertTextContains("Try Again")
            .performClick()
    }

    @Test
    fun checkForSuccessfulState() {
        repository.setGroupsData(sampleGroups)
        val pager = createPager()


        lateinit var lazyPagingItems: LazyPagingItems<Group>
        lateinit var lazyListState: LazyListState

        composeTestRule.setContent {
            lazyListState = rememberLazyListState()
            lazyPagingItems = pager.flow.collectAsLazyPagingItems()

            GroupsListScreen(
                lazyListState = lazyListState,
                swipeRefreshState = rememberSwipeRefreshState(isRefreshing = false),
                data = lazyPagingItems,
                selectedItems = emptyList(),
                onGroupClick = {},
                onSelectItem = {},
                onAddGroupClick = {},
                onSyncClick = {},
                resetSelectionMode = {},
            )
        }

        assertEquals(LoadState.NotLoading(false), lazyPagingItems.loadState.refresh)

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithContentDescription("MifosSweetError")
            .assertIsNotDisplayed()

        composeTestRule.waitForIdle()

        assertEquals(
            expected = sampleGroups.take(pageSize),
            actual = lazyPagingItems.itemSnapshotList.items,
            message = "Sample Groups are differ from Snapshot items"
        )

        //Check paged item is being displayed
        sampleGroups.take(pageSize).forEach { group ->
            group.name?.let {
                composeTestRule
                    .onNodeWithTag(it)
                    .assertIsDisplayed()
            }
        }

        composeTestRule.waitForIdle()

        // And not more than item will displayed
        sampleGroups[pageSize + 1].name?.let {
            composeTestRule
                .onNodeWithTag(it)
                .assertDoesNotExist()
        }
    }

    @Test
    fun checkForAppendDataState() {
        val pager = createPagerWithoutPlaceholder()

        lateinit var lazyPagingItems: LazyPagingItems<Group>
        lateinit var lazyListState: LazyListState

        composeTestRule.setContent {
            lazyListState = rememberLazyListState()
            lazyPagingItems = pager.flow.collectAsLazyPagingItems()

            GroupsListScreen(
                lazyListState = lazyListState,
                swipeRefreshState = rememberSwipeRefreshState(isRefreshing = false),
                data = lazyPagingItems,
                selectedItems = emptyList(),
                onGroupClick = {},
                onSelectItem = {},
                onAddGroupClick = {},
                onSyncClick = {},
                resetSelectionMode = {},
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule.runOnIdle {
            runBlocking {
                lazyListState.scrollToItem(pageSize + 1)
            }
        }

        assertEquals(
            pageSize * 2,
            lazyPagingItems.itemSnapshotList.items.size
        )

        assertEquals(
            LoadState.NotLoading(false),
            lazyPagingItems.loadState.append
        )

        composeTestRule.runOnIdle {
            runBlocking {
                lazyListState.scrollToItem(pageSize * 2 + 1)
            }
        }

        composeTestRule.waitForIdle()

        // end of pagination reached so total item will be (pageSize * 3) + 1 = 31
        assertEquals(
            pageSize * 3 + 1,
            lazyListState.layoutInfo.totalItemsCount
        )

    }

    @Test
    fun checkTheSelectedGroupItemAndVisibilityOfSelectionModeTopAppBar() {
        val pager = createPager()
        val selectedItems = mutableStateListOf<Group>()

        lateinit var lazyPagingItems: LazyPagingItems<Group>
        lateinit var lazyListState: LazyListState
        var surfaceColor = Color.Unspecified
        composeTestRule.mainClock.autoAdvance = false

        composeTestRule.setContent {
            lazyListState = rememberLazyListState()
            lazyPagingItems = pager.flow.collectAsLazyPagingItems()
            surfaceColor = MaterialTheme.colorScheme.surface

            GroupsListScreen(
                lazyListState = lazyListState,
                swipeRefreshState = rememberSwipeRefreshState(isRefreshing = false),
                data = lazyPagingItems,
                selectedItems = selectedItems.toList(),
                onGroupClick = {},
                onSelectItem = {
                    if (selectedItems.contains(it)) {
                        selectedItems.remove(it)
                    } else {
                        selectedItems.add(it)
                    }
                },
                onAddGroupClick = {},
                onSyncClick = {},
                resetSelectionMode = {
                    selectedItems.clear()
                },
            )
        }

        assertEquals(LoadState.NotLoading(false), lazyPagingItems.loadState.refresh)

        composeTestRule.waitForIdle()

        // Check Group is displayed in screen and has click action
        sampleGroups[2].name?.let {
            composeTestRule
                .onNodeWithTag(it)
                .assertIsDisplayed()
                .assertHasClickAction()
        }

        // Perform long click that item
        sampleGroups[2].name?.let {
            composeTestRule
                .onNodeWithTag(it)
                .assertIsDisplayed()
                .assertHasClickAction()
                .performTouchInput {
                    longClick()
                }
        }

        composeTestRule.mainClock.advanceTimeByFrame()
        composeTestRule.waitForIdle()
        composeTestRule.mainClock.advanceTimeByFrame()
        composeTestRule.mainClock.advanceTimeBy(250)

        // Check item has been selected
        assert(selectedItems.contains(sampleGroups[2]))

        // Select another group item
        sampleGroups[4].name?.let {
            composeTestRule
                .onNodeWithTag(it)
                .assertIsDisplayed()
                .assertHasClickAction()
                .performTouchInput {
                    longClick()
                }
        }


        // Check item has been selected
        assert(selectedItems.contains(sampleGroups[4]))

        //Check both item has been selected
        assertEquals(
            2,
            selectedItems.size
        )

        composeTestRule.waitForIdle()

        // check for background color change of selected item
        sampleGroups[4].name?.let {
            val data = composeTestRule
                .onNodeWithTag(it)
                .captureToImage()

            assertEquals(BlueSecondary.colorSpace, data.colorSpace)
        }

        composeTestRule.waitUntil {
            selectedItems.size == 2
        }

        composeTestRule.waitForIdle()

        //check for Contextual TopAppBar Visibility
        composeTestRule
            .onNodeWithContentDescription("GroupList::ContextualTopAppBar")
            .assertIsDisplayed()

        //Check reset selection IconButton is visible or not
        composeTestRule
            .onNodeWithContentDescription("reset selection")
            .assertIsDisplayed()
            .assertHasClickAction()

        //Check selected text is visible or not
        composeTestRule
            .onNodeWithContentDescription("GroupList::ContextualTopAppBar")
            .assertIsDisplayed()
            .onChildren()[1]
            .assertTextContains("${selectedItems.size} selected")
            .assertIsDisplayed()

        //Check Sync Button is visible or not
        composeTestRule
            .onNodeWithContentDescription("GroupList::ContextualTopAppBar")
            .assertIsDisplayed()
            .onChildren()[2]
            .assertContentDescriptionContains("Sync Items")
            .assertIsDisplayed()
            .assertHasClickAction()

        // now deselect an item by clicking and check item has been removed or not from list
        sampleGroups[2].name?.let {
            composeTestRule
                .onNodeWithTag(it)
                .performClick()
        }

        // check item removed from selected list or not
        assert(!selectedItems.contains(sampleGroups[2]))

        // and selected list only contain 1 item
        assert(selectedItems.size == 1)

        //also check the background color of that item is set to surfaceColor
        sampleGroups[2].name?.let {
            val data = composeTestRule
                .onNodeWithTag(it)
                .captureToImage()

            assertEquals(data.colorSpace, surfaceColor.colorSpace)
        }

        // Deselect all item by clicking on reset-selection button
        composeTestRule
            .onNodeWithContentDescription("reset selection")
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()

        composeTestRule.waitForIdle()

        // check selected items list should be empty
        assert(selectedItems.isEmpty())
    }
}