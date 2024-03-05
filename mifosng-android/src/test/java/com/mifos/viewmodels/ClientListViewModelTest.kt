package com.mifos.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mifos.core.objects.client.Client
import com.mifos.core.objects.client.Page
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.online.clientlist.ClientListRepository
import com.mifos.mifosxdroid.online.clientlist.ClientListUiState
import com.mifos.mifosxdroid.online.clientlist.ClientListViewModel
import com.mifos.mifosxdroid.util.RxSchedulersOverrideRule
import org.junit.After
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
 * Created by Aditya Gupta on 06/09/23.
 */
@RunWith(MockitoJUnitRunner::class)
class ClientListViewModelTest {

    @get:Rule
    val overrideSchedulersRule = RxSchedulersOverrideRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var clientListRepository: ClientListRepository

    @Mock
    private lateinit var clientListUiObserver: Observer<ClientListUiState>

    private lateinit var clientListViewModel: ClientListViewModel

    @Mock
    private lateinit var mockClientList: Page<Client>

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        clientListViewModel = ClientListViewModel(clientListRepository)
        clientListViewModel.clientListUiState.observeForever(clientListUiObserver)
    }

    @Test
    fun testClientList_SuccessfulClientListReceivedFromRepository_ReturnsClientList() {
        val list1 = Mockito.mock(Client::class.java)
        val list2 = Mockito.mock(Client::class.java)
        val list = listOf(list1, list2)
        val mockPage = Page(2, list)
        Mockito.`when`(
            clientListRepository.getAllClients(
                Mockito.anyInt(),
                Mockito.anyInt()
            )
        ).thenReturn(Observable.just(mockPage))
        clientListViewModel.loadClients(false, 1)
        Mockito.verify(clientListUiObserver).onChanged(ClientListUiState.ShowProgressbar(true))
        Mockito.verify(clientListUiObserver, Mockito.never())
            .onChanged(ClientListUiState.ShowEmptyClientList(R.string.client))
        Mockito.verify(clientListUiObserver, Mockito.never())
            .onChanged(ClientListUiState.UnregisterSwipeAndScrollListener)
        Mockito.verify(clientListUiObserver, Mockito.never()).onChanged(ClientListUiState.ShowError)
        Mockito.verify(clientListUiObserver, Mockito.never())
            .onChanged(ClientListUiState.ShowMessage(R.string.failed_to_load_client))
        Mockito.verifyNoMoreInteractions(clientListUiObserver)
    }

    @Test
    fun testClientList_SuccessfulClientListReceivedFromRepository_ReturnsEmptyClientList() {
        Mockito.`when`(
            clientListRepository.getAllClients(
                Mockito.anyInt(),
                Mockito.anyInt()
            )
        ).thenReturn(Observable.just(mockClientList))
        clientListViewModel.loadClients(false, 0)
        Mockito.verify(clientListUiObserver).onChanged(ClientListUiState.ShowProgressbar(true))
        Mockito.verify(clientListUiObserver)
            .onChanged(ClientListUiState.ShowEmptyClientList(R.string.client))
        Mockito.verify(clientListUiObserver)
            .onChanged(ClientListUiState.UnregisterSwipeAndScrollListener)
        Mockito.verify(clientListUiObserver, Mockito.never()).onChanged(ClientListUiState.ShowError)
        Mockito.verify(clientListUiObserver, Mockito.never())
            .onChanged(ClientListUiState.ShowMessage(R.string.failed_to_load_client))
        Mockito.verifyNoMoreInteractions(clientListUiObserver)
    }

    @Test
    fun testClientList_UnsuccessfulClientListReceivedFromRepository_ReturnsError() {
        Mockito.`when`(
            clientListRepository.getAllClients(
                Mockito.anyInt(),
                Mockito.anyInt()
            )
        ).thenReturn(
            Observable.error(RuntimeException("some error message"))
        )
        clientListViewModel.loadClients(false, 0)
        Mockito.verify(clientListUiObserver).onChanged(ClientListUiState.ShowProgressbar(true))
        Mockito.verify(clientListUiObserver, Mockito.never())
            .onChanged(ClientListUiState.ShowClientList(mockClientList.pageItems))
        Mockito.verify(clientListUiObserver).onChanged(ClientListUiState.ShowError)
        Mockito.verifyNoMoreInteractions(clientListUiObserver)
    }

    @Test
    fun testClientList_SuccessfulClientListReceivedFromRepository_ReturnsDbClientList() {
        Mockito.`when`(
            clientListRepository.allDatabaseClients()
        ).thenReturn(Observable.just(mockClientList))
        clientListViewModel.loadDatabaseClients()
        Mockito.verify(clientListUiObserver, Mockito.never())
            .onChanged(ClientListUiState.ShowMessage(R.string.failed_to_load_db_clients))
        Mockito.verifyNoMoreInteractions(clientListUiObserver)
    }

    @Test
    fun testClientListDatabase_UnsuccessfulClientListReceivedFromRepository_ReturnsError() {
        Mockito.`when`(clientListRepository.allDatabaseClients())
            .thenReturn(Observable.error(RuntimeException("some error message")))
        clientListViewModel.loadDatabaseClients()
        Mockito.verify(clientListUiObserver)
            .onChanged(ClientListUiState.ShowMessage(R.string.failed_to_load_db_clients))
        Mockito.verifyNoMoreInteractions(clientListUiObserver)
    }

    @After
    fun tearDown() {
        clientListViewModel.clientListUiState.removeObserver(clientListUiObserver)
    }
}