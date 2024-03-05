package com.mifos.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mifos.core.objects.client.Page
import com.mifos.core.objects.group.Group
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.online.groupslist.GroupsListRepository
import com.mifos.mifosxdroid.online.groupslist.GroupsListUiState
import com.mifos.mifosxdroid.online.groupslist.GroupsListViewModel
import com.mifos.mifosxdroid.util.RxSchedulersOverrideRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import rx.Observable

/**
 * Created by Aditya Gupta on 06/09/23.
 */
@RunWith(MockitoJUnitRunner::class)
class GroupsListViewModelTest {


    @get:Rule
    val overrideSchedulersRule = RxSchedulersOverrideRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var groupsListRepository: GroupsListRepository

    private lateinit var groupsListViewModel: GroupsListViewModel

    @Mock
    private lateinit var groupsListObserver: Observer<GroupsListUiState>

    @Mock
    private lateinit var mockGroupList: Page<Group>

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        groupsListViewModel = GroupsListViewModel(groupsListRepository)
        groupsListViewModel.groupsListUiState.observeForever(groupsListObserver)


    }

    @Test
    fun testGroupsListDatabase_SuccessfulGroupsListReceivedFromRepository_ReturnsGroupsList() {
        val list1 = Mockito.mock(Group::class.java)
        val list2 = Mockito.mock(Group::class.java)
        val list = listOf(list1, list2)
        val mockPage = Page(2, list)

        Mockito.`when`(
            groupsListRepository.getGroups(
                Mockito.anyBoolean(),
                Mockito.anyInt(),
                Mockito.anyInt()
            )
        ).thenReturn(Observable.just(mockPage))
        groupsListViewModel.loadGroups(false, 1)
        Mockito.verify(groupsListObserver).onChanged(GroupsListUiState.ShowProgressbar)
        Mockito.verify(groupsListObserver, Mockito.never())
            .onChanged(GroupsListUiState.ShowEmptyGroups(R.string.group))
        Mockito.verify(groupsListObserver, Mockito.never())
            .onChanged(GroupsListUiState.UnregisterSwipeAndScrollListener)
        Mockito.verify(groupsListObserver, Mockito.never())
            .onChanged(GroupsListUiState.ShowEmptyGroups(R.string.group))
        Mockito.verify(groupsListObserver, Mockito.never())
            .onChanged(GroupsListUiState.ShowFetchingError)
        Mockito.verifyNoMoreInteractions(groupsListObserver)
    }

    @Test
    fun testGroupsListDatabase_SuccessfulGroupsListReceivedFromRepository_ReturnsEmptyGroupsList() {
        Mockito.`when`(
            groupsListRepository.getGroups(
                Mockito.anyBoolean(),
                Mockito.anyInt(),
                Mockito.anyInt()
            )
        ).thenReturn(Observable.just(mockGroupList))
        groupsListViewModel.loadGroups(false, 1)
        Mockito.verify(groupsListObserver).onChanged(GroupsListUiState.ShowProgressbar)
        Mockito.verify(groupsListObserver)
            .onChanged(GroupsListUiState.ShowEmptyGroups(R.string.group))
        Mockito.verify(groupsListObserver)
            .onChanged(GroupsListUiState.UnregisterSwipeAndScrollListener)
        Mockito.verify(groupsListObserver)
            .onChanged(GroupsListUiState.ShowEmptyGroups(R.string.group))
        Mockito.verify(groupsListObserver, Mockito.never())
            .onChanged(GroupsListUiState.ShowFetchingError)
        Mockito.verifyNoMoreInteractions(groupsListObserver)
    }

    @Test
    fun testGroupsListDatabase_UnsuccessfulGroupsListReceivedFromRepository_ReturnsError() {
        Mockito.`when`(
            groupsListRepository.getGroups(
                Mockito.anyBoolean(),
                Mockito.anyInt(),
                Mockito.anyInt()
            )
        ).thenReturn(Observable.error(RuntimeException("some error message")))
        groupsListViewModel.loadGroups(false, 1)
        Mockito.verify(groupsListObserver).onChanged(GroupsListUiState.ShowProgressbar)
        Mockito.verify(groupsListObserver, Mockito.never())
            .onChanged(GroupsListUiState.ShowEmptyGroups(R.string.group))
        Mockito.verify(groupsListObserver, Mockito.never())
            .onChanged(GroupsListUiState.UnregisterSwipeAndScrollListener)
        Mockito.verify(groupsListObserver).onChanged(GroupsListUiState.ShowFetchingError)
        Mockito.verifyNoMoreInteractions(groupsListObserver)
    }

    @Test
    fun testGroupsListDatabase_SuccessfulDbGroupsListReceivedFromRepository_ReturnsGroupsList() {
        Mockito.`when`(groupsListRepository.databaseGroups())
            .thenReturn(Observable.just(mockGroupList))
        groupsListViewModel.loadDatabaseGroups()
        Mockito.verify(groupsListObserver).onChanged(GroupsListUiState.ShowProgressbar)
        Mockito.verify(groupsListObserver, Mockito.never())
            .onChanged(GroupsListUiState.ShowMessage(R.string.failed_to_load_db_groups))
        Mockito.verifyNoMoreInteractions(groupsListObserver)
    }

    @Test
    fun testGroupsListDatabase_UnsuccessfulDbGroupsListReceivedFromRepository_ReturnsError() {
        Mockito.`when`(groupsListRepository.databaseGroups())
            .thenReturn(Observable.error(RuntimeException("some error message")))
        groupsListViewModel.loadDatabaseGroups()
        Mockito.verify(groupsListObserver).onChanged(GroupsListUiState.ShowProgressbar)
        Mockito.verify(groupsListObserver)
            .onChanged(GroupsListUiState.ShowMessage(R.string.failed_to_load_db_groups))
        Mockito.verifyNoMoreInteractions(groupsListObserver)
    }


    @After
    fun tearDown() {
        groupsListViewModel.groupsListUiState.removeObserver(groupsListObserver)
    }
}