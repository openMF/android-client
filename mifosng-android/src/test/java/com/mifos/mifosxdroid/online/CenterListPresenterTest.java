package com.mifos.mifosxdroid.online;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mifos.api.datamanager.DataManagerCenter;
import com.mifos.mifosxdroid.FakeRemoteDataSource;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.online.centerlist.CenterListMvpView;
import com.mifos.mifosxdroid.online.centerlist.CenterListPresenter;
import com.mifos.mifosxdroid.util.RxSchedulersOverrideRule;
import com.mifos.objects.client.Client;
import com.mifos.objects.client.Page;
import com.mifos.objects.group.Center;
import com.mifos.objects.group.CenterWithAssociations;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import rx.Observable;

/**
 * Created by Rajan Maurya on 19/6/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class CenterListPresenterTest {


    @Rule
    public final RxSchedulersOverrideRule mOverrideSchedulersRule = new RxSchedulersOverrideRule();

    private CenterListPresenter centerListPresenter;

    @Mock
    DataManagerCenter mDataManagerCenter;

    @Mock
    CenterListMvpView mCenterListMvpView;

    private Page<Center> centerPage;

    private CenterWithAssociations centerWithAssociations;

    private int offset = 0;
    private int limit = 100;
    private int centerId = 1;

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

        stubDataManagerGetCenters(Observable.just(centerPage));

        centerListPresenter.loadCenters(false, offset);

        verify(mCenterListMvpView).showProgressbar(true);
        verify(mCenterListMvpView).showCenters(centerPage.getPageItems());
        verify(mCenterListMvpView, never()).showFetchingError();
        verify(mCenterListMvpView).showProgressbar(false);
    }

    @Test
    public void testLoadCentersFails() {

        stubDataManagerGetCenters(Observable.<Page<Client>>error(new RuntimeException()));

        centerListPresenter.loadCenters(false, offset);

        verify(mCenterListMvpView).showProgressbar(true);
        verify(mCenterListMvpView).showFetchingError();
        verify(mCenterListMvpView, never()).showCenters(centerPage.getPageItems());
        verify(mCenterListMvpView).showProgressbar(false);
    }

    @Test
    public void testLoadMoreCenters() {

        stubDataManagerGetCenters(Observable.just(centerPage));

        centerListPresenter.loadCenters(true, offset);

        verify(mCenterListMvpView).showProgressbar(true);
        verify(mCenterListMvpView).showMoreCenters(centerPage.getPageItems());
        verify(mCenterListMvpView, never()).showMessage(R.string.failed_to_fetch_centers);
        verify(mCenterListMvpView).showProgressbar(false);
    }

    @Test
    public void testLoadMoreCentersFails() {

        stubDataManagerGetCenters(Observable.<Page<Client>>error(new RuntimeException()));

        centerListPresenter.loadCenters(true, offset);

        verify(mCenterListMvpView).showProgressbar(true);
        verify(mCenterListMvpView).showMessage(R.string.failed_to_fetch_centers);
        verify(mCenterListMvpView, never()).showFetchingError();
        verify(mCenterListMvpView).showProgressbar(false);
    }

    @Test
    public void testEmptyCenters() {

        stubDataManagerGetCenters(Observable.just(new Page<Center>()));

        centerListPresenter.loadCenters(false, offset);

        verify(mCenterListMvpView).showProgressbar(true);
        verify(mCenterListMvpView).showEmptyCenters(R.string.center);
        verify(mCenterListMvpView, never()).showFetchingError();
        verify(mCenterListMvpView).showProgressbar(false);
    }

    @Test
    public void testNoMoreGroupsAvailable() {

        stubDataManagerGetCenters(Observable.just(new Page<Center>()));

        centerListPresenter.loadCenters(true, offset);

        verify(mCenterListMvpView).showProgressbar(true);
        verify(mCenterListMvpView).showMessage(R.string.no_more_centers_available);
        verify(mCenterListMvpView, never()).showFetchingError();
        verify(mCenterListMvpView).showProgressbar(false);
    }

    @Test
    public void testLoadCentersGroupAndMeeting() {

        when(mDataManagerCenter.getCentersGroupAndMeeting(centerId))
                .thenReturn(Observable.just(centerWithAssociations));

        centerListPresenter.loadCentersGroupAndMeeting(centerId);

        verify(mCenterListMvpView).showCentersGroupAndMeeting(centerWithAssociations, centerId);
        verify(mCenterListMvpView, never()).showMessage(R.string.failed_to_fetch_Group_and_meeting);
    }

    @Test
    public void testLoadCenterGroupAndMeetingFails() {
        when(mDataManagerCenter.getCentersGroupAndMeeting(centerId))
                .thenReturn(Observable.<CenterWithAssociations>error(new RuntimeException()));

        centerListPresenter.loadCentersGroupAndMeeting(centerId);

        verify(mCenterListMvpView).showMessage(R.string.failed_to_fetch_Group_and_meeting);
        verify(mCenterListMvpView, never())
                .showCentersGroupAndMeeting(centerWithAssociations, centerId);
    }

    public void stubDataManagerGetCenters(Observable observable) {
        when(mDataManagerCenter.getCenters(true, offset, limit)).thenReturn(observable);
    }
}