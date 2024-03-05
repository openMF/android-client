package com.mifos.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mifos.core.objects.client.Client
import com.mifos.core.objects.client.ClientPayload
import com.mifos.mifosxdroid.offline.syncclientpayloads.SyncClientPayloadsRepository
import com.mifos.mifosxdroid.offline.syncclientpayloads.SyncClientPayloadsUiState
import com.mifos.mifosxdroid.offline.syncclientpayloads.SyncClientPayloadsViewModel
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
class SyncClientPayloadsViewModelTest {

    @get:Rule
    val overrideSchedulersRule = RxSchedulersOverrideRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var syncClientPayloadsRepository: SyncClientPayloadsRepository

    private lateinit var syncClientPayloadsViewModel: SyncClientPayloadsViewModel

    @Mock
    private lateinit var syncClientPayloadsUiStateObserver: Observer<SyncClientPayloadsUiState>

    @Mock
    private lateinit var mockClientPayload: List<ClientPayload>


    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        syncClientPayloadsViewModel = SyncClientPayloadsViewModel(syncClientPayloadsRepository)
        syncClientPayloadsViewModel.syncClientPayloadsUiState.observeForever(
            syncClientPayloadsUiStateObserver
        )
    }

    @Test
    fun testSyncClientPayloads_SuccessfulSyncClientPayloadReceivedFromRepository_ReturnsSyncPayload() {
        Mockito.`when`(
            syncClientPayloadsRepository.allDatabaseClientPayload()
        ).thenReturn(Observable.just(mockClientPayload))
        syncClientPayloadsViewModel.loadDatabaseClientPayload()
        Mockito.verify(syncClientPayloadsUiStateObserver)
            .onChanged(SyncClientPayloadsUiState.ShowProgressbar)
        Mockito.verify(syncClientPayloadsUiStateObserver, Mockito.never())
            .onChanged(SyncClientPayloadsUiState.ShowError("some error message"))
        Mockito.verify(syncClientPayloadsUiStateObserver)
            .onChanged(SyncClientPayloadsUiState.ShowPayloads(mockClientPayload))
        Mockito.verifyNoMoreInteractions(syncClientPayloadsUiStateObserver)
    }

    @Test
    fun testSyncClientPayloads_UnsuccessfulSyncClientPayloadReceivedFromRepository_ReturnsError() {
        Mockito.`when`(
            syncClientPayloadsRepository.allDatabaseClientPayload()
        ).thenReturn(Observable.error(RuntimeException("some error message")))
        syncClientPayloadsViewModel.loadDatabaseClientPayload()
        Mockito.verify(syncClientPayloadsUiStateObserver)
            .onChanged(SyncClientPayloadsUiState.ShowProgressbar)
        Mockito.verify(syncClientPayloadsUiStateObserver)
            .onChanged(SyncClientPayloadsUiState.ShowError("some error message"))
        Mockito.verify(syncClientPayloadsUiStateObserver, Mockito.never())
            .onChanged(SyncClientPayloadsUiState.ShowPayloads(mockClientPayload))
        Mockito.verifyNoMoreInteractions(syncClientPayloadsUiStateObserver)
    }

    @Test
    fun testCreateClientPayloads_UnsuccessfulCreateClientPayloadReceivedFromRepository_ReturnsCreateClient() {
        Mockito.`when`(
            syncClientPayloadsRepository.createClient(ClientPayload())
        ).thenReturn(Observable.just(Client()))
        syncClientPayloadsViewModel.syncClientPayload(ClientPayload())
        Mockito.verify(syncClientPayloadsUiStateObserver)
            .onChanged(SyncClientPayloadsUiState.ShowProgressbar)
        Mockito.verify(syncClientPayloadsUiStateObserver, Mockito.never())
            .onChanged(SyncClientPayloadsUiState.ShowError("some error message"))
        Mockito.verify(syncClientPayloadsUiStateObserver)
            .onChanged(SyncClientPayloadsUiState.ShowSyncResponse)
        Mockito.verifyNoMoreInteractions(syncClientPayloadsUiStateObserver)
    }

    @Test
    fun testCreateClientPayloads_UnsuccessfulCreateClientPayloadReceivedFromRepository_ReturnsError() {
        Mockito.`when`(
            syncClientPayloadsRepository.createClient(ClientPayload())
        ).thenReturn(Observable.error(RuntimeException("some error message")))
        syncClientPayloadsViewModel.syncClientPayload(ClientPayload())
        Mockito.verify(syncClientPayloadsUiStateObserver)
            .onChanged(SyncClientPayloadsUiState.ShowProgressbar)
        Mockito.verify(syncClientPayloadsUiStateObserver)
            .onChanged(SyncClientPayloadsUiState.ShowError("some error message"))
        Mockito.verify(syncClientPayloadsUiStateObserver, Mockito.never())
            .onChanged(SyncClientPayloadsUiState.ShowSyncResponse)
        Mockito.verifyNoMoreInteractions(syncClientPayloadsUiStateObserver)
    }

    @Test
    fun testDeleteAndUpdateClientPayloads_UnsuccessfulDeleteAndUpdateClientPayloadsReceivedFromRepository_ReturnsClientPayload() {
        Mockito.`when`(
            syncClientPayloadsRepository.deleteAndUpdatePayloads(
                Mockito.anyInt(),
                Mockito.anyLong()
            )
        ).thenReturn(Observable.just(mockClientPayload))
        syncClientPayloadsViewModel.deleteAndUpdateClientPayload(1, 1)
        Mockito.verify(syncClientPayloadsUiStateObserver)
            .onChanged(SyncClientPayloadsUiState.ShowProgressbar)
        Mockito.verify(syncClientPayloadsUiStateObserver, Mockito.never())
            .onChanged(SyncClientPayloadsUiState.ShowError("some error message"))
        Mockito.verify(syncClientPayloadsUiStateObserver)
            .onChanged(
                SyncClientPayloadsUiState.ShowPayloadDeletedAndUpdatePayloads(
                    mockClientPayload
                )
            )
        Mockito.verifyNoMoreInteractions(syncClientPayloadsUiStateObserver)
    }

    @Test
    fun testDeleteAndUpdateClientPayloads_UnsuccessfulDeleteAndUpdateClientPayloadsReceivedFromRepository_ReturnsError() {
        Mockito.`when`(
            syncClientPayloadsRepository.deleteAndUpdatePayloads(
                Mockito.anyInt(),
                Mockito.anyLong()
            )
        ).thenReturn(Observable.error(RuntimeException("some error message")))
        syncClientPayloadsViewModel.deleteAndUpdateClientPayload(1, 1)
        Mockito.verify(syncClientPayloadsUiStateObserver)
            .onChanged(SyncClientPayloadsUiState.ShowProgressbar)
        Mockito.verify(syncClientPayloadsUiStateObserver)
            .onChanged(SyncClientPayloadsUiState.ShowError("some error message"))
        Mockito.verify(syncClientPayloadsUiStateObserver, Mockito.never())
            .onChanged(
                SyncClientPayloadsUiState.ShowPayloadDeletedAndUpdatePayloads(
                    mockClientPayload
                )
            )
        Mockito.verifyNoMoreInteractions(syncClientPayloadsUiStateObserver)
    }

    @Test
    fun testUpdateClientPayloads_UnsuccessfulUpdateClientPayloadsReceivedFromRepository_ReturnsUpdateClientPayload() {
        Mockito.`when`(
            syncClientPayloadsRepository.updateClientPayload(ClientPayload())
        ).thenReturn(Observable.just(ClientPayload()))
        syncClientPayloadsViewModel.updateClientPayload(ClientPayload())
        Mockito.verify(syncClientPayloadsUiStateObserver)
            .onChanged(SyncClientPayloadsUiState.ShowProgressbar)
        Mockito.verify(syncClientPayloadsUiStateObserver, Mockito.never())
            .onChanged(SyncClientPayloadsUiState.ShowError("some error message"))
        Mockito.verify(syncClientPayloadsUiStateObserver)
            .onChanged(
                SyncClientPayloadsUiState.ShowClientPayloadUpdated(ClientPayload())
            )
        Mockito.verifyNoMoreInteractions(syncClientPayloadsUiStateObserver)
    }

    @Test
    fun testUpdateClientPayloads_UnsuccessfulUpdateClientPayloadsReceivedFromRepository_ReturnsError() {
        Mockito.`when`(
            syncClientPayloadsRepository.updateClientPayload(ClientPayload())
        ).thenReturn(Observable.error(RuntimeException("some error message")))
        syncClientPayloadsViewModel.updateClientPayload(ClientPayload())
        Mockito.verify(syncClientPayloadsUiStateObserver)
            .onChanged(SyncClientPayloadsUiState.ShowProgressbar)
        Mockito.verify(syncClientPayloadsUiStateObserver)
            .onChanged(SyncClientPayloadsUiState.ShowError("some error message"))
        Mockito.verify(syncClientPayloadsUiStateObserver, Mockito.never())
            .onChanged(
                SyncClientPayloadsUiState.ShowClientPayloadUpdated(ClientPayload())
            )
        Mockito.verifyNoMoreInteractions(syncClientPayloadsUiStateObserver)
    }

    @After
    fun tearDown() {
        syncClientPayloadsViewModel.syncClientPayloadsUiState.removeObserver(
            syncClientPayloadsUiStateObserver
        )
    }
}