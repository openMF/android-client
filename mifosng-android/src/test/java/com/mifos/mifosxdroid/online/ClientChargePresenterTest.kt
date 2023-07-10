package com.mifos.mifosxdroid.online

import com.mifos.api.datamanager.DataManagerCharge
import com.mifos.mifosxdroid.FakeRemoteDataSource.clientCharges
import com.mifos.mifosxdroid.online.clientcharge.ClientChargeMvpView
import com.mifos.mifosxdroid.online.clientcharge.ClientChargePresenter
import com.mifos.mifosxdroid.util.RxSchedulersOverrideRule
import com.mifos.objects.client.Charges
import com.mifos.objects.client.Page
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
 * Created by Rajan Maurya on 20/06/16.
 */
@RunWith(MockitoJUnitRunner::class)
class ClientChargePresenterTest {
    @get:Rule
    val mOverrideSchedulersRule = RxSchedulersOverrideRule()
    private lateinit var mClientChargePresenter: ClientChargePresenter

    @Mock
    lateinit var mDataManagerCharge: DataManagerCharge

    @Mock
    lateinit var mClientChargeMvpView: ClientChargeMvpView
    private lateinit var chargesPage: Page<Charges>
    var clientId = 1
    var offset = 0
    var limit = 10

    @Before
    fun setUp() {
        mClientChargePresenter = ClientChargePresenter(mDataManagerCharge)
        mClientChargePresenter.attachView(mClientChargeMvpView)
        chargesPage = clientCharges
    }

    @After
    fun tearDown() {
        mClientChargePresenter.detachView()
    }

    @Test
    fun testLoadCharges() {
        Mockito.`when`<Observable<Page<Charges>>>(
            mDataManagerCharge.getClientCharges(clientId, offset, limit)
        )
            .thenReturn(Observable.just(chargesPage))
        mClientChargePresenter.loadCharges(clientId, offset, limit)
        Mockito.verify(mClientChargeMvpView).showChargesList(chargesPage)
    }

    @Test
    fun testLoadChargesFails() {
        Mockito.`when`(
            mDataManagerCharge.getClientCharges(clientId, offset, limit)
        )
            .thenReturn(Observable.error(RuntimeException()))
        mClientChargePresenter.loadCharges(clientId, offset, limit)
        Mockito.verify(mClientChargeMvpView, Mockito.never()).showChargesList(
            chargesPage
        )
    }
}