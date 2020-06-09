package com.mifos.mifosxdroid.offlinejobs;

import androidx.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.mifos.App;
import com.mifos.api.datamanager.DataManagerCenter;
import com.mifos.objects.response.SaveResponse;
import com.mifos.services.data.CenterPayload;
import com.mifos.utils.MFErrorParser;
import com.mifos.utils.PrefManager;
import com.mifos.utils.Tags;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by aksh on 17/7/18.
 */

public class OfflineSyncCenter extends Job {

    @Inject
    DataManagerCenter mDataManagerCenter;

    List<CenterPayload> centerPayloads;
    private CompositeSubscription mSubscriptions;
    private int mCenterSyncIndex = 0;

    public static void schedulePeriodic() {
        new JobRequest.Builder(Tags.OfflineSyncCenter)
                .setPeriodic(TimeUnit.MINUTES.toMillis(15),
                        TimeUnit.MINUTES.toMillis(5))
                .build()
                .schedule();
    }

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        mSubscriptions = new CompositeSubscription();
        centerPayloads = new ArrayList<>();
        App.get(getContext()).getComponent().inject(this);
        if (PrefManager.getUserStatus() == 0) {
            loadDatabaseCenterPayload();
            return Result.SUCCESS;
        } else {
            return Result.FAILURE;
        }
    }

    public void loadDatabaseCenterPayload() {
        mSubscriptions.add(mDataManagerCenter.getAllDatabaseCenterPayload()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<CenterPayload>>() {
                    @Override
                    public void onCompleted() {
                        mCenterSyncIndex = 0;
                        startSync();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<CenterPayload> centerPayloads) {
                        showCenters(centerPayloads);
                    }
                }));
    }

    public void showCenters(List<CenterPayload> centerPayload) {
        centerPayloads = centerPayload;
    }

    public void startSync() {
        for (int i = mCenterSyncIndex; i < centerPayloads.size(); ++i) {
            if (centerPayloads.get(i).getErrorMessage() == null) {
                mCenterSyncIndex = i;
                syncCenterPayload(centerPayloads.get(i));
                break;
            }
        }
    }

    public void syncCenterPayload(CenterPayload centerPayload) {
        mSubscriptions.add(mDataManagerCenter.createCenter(centerPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<SaveResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        showCenterSyncFailed(MFErrorParser.errorMessage(e));
                    }

                    @Override
                    public void onNext(SaveResponse center) {
                        showCenterSyncResponse();
                    }
                }));
    }

    public void showCenterSyncResponse() {
        deleteAndUpdateCenterPayload(centerPayloads
                .get(mCenterSyncIndex).getId());
    }

    public void deleteAndUpdateCenterPayload(int id) {
        mSubscriptions.add(mDataManagerCenter.deleteAndUpdateCenterPayloads(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<CenterPayload>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<CenterPayload> centerPayloads) {
                        showPayloadDeletedAndUpdatePayloads(centerPayloads);
                    }
                }));
    }

    public void showPayloadDeletedAndUpdatePayloads(List<CenterPayload> centers) {
        mCenterSyncIndex = 0;
        this.centerPayloads = centers;
        if (centerPayloads.size() != 0) {

            startSync();
        }
    }

    public void showCenterSyncFailed(String errorMessage) {
        CenterPayload centerPayload = centerPayloads.get(mCenterSyncIndex);
        centerPayload.setErrorMessage(errorMessage);
        updateCenterPayload(centerPayload);

    }

    public void showCenterPayloadUpdated(CenterPayload centerPayload) {
        centerPayloads.set(mCenterSyncIndex, centerPayload);
        mCenterSyncIndex = mCenterSyncIndex + 1;
        if (centerPayloads.size() != mCenterSyncIndex) {
            startSync();
        }
    }

    public void updateCenterPayload(CenterPayload centerPayload) {
        mSubscriptions.add(mDataManagerCenter.updateCenterPayload(centerPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<CenterPayload>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(CenterPayload centerPayload) {
                        showCenterPayloadUpdated(centerPayload);
                    }
                })
        );
    }
}
