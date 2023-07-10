package com.mifos.mifosxdroid.offline.syncclientpayloads

import com.mifos.api.datamanager.DataManagerClient
import com.mifos.mifosxdroid.FakeRemoteDataSource
import com.mifos.mifosxdroid.FakeRemoteDataSource.failureServerResponse
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.util.RxSchedulersOverrideRule
import com.mifos.objects.client.Client
import com.mifos.objects.client.ClientPayload
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
 * Created by rajan on 11/07/16.
 */
@RunWith(MockitoJUnitRunner::class)
class SyncClientPayloadsPresenterTest {
    @get:Rule
    val mOverrideSchedulersRule = RxSchedulersOverrideRule()
    private lateinit var syncClientPayloadsPresenter: SyncClientPayloadsPresenter

    @Mock
    lateinit var mDataManagerClient: DataManagerClient

    @Mock
    lateinit var mSyncClientPayloadsMvpView: SyncClientPayloadsMvpView
    private lateinit var clientPayloads: List<ClientPayload>
    private lateinit var errorSyncServerMessage: String
    var clientCreationTime: Long = 0

    @Before
    fun setUp() {
        syncClientPayloadsPresenter = SyncClientPayloadsPresenter(mDataManagerClient)
        syncClientPayloadsPresenter.attachView(mSyncClientPayloadsMvpView)
        clientPayloads = FakeRemoteDataSource.clientPayloads
        errorSyncServerMessage = failureServerResponse.errors[0].defaultUserMessage
        clientCreationTime = System.currentTimeMillis()
    }

    @After
    fun tearDown() {
        syncClientPayloadsPresenter.detachView()
    }

    @Test
    fun testLoadDatabaseClientPayload() {
        Mockito.`when`(mDataManagerClient.allDatabaseClientPayload)
            .thenReturn(
                Observable.just(clientPayloads)
            )
        syncClientPayloadsPresenter.loadDatabaseClientPayload()
        Mockito.verify(mSyncClientPayloadsMvpView)?.showPayloads(
            clientPayloads
        )
        Mockito.verify(mSyncClientPayloadsMvpView, Mockito.never())
            ?.showError(R.string.failed_to_load_clientpayload)
    }

    @Test
    fun testLoadDatabaseClientPayloadFails() {
        Mockito.`when`(mDataManagerClient.allDatabaseClientPayload)
            .thenReturn(Observable.error(RuntimeException()))
        syncClientPayloadsPresenter.loadDatabaseClientPayload()
        Mockito.verify(mSyncClientPayloadsMvpView)?.showError(R.string.failed_to_load_clientpayload)
        Mockito.verify(mSyncClientPayloadsMvpView, Mockito.never())?.showPayloads(
            clientPayloads
        )
    }

    @Test
    fun testSyncClientPayload() {
        //Syncing the first Client Payload of FakeDataSource
        Mockito.`when`(
            mDataManagerClient.createClient(
                clientPayloads[0]
            )
        ).thenReturn(Observable.just(Client()))
        syncClientPayloadsPresenter.syncClientPayload(clientPayloads[0])
        Mockito.verify(mSyncClientPayloadsMvpView)?.showSyncResponse()
        Mockito.verify(mSyncClientPayloadsMvpView, Mockito.never())
            ?.showClientSyncFailed(errorSyncServerMessage)
    }

    @Test
    fun testSyncClientPayloadFails() {
        Mockito.`when`(
            mDataManagerClient.createClient(
                clientPayloads[0]
            )
        )
            .thenReturn(Observable.error(RuntimeException()))
        syncClientPayloadsPresenter.syncClientPayload(clientPayloads[0])

        //TODO Fix this test showClientSyncFailed, It is failing because this is calling a
        //TODO Schedulers in Fragment View Class.
        //verify(mSyncClientPayloadsMvpView).showClientSyncFailed(errorSyncServerMessage);
        Mockito.verify(mSyncClientPayloadsMvpView, Mockito.never())?.showSyncResponse()
    }

    @Test
    @Throws(Exception::class)
    fun testDeleteAndUpdateClientPayload() {
        //Deleting the Client Payload of Id = 1, Id is transient. it will not serialize and
        // deserialize during retrofit request. It is for just numbering the payload in Table.
        Mockito.`when`<Observable<List<ClientPayload>>>(
            mDataManagerClient.deleteAndUpdatePayloads(
                1,
                clientCreationTime
            )
        )
            .thenReturn(Observable.just(clientPayloads))
        syncClientPayloadsPresenter.deleteAndUpdateClientPayload(1, clientCreationTime)
        Mockito.verify(mSyncClientPayloadsMvpView)?.showPayloadDeletedAndUpdatePayloads(
            clientPayloads
        )
        Mockito.verify(mSyncClientPayloadsMvpView, Mockito.never())
            ?.showError(R.string.failed_to_update_list)
    }

    @Test
    fun testDeleteAndUpdateClientPayloadFails() {
        Mockito.`when`(mDataManagerClient.deleteAndUpdatePayloads(1, clientCreationTime))
            .thenReturn(Observable.error(RuntimeException()))
        syncClientPayloadsPresenter.deleteAndUpdateClientPayload(1, clientCreationTime)
        Mockito.verify(mSyncClientPayloadsMvpView)?.showError(R.string.failed_to_update_list)
        Mockito.verify(mSyncClientPayloadsMvpView, Mockito.never())
            ?.showPayloadDeletedAndUpdatePayloads(clientPayloads)
    }
}