package com.mifos.mifosxdroid.online

import com.mifos.api.datamanager.DataManagerGroups
import com.mifos.mifosxdroid.FakeRemoteDataSource.groups
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.online.groupslist.GroupsListMvpView
import com.mifos.mifosxdroid.online.groupslist.GroupsListPresenter
import com.mifos.mifosxdroid.util.RxSchedulersOverrideRule
import com.mifos.objects.client.Page
import com.mifos.objects.group.Group
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import rx.Observable

/**
 * Created by Rajan Maurya on 28/6/16.
 */
@RunWith(MockitoJUnitRunner::class)
class GroupsListPresenterTest {
    @get:Rule
    val mOverrideSchedulersRule = RxSchedulersOverrideRule()
    private lateinit var mGroupsListPresenter: GroupsListPresenter

    @Mock
    lateinit var mDataManagerGroups: DataManagerGroups

    @Mock
    lateinit var mGroupsListMvpView: GroupsListMvpView
    private lateinit var groupPage: Page<Group>
    var offset = 0
    var limit = 100

    @Before
    fun setUp() {
        mGroupsListPresenter = GroupsListPresenter(mDataManagerGroups)
        mGroupsListPresenter.attachView(mGroupsListMvpView)
        groupPage = groups
    }

    @After
    fun tearDown() {
        mGroupsListPresenter.detachView()
    }

    @Test
    fun testLoadGroup() {
        stubDatabaseGetAllGroups(Observable.just(groupPage))
        mGroupsListPresenter.loadDatabaseGroups()
        mGroupsListPresenter.setAlreadyClientSyncStatus()
        Mockito.verify(mGroupsListMvpView, Mockito.never())
            .showMessage(R.string.failed_to_load_db_groups)
        stubDataManagerGetGroups(Observable.just(groupPage))
        mGroupsListPresenter.loadGroups(false, offset)
        Mockito.verify(mGroupsListMvpView).showProgressbar(true)
        Mockito.verify(mGroupsListMvpView).showGroups(groupPage.pageItems)
        Mockito.verify(mGroupsListMvpView, Mockito.never()).showFetchingError()
        Mockito.verify(mGroupsListMvpView).showProgressbar(false)
    }

    @Test
    fun testLoadGroupFails() {
        stubDataManagerGetGroups(Observable.error(RuntimeException()))
        mGroupsListPresenter.loadGroups(false, offset)
        Mockito.verify(mGroupsListMvpView).showFetchingError()
        Mockito.verify(mGroupsListMvpView, Mockito.never()).showGroups(groupPage.pageItems)
    }

    @Test
    fun testLoadMoreGroups() {
        stubDatabaseGetAllGroups(Observable.just(groupPage))
        mGroupsListPresenter.loadDatabaseGroups()
        mGroupsListPresenter.setAlreadyClientSyncStatus()
        Mockito.verify(mGroupsListMvpView, Mockito.never())
            .showMessage(R.string.failed_to_load_db_groups)
        stubDataManagerGetGroups(Observable.just(groupPage))
        mGroupsListPresenter.loadGroups(true, offset)
        Mockito.verify(mGroupsListMvpView).showProgressbar(true)
        Mockito.verify(mGroupsListMvpView).showLoadMoreGroups(groupPage.pageItems)
        Mockito.verify(mGroupsListMvpView, Mockito.never()).showFetchingError()
        Mockito.verify(mGroupsListMvpView).showProgressbar(false)
    }

    @Test
    fun testLoadMoreGroupFails() {
        stubDataManagerGetGroups(Observable.error(RuntimeException()))
        mGroupsListPresenter.loadGroups(true, offset)
        Mockito.verify(mGroupsListMvpView).showMessage(R.string.failed_to_fetch_groups)
        Mockito.verify(mGroupsListMvpView, Mockito.never()).showGroups(groupPage.pageItems)
    }

    @Test
    fun testEmptyGroupList() {
        stubDataManagerGetGroups(Observable.just(Page<Group>()))
        mGroupsListPresenter.loadGroups(false, offset)
        Mockito.verify(mGroupsListMvpView).showProgressbar(true)
        Mockito.verify(mGroupsListMvpView).showEmptyGroups(R.string.group)
        Mockito.verify(mGroupsListMvpView).unregisterSwipeAndScrollListener()
        Mockito.verify(mGroupsListMvpView, Mockito.never()).showFetchingError()
        Mockito.verify(mGroupsListMvpView).showProgressbar(false)
    }

    @Test
    fun testNoMoreGroupsAvailable() {
        stubDataManagerGetGroups(Observable.just(Page<Group>()))
        mGroupsListPresenter.loadGroups(true, offset)
        Mockito.verify(mGroupsListMvpView).showProgressbar(true)
        Mockito.verify(mGroupsListMvpView).showMessage(R.string.no_more_groups_available)
        Mockito.verify(mGroupsListMvpView, Mockito.never()).showFetchingError()
        Mockito.verify(mGroupsListMvpView).showProgressbar(false)
    }

    @Test
    fun testLoadDatabaseGroups() {
        stubDatabaseGetAllGroups(Observable.just(groupPage))
        mGroupsListPresenter.loadDatabaseGroups()
        mGroupsListPresenter.setAlreadyClientSyncStatus()
        Mockito.verify(mGroupsListMvpView, Mockito.never())
            .showMessage(R.string.failed_to_load_db_groups)
    }

    @Test
    fun testLoadDatabaseGroupsFails() {
        stubDatabaseGetAllGroups(Observable.error(RuntimeException()))
        mGroupsListPresenter.loadDatabaseGroups()
        Mockito.verify(mGroupsListMvpView).showMessage(R.string.failed_to_load_db_groups)
    }

    private fun stubDataManagerGetGroups(observable: Observable<Page<Group>>) {
        Mockito.`when`(
            mDataManagerGroups.getGroups(true, offset, limit)
        ).thenReturn(observable)
    }

    private fun stubDatabaseGetAllGroups(observable: Observable<Page<Group>>) {
        Mockito.`when`(
            mDataManagerGroups.databaseGroups
        ).thenReturn(observable)
    }
}