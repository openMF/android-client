package com.mifos.mifosxdroid.offlinejobs

import com.evernote.android.job.Job
import com.evernote.android.job.JobCreator
import com.mifos.utils.Tags

/**
 * Created by aksh on 17/7/18.
 */
class OfflineJobCreator : JobCreator {
    override fun create(tag: String): Job? {
        return when (tag) {
            Tags.OfflineSyncCenter -> OfflineSyncCenter()
            Tags.OfflineSyncClient -> OfflineSyncClient()
            Tags.OfflineSyncGroup -> OfflineSyncGroup()
            Tags.OfflineSyncSavingsAccount -> OfflineSyncSavingsAccount()
            Tags.OfflineSyncLoanRepayment -> OfflineSyncLoanRepayment()
            else -> null
        }
    }
}