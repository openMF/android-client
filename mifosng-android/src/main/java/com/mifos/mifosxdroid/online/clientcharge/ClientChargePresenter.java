package com.mifos.mifosxdroid.online.clientcharge;

import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.base.Presenter;
import com.mifos.objects.client.Charges;
import com.mifos.objects.client.Page;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 5/6/16.
 */
public class ClientChargePresenter implements Presenter<ClientChargeMvpView> {


    private final DataManager mDataManager;
    private Subscription mSubscription;
    private ClientChargeMvpView mClientChargeMvpView;

    @Inject
    public ClientChargePresenter(DataManager dataManager) {
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

    public void loadCharges(int clientId, int offset, int limit) {
        mClientChargeMvpView.showProgressbar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getClientCharges(clientId, offset, limit)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Page<Charges>>() {
                    @Override
                    public void onCompleted() {
                        mClientChargeMvpView.showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mClientChargeMvpView.showProgressbar(false);
                        mClientChargeMvpView
                                .showFetchingErrorCharges("Failed to Load Charges");
                    }

                    @Override
                    public void onNext(Page<Charges> chargesPage) {
                        mClientChargeMvpView.showProgressbar(false);
                        mClientChargeMvpView.showChargesList(chargesPage);
                    }
                });
    }


}
