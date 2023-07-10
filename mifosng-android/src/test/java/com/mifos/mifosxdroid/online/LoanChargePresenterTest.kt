package com.mifos.mifosxdroid.online

import com.mifos.api.DataManager
import com.mifos.mifosxdroid.FakeRemoteDataSource.loanCharges
import com.mifos.mifosxdroid.online.loancharge.LoanChargeMvpView
import com.mifos.mifosxdroid.online.loancharge.LoanChargePresenter
import com.mifos.mifosxdroid.util.RxSchedulersOverrideRule
import com.mifos.objects.client.Charges
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
class LoanChargePresenterTest {
    @get:Rule
    val mOverrideSchedulersRule = RxSchedulersOverrideRule()
    lateinit var mLoanChargePresenter: LoanChargePresenter

    @Mock
    lateinit var mDataManager: DataManager

    @Mock
    lateinit var mLoanChargeMvpView: LoanChargeMvpView
    private lateinit var chargesList: List<Charges>
    var loanId = 441

    @Before
    @Throws(Exception::class)
    fun setUp() {
        mLoanChargePresenter = LoanChargePresenter(mDataManager)
        mLoanChargePresenter.attachView(mLoanChargeMvpView)
        chargesList = loanCharges
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        mLoanChargePresenter.detachView()
    }

    @Test
    @Throws(Exception::class)
    fun testLoadLoanChargesList() {
        Mockito.`when`(
            mDataManager.getListOfLoanCharges(loanId)
        )
            .thenReturn(Observable.just(chargesList))
        mLoanChargePresenter.loadLoanChargesList(loanId)
        Mockito.verify(mLoanChargeMvpView).showLoanChargesList(chargesList as MutableList<Charges>)
        Mockito.verify(mLoanChargeMvpView, Mockito.never())
            .showFetchingError("Failed to load Charges")
    }

    @Test
    fun testLoadChargesFails() {
        Mockito.`when`(
            mDataManager.getListOfLoanCharges(loanId)
        )
            .thenReturn(Observable.error(RuntimeException()))
        mLoanChargePresenter.loadLoanChargesList(loanId)
        Mockito.verify(mLoanChargeMvpView, Mockito.never())
            .showLoanChargesList(chargesList as MutableList<Charges>)
    }
}