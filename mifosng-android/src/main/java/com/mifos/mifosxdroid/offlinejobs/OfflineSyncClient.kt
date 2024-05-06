package com.mifos.mifosxdroid.offlinejobs

import com.evernote.android.job.Job
import com.evernote.android.job.JobRequest
import com.mifos.core.network.datamanager.DataManagerClient
import com.mifos.core.objects.client.Client
import com.mifos.core.objects.client.ClientPayload
import com.mifos.utils.MFErrorParser.errorMessage
import com.mifos.utils.PrefManager.userStatus
import com.mifos.utils.Tags
import rx.Observer
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by aksh on 22/7/18.
 */
class OfflineSyncClient : Job() {
    @Inject
    lateinit var mDataManagerClient: DataManagerClient
    var clientPayloads: MutableList<ClientPayload> = ArrayList()
    private var mSubscriptions: CompositeSubscription? = null
    private var mClientSyncIndex = 0
    override fun onRunJob(params: Params): Result {
        mSubscriptions = CompositeSubscription()
        return if (!userStatus) {
            loadDatabaseClientPayload()
            Result.SUCCESS
        } else {
            Result.FAILURE
        }
    }

    private fun loadDatabaseClientPayload() {
        mSubscriptions?.add(
            mDataManagerClient.allDatabaseClientPayload
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<List<ClientPayload>>() {
                    override fun onCompleted() {
                        mClientSyncIndex = 0
                        syncClient()
                    }

                    override fun onError(e: Throwable) {}
                    override fun onNext(clientPayloads: List<ClientPayload>) {
                        showPayloads(clientPayloads)
                    }
                })
        )
    }

    fun showPayloads(clientPayload: List<ClientPayload>) {
        clientPayloads = clientPayload as MutableList<ClientPayload>
    }

    fun syncClient() {
        for (i in mClientSyncIndex until clientPayloads.size) {
            if (clientPayloads[i].errorMessage == null) {
                syncClientPayload(clientPayloads[i])
                mClientSyncIndex = i
                break
            }
        }
    }

    private fun syncClientPayload(clientPayload: ClientPayload?) {
        mSubscriptions?.add(
            mDataManagerClient.createClient(clientPayload!!)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Observer<Client> {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        showClientSyncFailed(errorMessage(e))
                    }

                    override fun onNext(client: Client) {
                        showSyncResponse()
                    }
                })
        )
    }

    fun showClientSyncFailed(errorMessage: String?) {
        val clientPayload = clientPayloads[mClientSyncIndex]
        clientPayload.errorMessage = errorMessage
        updateClientPayload(clientPayload)
    }

    fun showSyncResponse() {
        clientPayloads[mClientSyncIndex].id?.let {
            clientPayloads[mClientSyncIndex].clientCreationTime?.let { it1 ->
                deleteAndUpdateClientPayload(
                    it,
                    it1
                )
            }
        }
    }

    private fun deleteAndUpdateClientPayload(id: Int, clientCreationTIme: Long) {
        mSubscriptions?.add(
            mDataManagerClient.deleteAndUpdatePayloads(
                id,
                clientCreationTIme
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Observer<List<ClientPayload>> {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {}
                    override fun onNext(clientPayloads: List<ClientPayload>) {
                        showPayloadDeletedAndUpdatePayloads(clientPayloads)
                    }
                })
        )
    }

    private fun updateClientPayload(clientPayload: ClientPayload?) {
        mSubscriptions?.add(
            mDataManagerClient.updateClientPayload(clientPayload!!)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<ClientPayload>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {}
                    override fun onNext(clientPayload: ClientPayload) {
                        showClientPayloadUpdated(clientPayload)
                    }
                })
        )
    }

    fun showPayloadDeletedAndUpdatePayloads(clients: List<ClientPayload>) {
        mClientSyncIndex = 0
        clientPayloads.clear()
        clientPayloads = clients as MutableList<ClientPayload>
        if (clientPayloads.size != 0) {
            syncClient()
        }
    }

    fun showClientPayloadUpdated(clientPayload: ClientPayload) {
        clientPayloads[mClientSyncIndex] = clientPayload
        mClientSyncIndex += 1
        if (clientPayloads.size != mClientSyncIndex) {
            syncClient()
        }
    }

    companion object {
        fun schedulePeriodic() {
            JobRequest.Builder(Tags.OfflineSyncClient)
                .setPeriodic(
                    TimeUnit.MINUTES.toMillis(15),
                    TimeUnit.MINUTES.toMillis(5)
                )
                .build()
                .schedule()
        }
    }
}