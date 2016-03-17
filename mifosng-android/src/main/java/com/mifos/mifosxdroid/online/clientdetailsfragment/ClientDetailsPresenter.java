package com.mifos.mifosxdroid.online.clientdetailsfragment;

import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.base.Presenter;
import com.mifos.mifosxdroid.online.clientchargefragment.ClientChargeMvpView;

import rx.Subscription;

/**
 * Created by Rajan Maurya on 17/3/16.
 */
public class ClientDetailsPresenter implements Presenter<ClientDetailsMvpView> {

    private final DataManager mDataManager;
    public Subscription mSubscription;
    private ClientDetailsMvpView mClientDetailsMvpView;

    public ClientDetailsPresenter(DataManager dataManager){
        mDataManager = dataManager;
    }

    @Override
    public void attachView(ClientDetailsMvpView mvpView) {
        mClientDetailsMvpView = mvpView;
    }

    @Override
    public void detachView() {
        mClientDetailsMvpView = null;
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void deleteclientimage(){
        mClientDetailsMvpView.showClientDetailsProgressBar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.
    }
}
