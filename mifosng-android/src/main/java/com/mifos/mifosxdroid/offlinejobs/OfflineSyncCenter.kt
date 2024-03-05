package com.mifos.mifosxdroid.offlinejobs

import com.evernote.android.job.Job
import com.evernote.android.job.JobRequest
import com.mifos.core.data.CenterPayload
import com.mifos.core.network.datamanager.DataManagerCenter
import com.mifos.core.objects.response.SaveResponse
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
 * Created by aksh on 17/7/18.
 */
class OfflineSyncCenter : Job() {
    @JvmField
    @Inject
    var mDataManagerCenter: DataManagerCenter? = null
    var centerPayloads: MutableList<CenterPayload>? = null
    private var mSubscriptions: CompositeSubscription? = null
    private var mCenterSyncIndex = 0
    override fun onRunJob(params: Params): Result {
        mSubscriptions = CompositeSubscription()
        centerPayloads = ArrayList()
        return if (!userStatus) {
            loadDatabaseCenterPayload()
            Result.SUCCESS
        } else {
            Result.FAILURE
        }
    }

    private fun loadDatabaseCenterPayload() {
        mSubscriptions!!.add(
            mDataManagerCenter!!.allDatabaseCenterPayload
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<List<CenterPayload>>() {
                    override fun onCompleted() {
                        mCenterSyncIndex = 0
                        startSync()
                    }

                    override fun onError(e: Throwable) {}
                    override fun onNext(centerPayloads: List<CenterPayload>) {
                        showCenters(centerPayloads)
                    }
                })
        )
    }

    fun showCenters(centerPayload: List<CenterPayload>) {
        centerPayloads = centerPayload.toMutableList()
    }

    fun startSync() {
        for (i in mCenterSyncIndex until centerPayloads!!.size) {
            if (centerPayloads!![i].errorMessage == null) {
                mCenterSyncIndex = i
                syncCenterPayload(centerPayloads!![i])
                break
            }
        }
    }

    fun syncCenterPayload(centerPayload: CenterPayload?) {
        mSubscriptions!!.add(
            mDataManagerCenter!!.createCenter(centerPayload!!)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Observer<SaveResponse?> {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        showCenterSyncFailed(errorMessage(e))
                    }

                    override fun onNext(center: SaveResponse?) {
                        showCenterSyncResponse()
                    }
                })
        )
    }

    fun showCenterSyncResponse() {
        deleteAndUpdateCenterPayload(
            centerPayloads
            !![mCenterSyncIndex].id
        )
    }

    private fun deleteAndUpdateCenterPayload(id: Int) {
        mSubscriptions!!.add(
            mDataManagerCenter!!.deleteAndUpdateCenterPayloads(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Observer<List<CenterPayload>> {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {}
                    override fun onNext(centerPayloads: List<CenterPayload>) {
                        showPayloadDeletedAndUpdatePayloads(centerPayloads)
                    }
                })
        )
    }

    fun showPayloadDeletedAndUpdatePayloads(centers: List<CenterPayload>) {
        mCenterSyncIndex = 0
        centerPayloads = centers.toMutableList()
        if (centerPayloads!!.isNotEmpty()) {
            startSync()
        }
    }

    fun showCenterSyncFailed(errorMessage: String?) {
        val centerPayload = centerPayloads!![mCenterSyncIndex]
        centerPayload.errorMessage = errorMessage
        updateCenterPayload(centerPayload)
    }

    fun showCenterPayloadUpdated(centerPayload: CenterPayload) {
        centerPayloads!![mCenterSyncIndex] = centerPayload
        mCenterSyncIndex += 1
        if (centerPayloads!!.size != mCenterSyncIndex) {
            startSync()
        }
    }

    private fun updateCenterPayload(centerPayload: CenterPayload?) {
        mSubscriptions!!.add(
            mDataManagerCenter!!.updateCenterPayload(centerPayload!!)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<CenterPayload>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {}
                    override fun onNext(centerPayload: CenterPayload) {
                        showCenterPayloadUpdated(centerPayload)
                    }
                })
        )
    }

    companion object {
        @JvmStatic
        fun schedulePeriodic() {
            JobRequest.Builder(Tags.OfflineSyncCenter)
                .setPeriodic(
                    TimeUnit.MINUTES.toMillis(15),
                    TimeUnit.MINUTES.toMillis(5)
                )
                .build()
                .schedule()
        }
    }
}