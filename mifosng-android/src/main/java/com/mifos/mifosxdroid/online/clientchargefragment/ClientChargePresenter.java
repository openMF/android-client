/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.clientchargefragment;

import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.base.Presenter;
import com.mifos.objects.client.Charges;
import com.mifos.objects.client.Page;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 17/3/16.
 */
public class ClientChargePresenter implements Presenter<ClientChargeMvpView> {

    private final DataManager mDataManager;
    public Subscription mSubscription;
    private ClientChargeMvpView mClientChargeMvpView;

    public ClientChargePresenter(DataManager dataManager){
        mDataManager = dataManager;
    }

    @Override
    public void attachView(ClientChargeMvpView mvpView) {
        mClientChargeMvpView = mvpView;
    }

    @Override
    public void detachView() {
        mClientChargeMvpView = null;
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void loadclientchargeslist(int clientId){
        mClientChargeMvpView.showClientChargesProgressBar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getClientChargesList(clientId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Page<Charges>>() {
                    @Override
                    public void onCompleted() {
                        mClientChargeMvpView.showClientChargesProgressBar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mClientChargeMvpView.showClientChargesProgressBar(false);
                        mClientChargeMvpView.showClientChargesListFetchError();
                    }

                    @Override
                    public void onNext(Page<Charges> chargesPage) {
                        mClientChargeMvpView.showClientChargesProgressBar(false);
                        mClientChargeMvpView.showClientChargesList(chargesPage);
                    }
                });

    }

    public void loadmoreclientchargeslist(int clientid){
        mClientChargeMvpView.showClientChargesProgressBar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getClientChargesList(clientid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Page<Charges>>() {
                    @Override
                    public void onCompleted() {
                        mClientChargeMvpView.showClientChargesProgressBar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mClientChargeMvpView.showClientChargesProgressBar(false);
                        mClientChargeMvpView.showClientChargesListFetchError();
                    }

                    @Override
                    public void onNext(Page<Charges> chargesPage) {
                        mClientChargeMvpView.showClientChargesProgressBar(false);
                        mClientChargeMvpView.showMoreClientChargesList(chargesPage);
                    }
                });

    }
}
