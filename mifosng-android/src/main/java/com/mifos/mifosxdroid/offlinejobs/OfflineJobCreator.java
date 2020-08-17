package com.mifos.mifosxdroid.offlinejobs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;
import com.mifos.utils.Tags;

/**
 * Created by aksh on 17/7/18.
 */

public class OfflineJobCreator implements JobCreator {
    @Nullable
    @Override
    public Job create(@NonNull String tag) {
        switch (tag) {
            case Tags.OfflineSyncCenter:
                return new OfflineSyncCenter();
            case Tags.OfflineSyncClient:
                return new OfflineSyncClient();
            case Tags.OfflineSyncGroup:
                return new OfflineSyncGroup();
            case Tags.OfflineSyncSavingsAccount:
                return new OfflineSyncSavingsAccount();
            case Tags.OfflineSyncLoanRepayment:
                return new OfflineSyncLoanRepayment();
            default:
                return null;
        }
    }
}
