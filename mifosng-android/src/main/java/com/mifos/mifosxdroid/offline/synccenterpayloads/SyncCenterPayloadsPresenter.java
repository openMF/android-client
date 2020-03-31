package com.mifos.mifosxdroid.offline.synccenterpayloads;

import com.mifos.api.datamanager.DataManagerCenter;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.response.SaveResponse;
import com.mifos.services.data.CenterPayload;
import com.mifos.utils.MFErrorParser;

import java.util.List;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by mayankjindal on 04/07/17.
 */

public class SyncCenterPayloadsPresenter extends BasePresenter<SyncCenterPayloadsMvpView> {

    private final DataManagerCenter mDataManagerCenter;
    private CompositeSubscription mSubscriptions;

    @Inject
    public SyncCenterPayloadsPresenter(DataManagerCenter dataManagerCenter) {
        mDataManagerCenter = dataManagerCenter;
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void attachView(SyncCenterPayloadsMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        mSubscriptions.unsubscribe();
    }

    public void loadDatabaseCenterPayload() {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerCenter.getAllDatabaseCenterPayload()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<CenterPayload>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError(R.string.failed_to_load_centerpayload);
                    }

                    @Override
                    public void onNext(List<CenterPayload> centerPayloads) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showCenters(centerPayloads);
                    }
                }));
    }

    public void syncCenterPayload(CenterPayload centerPayload) {
        checkViewAttached();
        getMvpView().showProgressbar(true);;
        mSubscriptions.add(mDataManagerCenter.createCenter(centerPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<SaveResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showCenterSyncFailed(MFErrorParser.errorMessage(e));
                    }

                    @Override
                    public void onNext(SaveResponse center) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showCenterSyncResponse();
                    }
                }));
    }

    public void deleteAndUpdateCenterPayload(int id) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerCenter.deleteAndUpdateCenterPayloads(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<CenterPayload>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError(R.string.failed_to_update_list);
                    }

                    @Override
                    public void onNext(List<CenterPayload> centerPayloads) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showPayloadDeletedAndUpdatePayloads(centerPayloads);
                    }
                }));
    }

    public void updateCenterPayload(CenterPayload centerPayload) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerCenter.updateCenterPayload(centerPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<CenterPayload>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError(R.string.failed_to_update_list);
                    }

                    @Override
                    public void onNext(CenterPayload centerPayload) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showCenterPayloadUpdated(centerPayload);
                    }
                })
        );
    }
}
