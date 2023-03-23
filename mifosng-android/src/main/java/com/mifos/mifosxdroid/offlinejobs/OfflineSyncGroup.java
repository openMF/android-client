package com.mifos.mifosxdroid.offlinejobs;

import androidx.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.mifos.App;
import com.mifos.api.datamanager.DataManagerGroups;
import com.mifos.objects.group.GroupPayload;
import com.mifos.objects.response.SaveResponse;
import com.mifos.utils.MFErrorParser;
import com.mifos.utils.PrefManager;
import com.mifos.utils.Tags;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by aksh on 22/7/18.
 */

public class OfflineSyncGroup extends Job {

    public CompositeSubscription mSubscriptions;
    List<GroupPayload> groupPayloads;
    @Inject
    DataManagerGroups mDataManagerGroups;

    private int mClientSyncIndex = 0;

    public static void schedulePeriodic() {
        new JobRequest.Builder(Tags.OfflineSyncGroup)
                .setPeriodic(TimeUnit.MINUTES.toMillis(15), TimeUnit.MINUTES.toMillis(5))
                .setUpdateCurrent(true)
                .build()
                .schedule();
    }

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        mSubscriptions = new CompositeSubscription();
        groupPayloads = new ArrayList<>();
        App.get(getContext()).getComponent().inject(this);
        if (PrefManager.getUserStatus() == 0) {
            loadDatabaseGroupPayload();
            return Result.SUCCESS;
        } else {
            return Result.FAILURE;
        }
    }

    public void loadDatabaseGroupPayload() {
        mSubscriptions.add(mDataManagerGroups.getAllDatabaseGroupPayload()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<GroupPayload>>() {
                    @Override
                    public void onCompleted() {
                        mClientSyncIndex = 0;
                        startSync();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<GroupPayload> groupPayloads) {
                        showGroups(groupPayloads);
                    }
                }));
    }

    public void showGroups(List<GroupPayload> groupPayload) {
        groupPayloads = groupPayload;
    }

    public void startSync() {
        for (int i = mClientSyncIndex; i < groupPayloads.size(); ++i) {
            if (groupPayloads.get(i).getErrorMessage() == null) {
                syncGroupPayload(groupPayloads.get(i));
                mClientSyncIndex = i;
                break;
            }
        }
    }

    public void syncGroupPayload(GroupPayload groupPayload) {
        mSubscriptions.add(mDataManagerGroups.createGroup(groupPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<SaveResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        showGroupSyncFailed(MFErrorParser.errorMessage(e));
                    }

                    @Override
                    public void onNext(SaveResponse group) {
                        showGroupSyncResponse();
                    }
                }));
    }

    public void showGroupSyncFailed(String errorMessage) {
        GroupPayload groupPayload = groupPayloads.get(mClientSyncIndex);
        groupPayload.setErrorMessage(errorMessage);
        updateGroupPayload(groupPayload);

    }

    public void showGroupSyncResponse() {
        deleteAndUpdateGroupPayload(groupPayloads
                .get(mClientSyncIndex).getId());
    }

    public void updateGroupPayload(GroupPayload groupPayload) {
        mSubscriptions.add(mDataManagerGroups.updateGroupPayload(groupPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<GroupPayload>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(GroupPayload groupPayload) {
                        showGroupPayloadUpdated(groupPayload);
                    }
                })
        );

    }

    public void deleteAndUpdateGroupPayload(int id) {
        mSubscriptions.add(mDataManagerGroups.deleteAndUpdateGroupPayloads(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<GroupPayload>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(List<GroupPayload> groupPayloads) {
                        showPayloadDeletedAndUpdatePayloads(groupPayloads);
                    }
                }));
    }

    public void showGroupPayloadUpdated(GroupPayload groupPayload) {
        groupPayloads.set(mClientSyncIndex, groupPayload);

        mClientSyncIndex = mClientSyncIndex + 1;
        if (groupPayloads.size() != mClientSyncIndex) {
            startSync();
        }
    }

    public void showPayloadDeletedAndUpdatePayloads(List<GroupPayload> groups) {
        mClientSyncIndex = 0;
        this.groupPayloads = groups;
        if (groupPayloads.size() != 0) {
            startSync();
        }
    }
}
