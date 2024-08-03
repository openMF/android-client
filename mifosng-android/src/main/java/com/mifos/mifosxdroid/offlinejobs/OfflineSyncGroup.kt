package com.mifos.mifosxdroid.offlinejobs

import com.evernote.android.job.Job
import com.evernote.android.job.JobRequest
import com.mifos.core.network.datamanager.DataManagerGroups
import com.mifos.core.objects.group.GroupPayload
import com.mifos.core.objects.response.SaveResponse
import com.mifos.utils.PrefManager.userStatus
import com.mifos.utils.Tags
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by aksh on 22/7/18.
 */
class OfflineSyncGroup : Job() {
    var mSubscriptions: CompositeSubscription? = null
    var groupPayloads: MutableList<GroupPayload> = ArrayList()

    @Inject
    lateinit var mDataManagerGroups: DataManagerGroups
    private var mClientSyncIndex = 0
    override fun onRunJob(params: Params): Result {
        mSubscriptions = CompositeSubscription()
        return if (!userStatus) {
            loadDatabaseGroupPayload()
            Result.SUCCESS
        } else {
            Result.FAILURE
        }
    }

    private fun loadDatabaseGroupPayload() {
        mSubscriptions?.add(
            mDataManagerGroups.allDatabaseGroupPayload
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<List<GroupPayload>>() {
                    override fun onCompleted() {
                        mClientSyncIndex = 0
                        startSync()
                    }

                    override fun onError(e: Throwable) {}
                    override fun onNext(groupPayloads: List<GroupPayload>) {
                        showGroups(groupPayloads)
                    }
                })
        )
    }

    fun showGroups(groupPayload: List<GroupPayload>) {
        groupPayloads = groupPayload as MutableList<GroupPayload>
    }

    fun startSync() {
        for (i in mClientSyncIndex until groupPayloads.size) {
            if (groupPayloads[i].errorMessage == null) {
                syncGroupPayload(groupPayloads[i])
                mClientSyncIndex = i
                break
            }
        }
    }

    private fun syncGroupPayload(groupPayload: GroupPayload?) {
        mSubscriptions?.add(
            mDataManagerGroups.createGroup(groupPayload!!)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<SaveResponse>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        showGroupSyncFailed(e.message.toString())
                    }

                    override fun onNext(group: SaveResponse) {
                        showGroupSyncResponse()
                    }
                })
        )
    }

    fun showGroupSyncFailed(errorMessage: String?) {
        val groupPayload = groupPayloads[mClientSyncIndex]
        groupPayload.errorMessage = errorMessage
        updateGroupPayload(groupPayload)
    }

    fun showGroupSyncResponse() {
        deleteAndUpdateGroupPayload(
            groupPayloads[mClientSyncIndex].id
        )
    }

    private fun updateGroupPayload(groupPayload: GroupPayload?) {
        mSubscriptions?.add(
            mDataManagerGroups.updateGroupPayload(groupPayload!!)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<GroupPayload>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {}
                    override fun onNext(groupPayload: GroupPayload) {
                        showGroupPayloadUpdated(groupPayload)
                    }
                })
        )
    }

    private fun deleteAndUpdateGroupPayload(id: Int) {
        mSubscriptions?.add(
            mDataManagerGroups.deleteAndUpdateGroupPayloads(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<List<GroupPayload>>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {}
                    override fun onNext(groupPayloads: List<GroupPayload>) {
                        showPayloadDeletedAndUpdatePayloads(groupPayloads)
                    }
                })
        )
    }

    fun showGroupPayloadUpdated(groupPayload: GroupPayload) {
        groupPayloads[mClientSyncIndex] = groupPayload
        mClientSyncIndex += 1
        if (groupPayloads.size != mClientSyncIndex) {
            startSync()
        }
    }

    fun showPayloadDeletedAndUpdatePayloads(groups: List<GroupPayload>) {
        mClientSyncIndex = 0
        groupPayloads = groups as MutableList<GroupPayload>
        if (groupPayloads.size != 0) {
            startSync()
        }
    }

    companion object {
        fun schedulePeriodic() {
            JobRequest.Builder(Tags.OfflineSyncGroup)
                .setPeriodic(TimeUnit.MINUTES.toMillis(15), TimeUnit.MINUTES.toMillis(5))
                .setUpdateCurrent(true)
                .build()
                .schedule()
        }
    }
}