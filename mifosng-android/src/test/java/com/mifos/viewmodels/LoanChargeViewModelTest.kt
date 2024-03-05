package com.mifos.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mifos.core.objects.client.Charges
import com.mifos.mifosxdroid.online.loancharge.LoanChargeRepository
import com.mifos.mifosxdroid.online.loancharge.LoanChargeUiState
import com.mifos.mifosxdroid.online.loancharge.LoanChargeViewModel
import com.mifos.mifosxdroid.util.RxSchedulersOverrideRule
import org.junit.After
import org.junit.Assert.*
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
 * Created by Aditya Gupta on 02/09/23.
 */
@RunWith(MockitoJUnitRunner::class)
class LoanChargeViewModelTest {


    @get:Rule
    val overrideSchedulersRule = RxSchedulersOverrideRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var loanChargeRepository: LoanChargeRepository

    @Mock
    private lateinit var loanChargeViewModel: LoanChargeViewModel

    @Mock
    private lateinit var loanChargeUiStateObserver: Observer<LoanChargeUiState>

    @Mock
    private lateinit var mockChargesPage: List<Charges>

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        loanChargeViewModel = LoanChargeViewModel(loanChargeRepository)
        loanChargeViewModel.loanChargeUiState.observeForever(loanChargeUiStateObserver)
    }

    @Test
    fun testLoadLoanChargesList_SuccessfulLoanChargesReceivedFromRepository_ReturnsSuccess() {

        Mockito.`when`(loanChargeRepository.getListOfLoanCharges(Mockito.anyInt())).thenReturn(
            Observable.just(mockChargesPage)
        )
        loanChargeViewModel.loadLoanChargesList(441)
        Mockito.verify(loanChargeUiStateObserver).onChanged(LoanChargeUiState.ShowProgressbar)
        Mockito.verify(loanChargeUiStateObserver, Mockito.never())
            .onChanged(LoanChargeUiState.ShowFetchingError("some error message"))
        Mockito.verify(loanChargeUiStateObserver)
            .onChanged(LoanChargeUiState.ShowLoanChargesList(mockChargesPage as MutableList<Charges>))
        Mockito.verifyNoMoreInteractions(loanChargeUiStateObserver)
    }

    @Test
    fun testLoadLoanChargesList_UnSuccessfulLoanChargesReceivedFromRepository_ReturnsError() {

        Mockito.`when`(loanChargeRepository.getListOfLoanCharges(Mockito.anyInt()))
            .thenReturn(Observable.error(RuntimeException("some error message")))
        loanChargeViewModel.loadLoanChargesList(441)
        Mockito.verify(loanChargeUiStateObserver).onChanged(LoanChargeUiState.ShowProgressbar)
        Mockito.verify(loanChargeUiStateObserver, Mockito.never())
            .onChanged(LoanChargeUiState.ShowLoanChargesList(mockChargesPage as MutableList<Charges>))
        Mockito.verifyNoMoreInteractions(loanChargeUiStateObserver)
    }

    @After
    fun tearDown() {
        loanChargeViewModel.loanChargeUiState.removeObserver(loanChargeUiStateObserver)
    }
}