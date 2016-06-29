package com.mifos.mifosxdroid.online;

import com.mifos.api.DataManager;
import com.mifos.api.datamanager.DataManagerCenter;
import com.mifos.mifosxdroid.FakeRemoteDataSource;
import com.mifos.mifosxdroid.online.centerlist.CenterListMvpView;
import com.mifos.mifosxdroid.online.centerlist.CenterListPresenter;
import com.mifos.mifosxdroid.util.RxSchedulersOverrideRule;
import com.mifos.objects.client.Page;
import com.mifos.objects.group.Center;
import com.mifos.objects.group.CenterWithAssociations;

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
 * Created by Rajan Maurya on 19/6/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class CenterListPresenterTest {


    @Rule
    public final RxSchedulersOverrideRule mOverrideSchedulersRule = new RxSchedulersOverrideRule();

    CenterListPresenter centerListPresenter;

    @Mock
    DataManagerCenter mDataManagerCenter;

    @Mock
    CenterListMvpView mCenterListMvpView;

    Page<Center> centerPage;

    CenterWithAssociations centerWithAssociations;

    int offset = 0;
    int limit = 100;
    int centerId = 1;

    @Before
    public void setUp() throws Exception {

        centerListPresenter = new CenterListPresenter(mDataManagerCenter);
        centerListPresenter.attachView(mCenterListMvpView);

        centerPage = FakeRemoteDataSource.getCenters();
        centerWithAssociations = FakeRemoteDataSource.getCentersGroupAndMeeting();

    }

    @After
    public void tearDown() throws Exception {
        centerListPresenter.detachView();
    }

    @Test
    public void testLoadCenters() {

        when(mDataManagerCenter.getCenters(true, offset, limit))
                .thenReturn(Observable.just(centerPage));

        centerListPresenter.loadCenters(true, offset, limit);

        verify(mCenterListMvpView).showCenters(centerPage);
        verify(mCenterListMvpView, never()).showFetchingError("Failed to load Centers");
    }

    @Test
    public void testLoadCentersFails() {

        when(mDataManagerCenter.getCenters(true, offset, limit))
                .thenReturn(Observable.<Page<Center>>error(new RuntimeException()));

        centerListPresenter.loadCenters(true, offset, limit);

        verify(mCenterListMvpView).showFetchingError("Failed to load Centers");
        verify(mCenterListMvpView, never()).showCenters(centerPage);
    }

    @Test
    public void testLoadCentersGroupAndMeeting() {

        when(mDataManagerCenter.getCentersGroupAndMeeting(centerId))
                .thenReturn(Observable.just(centerWithAssociations));

        centerListPresenter.loadCentersGroupAndMeeting(centerId);

        verify(mCenterListMvpView).showCentersGroupAndMeeting(centerWithAssociations, centerId);
        verify(mCenterListMvpView, never())
                .showFetchingError("Failed to load Center Groups and Meeting");
    }

    @Test
    public void testLoadCenterGroupAndMeetingFails() {
        when(mDataManagerCenter.getCentersGroupAndMeeting(centerId))
                .thenReturn(Observable.<CenterWithAssociations>error(new RuntimeException()));

        centerListPresenter.loadCentersGroupAndMeeting(centerId);

        verify(mCenterListMvpView).showFetchingError("Failed to load Center Groups and Meeting");
        verify(mCenterListMvpView, never()).showCentersGroupAndMeeting(centerWithAssociations,
                centerId);
    }

}