package com.mifos.mifosxdroid.activity.pinpointclient;

import com.mifos.api.DataManager;
import com.mifos.api.model.GpsCoordinatesRequest;
import com.mifos.api.model.GpsCoordinatesResponse;
import com.mifos.mifosxdroid.base.BasePresenter;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 08/06/16.
 */
public class PinPointClientPresenter extends BasePresenter<PinPointClientMvpView> {


    private final DataManager mDataManager;
    private Subscription mSubscription;

    @Inject
    public PinPointClientPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(PinPointClientMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void updateGpsData(int clientId, GpsCoordinatesRequest gpsCoordinatesRequest) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.updateGpsData(clientId, gpsCoordinatesRequest)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<GpsCoordinatesResponse>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFailedToUpdateGpsData("Error saving client location!");
                    }

                    @Override
                    public void onNext(GpsCoordinatesResponse gpsCoordinatesResponse) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showGpsDataUpdatedSuccessfully(gpsCoordinatesResponse);
                    }
                });
    }
}
