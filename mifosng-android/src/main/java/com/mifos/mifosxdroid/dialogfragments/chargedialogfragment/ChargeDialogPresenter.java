package com.mifos.mifosxdroid.dialogfragments.chargedialogfragment;

import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.base.Presenter;
import com.mifos.objects.client.Charges;
import com.mifos.services.data.ChargesPayload;

import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 19/3/16.
 */
public class ChargeDialogPresenter implements Presenter<ChargeDialogMvpView> {

    private final DataManager mDatamanager;
    private Subscription mSubscription;
    private ChargeDialogMvpView mChargeDialogMvpView;

    public ChargeDialogPresenter(DataManager dataManager){
        mDatamanager = dataManager;
    }

    @Override
    public void attachView(ChargeDialogMvpView mvpView) {
        mChargeDialogMvpView = mvpView;
    }

    @Override
    public void detachView() {
        mChargeDialogMvpView = null;
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void loadAllCharges(){
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDatamanager.getAllChargesS()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<Charges>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<Charges> charges) {

                    }
                });
    }

    public void CreateCharges(int clientId, ChargesPayload payload){
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDatamanager.createcharges(clientId,payload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Charges>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mChargeDialogMvpView.ResponseError("Try Again");
                    }

                    @Override
                    public void onNext(Charges charges) {
                        mChargeDialogMvpView.createchargesRsult(charges);
                    }
                });
    }
}
