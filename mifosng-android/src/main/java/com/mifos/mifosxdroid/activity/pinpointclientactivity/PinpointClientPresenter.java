package com.mifos.mifosxdroid.activity.pinpointclientactivity;

import com.mifos.api.DataManager;
import com.mifos.api.model.GpsCoordinatesRequest;
import com.mifos.api.model.GpsCoordinatesResponse;
import com.mifos.mifosxdroid.base.Presenter;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 19/3/16.
 */
public class PinpointClientPresenter implements Presenter<PinpointClientMvpView> {

    private final DataManager mDatamanager;
    private Subscription mSubscription;
    private PinpointClientMvpView mPinpointClientMvpView;

    public PinpointClientPresenter(DataManager dataManager){
        mDatamanager = dataManager;
    }


    @Override
    public void attachView(PinpointClientMvpView mvpView) {
        mPinpointClientMvpView = mvpView;
    }

    @Override
    public void detachView() {
        mPinpointClientMvpView = null;
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void updateGpsData(int client, GpsCoordinatesRequest request){
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDatamanager.updateGpsData(client,request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<GpsCoordinatesResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mPinpointClientMvpView.ResponseError("Error saving client location!");
                    }

                    @Override
                    public void onNext(GpsCoordinatesResponse gpsCoordinatesResponse) {
                        mPinpointClientMvpView.updateGpsResquestCallBack(gpsCoordinatesResponse);
                    }
                });
    }
}
