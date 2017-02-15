package com.mifos.mifosxdroid.offline.syncgrouppayloads;

import com.mifos.api.datamanager.DataManagerGroups;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.group.GroupPayload;
import com.mifos.objects.response.SaveResponse;
import com.mifos.utils.MFErrorParser;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Rajan Maurya on 19/07/16.
 */
public class SyncGroupPayloadsPresenter extends BasePresenter<SyncGroupPayloadsMvpView> {

    public final DataManagerGroups mDataManagerGroups;
    public CompositeSubscription mSubscriptions;

    @Inject
    public SyncGroupPayloadsPresenter(DataManagerGroups dataManagerGroups) {
        mDataManagerGroups = dataManagerGroups;
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void attachView(SyncGroupPayloadsMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        mSubscriptions.unsubscribe();
    }

    public void loanDatabaseGroupPayload() {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerGroups.getAllDatabaseGroupPayload()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<GroupPayload>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError(R.string.failed_to_load_grouppayload);
                    }

                    @Override
                    public void onNext(List<GroupPayload> groupPayloads) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showGroups(groupPayloads);
                    }
                }));
    }

    public void syncGroupPayload(GroupPayload groupPayload) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerGroups.createGroup(groupPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<SaveResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showGroupSyncFailed(MFErrorParser.errorMessage(e));
                    }

                    @Override
                    public void onNext(SaveResponse group) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showGroupSyncResponse();
                    }
                }));
    }

    public void deleteAndUpdateGroupPayload(int id) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerGroups.deleteAndUpdateGroupPayloads(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<GroupPayload>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError(R.string.failed_to_update_list);
                    }

                    @Override
                    public void onNext(List<GroupPayload> groupPayloads) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showPayloadDeletedAndUpdatePayloads(groupPayloads);
                    }
                }));
    }

    public void updateGroupPayload(GroupPayload groupPayload) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerGroups.updateGroupPayload(groupPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<GroupPayload>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError(R.string.failed_to_load_grouppayload);
                    }

                    @Override
                    public void onNext(GroupPayload groupPayload) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showGroupPayloadUpdated(groupPayload);
                    }
                })
        );

    }
}
