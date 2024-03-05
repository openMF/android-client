package com.mifos.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mifos.core.objects.client.Charges
import com.mifos.core.objects.client.Page
import com.mifos.mifosxdroid.online.clientcharge.ClientChargeRepository
import com.mifos.mifosxdroid.online.clientcharge.ClientChargeUiState
import com.mifos.mifosxdroid.online.clientcharge.ClientChargeViewModel
import com.mifos.mifosxdroid.util.RxSchedulersOverrideRule
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import rx.Observable

/**
 * Created by Aditya Gupta on 02/09/23.
 */
@RunWith(MockitoJUnitRunner::class)
class ClientChargeViewModelTest {

    @JvmField
    @Rule
    val overrideSchedulersRule = RxSchedulersOverrideRule()

    @JvmField
    @Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var clientChargeRepository: ClientChargeRepository

    @Mock
    private lateinit var clientUiStateObserver: Observer<ClientChargeUiState>

    private lateinit var clientChargeViewModel: ClientChargeViewModel

    @Mock
    lateinit var mockChargesPage: Page<Charges>


    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        clientChargeViewModel = ClientChargeViewModel(clientChargeRepository)
        clientChargeViewModel.clientChargeUiState.observeForever(clientUiStateObserver)
    }

    @Test
    fun testLoadChargesEmptyList_SuccessfulChargesReceivedFromRepository_ReturnsSuccess() {

        Mockito.`when`(
            clientChargeRepository.getClientCharges(
                Mockito.anyInt(),
                Mockito.anyInt(),
                Mockito.anyInt()
            )
        ).thenReturn(Observable.just(mockChargesPage))
        clientChargeViewModel.loadCharges(1, 1, 1)
        Mockito.verify(clientUiStateObserver).onChanged(ClientChargeUiState.ShowProgressbar)
        Mockito.verify(clientUiStateObserver, Mockito.never())
            .onChanged(ClientChargeUiState.ShowFetchingErrorCharges("some error message"))
        Mockito.verify(clientUiStateObserver, Mockito.never())
            .onChanged(ClientChargeUiState.ShowChargesList(mockChargesPage))
        Mockito.verify(clientUiStateObserver).onChanged(ClientChargeUiState.ShowEmptyCharges)
        Mockito.verifyNoMoreInteractions(clientUiStateObserver)
    }

    @Test
    fun testLoadChargesList_SuccessfulChargesReceivedFromRepository_ReturnsSuccess() {

        val list1 = mock(Charges::class.java)
        val list2 = mock(Charges::class.java)
        val list = listOf(list1, list2)
        val mockPage = Page(2, list)

        Mockito.`when`(
            clientChargeRepository.getClientCharges(
                Mockito.anyInt(),
                Mockito.anyInt(),
                Mockito.anyInt()
            )
        ).thenReturn(Observable.just(mockPage))
        clientChargeViewModel.loadCharges(1, 1, 1)
        Mockito.verify(clientUiStateObserver).onChanged(ClientChargeUiState.ShowProgressbar)
        Mockito.verify(clientUiStateObserver, Mockito.never())
            .onChanged(ClientChargeUiState.ShowFetchingErrorCharges("some error message"))
        Mockito.verify(clientUiStateObserver)
            .onChanged(ClientChargeUiState.ShowChargesList(mockPage))
        Mockito.verify(clientUiStateObserver, Mockito.never())
            .onChanged(ClientChargeUiState.ShowEmptyCharges)
        Mockito.verifyNoMoreInteractions(clientUiStateObserver)
    }

    @Test
    fun testLoadCharges_UnsuccessfulChargesReceivedFromRepository_ReturnsError() {

        Mockito.`when`(
            clientChargeRepository.getClientCharges(
                Mockito.anyInt(),
                Mockito.anyInt(),
                Mockito.anyInt()
            )
        ).thenReturn(Observable.error(RuntimeException("some error message")))
        clientChargeViewModel.loadCharges(1, 1, 1)
        Mockito.verify(clientUiStateObserver).onChanged(ClientChargeUiState.ShowProgressbar)
        Mockito.verify(clientUiStateObserver, Mockito.never())
            .onChanged(ClientChargeUiState.ShowChargesList(mockChargesPage))
        Mockito.verify(clientUiStateObserver, Mockito.never())
            .onChanged(ClientChargeUiState.ShowEmptyCharges)
        Mockito.verifyNoMoreInteractions(clientUiStateObserver)
    }

    @After
    fun tearDown() {
        clientChargeViewModel.clientChargeUiState.removeObserver(clientUiStateObserver)
    }
}