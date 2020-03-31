package com.mifos.mifosxdroid.online;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mifos.api.datamanager.DataManagerGroups;
import com.mifos.mifosxdroid.FakeRemoteDataSource;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.online.groupslist.GroupsListMvpView;
import com.mifos.mifosxdroid.online.groupslist.GroupsListPresenter;
import com.mifos.mifosxdroid.util.RxSchedulersOverrideRule;
import com.mifos.objects.client.Client;
import com.mifos.objects.client.Page;
import com.mifos.objects.group.Group;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import rx.Observable;

/**
 * Created by Rajan Maurya on 28/6/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class GroupsListPresenterTest {

    @Rule
    public final RxSchedulersOverrideRule mOverrideSchedulersRule = new RxSchedulersOverrideRule();

    GroupsListPresenter mGroupsListPresenter;

    @Mock
    DataManagerGroups mDataManagerGroups;

    @Mock
    GroupsListMvpView mGroupsListMvpView;

    Page<Group> groupPage;

    int offset = 0;
    int limit = 100;

    @Before
    public void setUp() {

        mGroupsListPresenter = new GroupsListPresenter(mDataManagerGroups);
        mGroupsListPresenter.attachView(mGroupsListMvpView);

        groupPage = FakeRemoteDataSource.getGroups();

    }

    @After
    public void tearDown() {
        mGroupsListPresenter.detachView();
    }

    @Test
    public void testLoadGroup() {

        stubDatabaseGetAllGroups(Observable.just(groupPage));

        mGroupsListPresenter.loadDatabaseGroups();

        mGroupsListPresenter.setAlreadyClientSyncStatus();
        verify(mGroupsListMvpView, never()).showMessage(R.string.failed_to_load_db_groups);

        stubDataManagerGetGroups(Observable.just(groupPage));

        mGroupsListPresenter.loadGroups(false, offset);

        verify(mGroupsListMvpView).showProgressbar(true);
        verify(mGroupsListMvpView).showGroups(groupPage.getPageItems());
        verify(mGroupsListMvpView, never()).showFetchingError();
        verify(mGroupsListMvpView).showProgressbar(false);
    }

    @Test
    public void testLoadGroupFails() {

        stubDataManagerGetGroups(Observable.<Page<Client>>error(new RuntimeException()));

        mGroupsListPresenter.loadGroups(false, offset);
        verify(mGroupsListMvpView).showFetchingError();
        verify(mGroupsListMvpView, never()).showGroups(groupPage.getPageItems());
    }

    @Test
    public void testLoadMoreGroups() {

        stubDatabaseGetAllGroups(Observable.just(groupPage));

        mGroupsListPresenter.loadDatabaseGroups();

        mGroupsListPresenter.setAlreadyClientSyncStatus();
        verify(mGroupsListMvpView, never()).showMessage(R.string.failed_to_load_db_groups);

        stubDataManagerGetGroups(Observable.just(groupPage));

        mGroupsListPresenter.loadGroups(true, offset);

        verify(mGroupsListMvpView).showProgressbar(true);
        verify(mGroupsListMvpView).showLoadMoreGroups(groupPage.getPageItems());
        verify(mGroupsListMvpView, never()).showFetchingError();
        verify(mGroupsListMvpView).showProgressbar(false);
    }

    @Test
    public void testLoadMoreGroupFails() {

        stubDataManagerGetGroups(Observable.<Page<Client>>error(new RuntimeException()));

        mGroupsListPresenter.loadGroups(true, offset);
        verify(mGroupsListMvpView).showMessage(R.string.failed_to_fetch_groups);
        verify(mGroupsListMvpView, never()).showGroups(groupPage.getPageItems());
    }

    @Test
    public void testEmptyGroupList() {

        stubDataManagerGetGroups(Observable.just(new Page<Client>()));

        mGroupsListPresenter.loadGroups(false, offset);

        verify(mGroupsListMvpView).showProgressbar(true);
        verify(mGroupsListMvpView).showEmptyGroups(R.string.group);
        verify(mGroupsListMvpView).unregisterSwipeAndScrollListener();
        verify(mGroupsListMvpView, never()).showFetchingError();
        verify(mGroupsListMvpView).showProgressbar(false);
    }

    @Test
    public void testNoMoreGroupsAvailable() {

        stubDataManagerGetGroups(Observable.just(new Page<Client>()));

        mGroupsListPresenter.loadGroups(true, offset);

        verify(mGroupsListMvpView).showProgressbar(true);
        verify(mGroupsListMvpView).showMessage(R.string.no_more_groups_available);
        verify(mGroupsListMvpView, never()).showFetchingError();
        verify(mGroupsListMvpView).showProgressbar(false);
    }

    @Test
    public void testLoadDatabaseGroups() {
        stubDatabaseGetAllGroups(Observable.just(groupPage));

        mGroupsListPresenter.loadDatabaseGroups();
        mGroupsListPresenter.setAlreadyClientSyncStatus();
        verify(mGroupsListMvpView, never()).showMessage(R.string.failed_to_load_db_groups);
    }

    @Test
    public void testLoadDatabaseGroupsFails() {
        stubDatabaseGetAllGroups(Observable.<Page<Client>>error(new RuntimeException()));

        mGroupsListPresenter.loadDatabaseGroups();

        verify(mGroupsListMvpView).showMessage(R.string.failed_to_load_db_groups);
    }

    public void stubDataManagerGetGroups(Observable observable) {
        when(mDataManagerGroups.getGroups(true, offset, limit)).thenReturn(observable);
    }

    public void stubDatabaseGetAllGroups(Observable observable) {
        when(mDataManagerGroups.getDatabaseGroups()).thenReturn(observable);
    }

}