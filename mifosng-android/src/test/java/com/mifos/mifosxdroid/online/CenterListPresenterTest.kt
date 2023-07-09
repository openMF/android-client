package com.mifos.mifosxdroid.online

import com.mifos.api.datamanager.DataManagerCenter
import com.mifos.mifosxdroid.FakeRemoteDataSource.centers
import com.mifos.mifosxdroid.FakeRemoteDataSource.centersGroupAndMeeting
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.online.centerlist.CenterListMvpView
import com.mifos.mifosxdroid.online.centerlist.CenterListPresenter
import com.mifos.mifosxdroid.util.RxSchedulersOverrideRule
import com.mifos.objects.client.Page
import com.mifos.objects.group.Center
import com.mifos.objects.group.CenterWithAssociations
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
 * Created by Rajan Maurya on 19/6/16.
 */
@RunWith(MockitoJUnitRunner::class)
class CenterListPresenterTest {
    @get:Rule
    val mOverrideSchedulersRule = RxSchedulersOverrideRule()
    private lateinit var centerListPresenter: CenterListPresenter

    @Mock
    lateinit var mDataManagerCenter: DataManagerCenter

    @Mock
    lateinit var mCenterListMvpView: CenterListMvpView
    private lateinit var centerPage: Page<Center>
    private lateinit var centerWithAssociations: CenterWithAssociations
    private val offset = 0
    private val limit = 100
    private val centerId = 1

    @Before
    @Throws(Exception::class)
    fun setUp() {
        centerListPresenter = CenterListPresenter(mDataManagerCenter)
        centerListPresenter.attachView(mCenterListMvpView)
        centerPage = centers
        centerWithAssociations = centersGroupAndMeeting
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        centerListPresenter.detachView()
    }

    @Test
    fun testLoadCenters() {
        stubDataManagerGetCenters(Observable.just(centerPage))
        centerListPresenter.loadCenters(false, offset)
        Mockito.verify(mCenterListMvpView).showProgressbar(true)
        Mockito.verify(mCenterListMvpView).showCenters(centerPage.pageItems)
        Mockito.verify(mCenterListMvpView, Mockito.never())?.showFetchingError()
        Mockito.verify(mCenterListMvpView).showProgressbar(false)
    }

    @Test
    fun testLoadCentersFails() {
        stubDataManagerGetCenters(Observable.error(RuntimeException()))
        centerListPresenter.loadCenters(false, offset)
        Mockito.verify(mCenterListMvpView).showProgressbar(true)
        Mockito.verify(mCenterListMvpView).showFetchingError()
        Mockito.verify(mCenterListMvpView, Mockito.never()).showCenters(
            centerPage.pageItems
        )
        Mockito.verify(mCenterListMvpView).showProgressbar(false)
    }

    @Test
    fun testLoadMoreCenters() {
        stubDataManagerGetCenters(Observable.just(centerPage))
        centerListPresenter.loadCenters(true, offset)
        Mockito.verify(mCenterListMvpView).showProgressbar(true)
        Mockito.verify(mCenterListMvpView).showMoreCenters(centerPage.pageItems)
        Mockito.verify(mCenterListMvpView, Mockito.never())
            .showMessage(R.string.failed_to_fetch_centers)
        Mockito.verify(mCenterListMvpView).showProgressbar(false)
    }

    @Test
    fun testLoadMoreCentersFails() {
        stubDataManagerGetCenters(Observable.error(RuntimeException()))
        centerListPresenter.loadCenters(true, offset)
        Mockito.verify(mCenterListMvpView).showProgressbar(true)
        Mockito.verify(mCenterListMvpView).showMessage(R.string.failed_to_fetch_centers)
        Mockito.verify(mCenterListMvpView, Mockito.never()).showFetchingError()
        Mockito.verify(mCenterListMvpView).showProgressbar(false)
    }

    @Test
    fun testEmptyCenters() {
        stubDataManagerGetCenters(Observable.just(Page<Center>()))
        centerListPresenter.loadCenters(false, offset)
        Mockito.verify(mCenterListMvpView).showProgressbar(true)
        Mockito.verify(mCenterListMvpView).showEmptyCenters(R.string.center)
        Mockito.verify(mCenterListMvpView, Mockito.never()).showFetchingError()
        Mockito.verify(mCenterListMvpView).showProgressbar(false)
    }

    @Test
    fun testNoMoreGroupsAvailable() {
        stubDataManagerGetCenters(Observable.just(Page<Center>()))
        centerListPresenter.loadCenters(true, offset)
        Mockito.verify(mCenterListMvpView).showProgressbar(true)
        Mockito.verify(mCenterListMvpView).showMessage(R.string.no_more_centers_available)
        Mockito.verify(mCenterListMvpView, Mockito.never()).showFetchingError()
        Mockito.verify(mCenterListMvpView).showProgressbar(false)
    }

    @Test
    fun testLoadCentersGroupAndMeeting() {
        Mockito.`when`<Observable<CenterWithAssociations>>(
            mDataManagerCenter.getCentersGroupAndMeeting(
                centerId
            )
        )
            .thenReturn(Observable.just(centerWithAssociations))
        centerListPresenter.loadCentersGroupAndMeeting(centerId)
        Mockito.verify(mCenterListMvpView)
            .showCentersGroupAndMeeting(centerWithAssociations, centerId)
        Mockito.verify(mCenterListMvpView, Mockito.never())
            .showMessage(R.string.failed_to_fetch_Group_and_meeting)
    }

    @Test
    fun testLoadCenterGroupAndMeetingFails() {
        Mockito.`when`(mDataManagerCenter.getCentersGroupAndMeeting(centerId))
            .thenReturn(Observable.error(RuntimeException()))
        centerListPresenter.loadCentersGroupAndMeeting(centerId)
        Mockito.verify(mCenterListMvpView).showMessage(R.string.failed_to_fetch_Group_and_meeting)
        Mockito.verify(mCenterListMvpView, Mockito.never())
            .showCentersGroupAndMeeting(centerWithAssociations, centerId)
    }

    private fun stubDataManagerGetCenters(observable: Observable<Page<Center>>) {
        Mockito.`when`(mDataManagerCenter.getCenters(true, offset, limit)).thenReturn(observable)
    }
}