package com.mifos.mifosxdroid.online;

import com.mifos.api.datamanager.DataManagerGroups;
import com.mifos.mifosxdroid.FakeRemoteDataSource;
import com.mifos.mifosxdroid.online.groupslist.GroupsListMvpView;
import com.mifos.mifosxdroid.online.groupslist.GroupsListPresenter;
import com.mifos.mifosxdroid.util.RxSchedulersOverrideRule;
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

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    int limit = 20;

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

        when(mDataManagerGroups.getGroups(true, offset, limit))
                .thenReturn(Observable.just(groupPage));

        mGroupsListPresenter.loadGroups(true, offset, limit);

        verify(mGroupsListMvpView).showGroups(groupPage);
        verify(mGroupsListMvpView, never()).showFetchingError("Failed to load Groups");
    }

    @Test
    public void testLoadGroupFails() {

        when(mDataManagerGroups.getGroups(true, offset, limit))
                .thenReturn(Observable.<Page<Group>>error(new RuntimeException()));

        mGroupsListPresenter.loadGroups(true, offset, limit);

        verify(mGroupsListMvpView).showFetchingError("Failed to load Groups");
        verify(mGroupsListMvpView, never()).showGroups(groupPage);
    }

}