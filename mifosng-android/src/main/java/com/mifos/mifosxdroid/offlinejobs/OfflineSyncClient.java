package com.mifos.mifosxdroid.offlinejobs;

import androidx.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.mifos.App;
import com.mifos.api.datamanager.DataManagerClient;
import com.mifos.objects.client.Client;
import com.mifos.objects.client.ClientPayload;
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
 * Created by aksh on 22/7/18.
 */

public class OfflineSyncClient extends Job {
    @Inject
    DataManagerClient mDataManagerClient;

    List<ClientPayload> clientPayloads;
    private CompositeSubscription mSubscriptions;
    private int mClientSyncIndex = 0;

    public static void schedulePeriodic() {
        new JobRequest.Builder(Tags.OfflineSyncClient)
                .setPeriodic(TimeUnit.MINUTES.toMillis(15),
                        TimeUnit.MINUTES.toMillis(5))
                .build()
                .schedule();
    }

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        mSubscriptions = new CompositeSubscription();
        clientPayloads = new ArrayList<>();
        App.get(getContext()).getComponent().inject(this);
        if (PrefManager.getUserStatus() == 0) {
            loadDatabaseClientPayload();
            return Result.SUCCESS;
        } else {
            return Result.FAILURE;
        }

    }

    public void loadDatabaseClientPayload() {
        mSubscriptions.add(mDataManagerClient.getAllDatabaseClientPayload()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<ClientPayload>>() {
                    @Override
                    public void onCompleted() {
                        mClientSyncIndex = 0;
                        syncClient();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<ClientPayload> clientPayloads) {
                        showPayloads(clientPayloads);
                    }
                }));
    }

    public void showPayloads(List<ClientPayload> clientPayload) {
        clientPayloads = clientPayload;
    }

    public void syncClient() {
        for (int i = mClientSyncIndex; i < clientPayloads.size(); ++i) {
            if (clientPayloads.get(i).getErrorMessage() == null) {
                syncClientPayload(clientPayloads.get(i));
                mClientSyncIndex = i;
                break;
            }
        }
    }

    public void syncClientPayload(ClientPayload clientPayload) {
        mSubscriptions.add(mDataManagerClient.createClient(clientPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Client>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        showClientSyncFailed(MFErrorParser.errorMessage(e));
                    }

                    @Override
                    public void onNext(Client client) {
                        showSyncResponse();
                    }
                }));
    }

    public void showClientSyncFailed(String errorMessage) {
        ClientPayload clientPayload = clientPayloads.get(mClientSyncIndex);
        clientPayload.setErrorMessage(errorMessage);
        updateClientPayload(clientPayload);
    }

    public void showSyncResponse() {
        deleteAndUpdateClientPayload(clientPayloads
                        .get(mClientSyncIndex).getId(),
                clientPayloads.get(mClientSyncIndex).getClientCreationTime());
    }

    public void deleteAndUpdateClientPayload(int id, long clientCreationTIme) {
        mSubscriptions.add(mDataManagerClient.deleteAndUpdatePayloads(id,
                clientCreationTIme)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<ClientPayload>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<ClientPayload> clientPayloads) {
                        showPayloadDeletedAndUpdatePayloads(clientPayloads);
                    }
                }));
    }

    public void updateClientPayload(ClientPayload clientPayload) {
        mSubscriptions.add(mDataManagerClient.updateClientPayload(clientPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<ClientPayload>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(ClientPayload clientPayload) {
                        showClientPayloadUpdated(clientPayload);
                    }
                })
        );
    }

    public void showPayloadDeletedAndUpdatePayloads(List<ClientPayload> clients) {
        mClientSyncIndex = 0;
        clientPayloads.clear();
        this.clientPayloads = clients;
        if (clientPayloads.size() != 0) {
            syncClient();
        }
    }

    public void showClientPayloadUpdated(ClientPayload clientPayload) {
        clientPayloads.set(mClientSyncIndex, clientPayload);
        mClientSyncIndex = mClientSyncIndex + 1;
        if (clientPayloads.size() != mClientSyncIndex) {
            syncClient();
        }
    }
}
