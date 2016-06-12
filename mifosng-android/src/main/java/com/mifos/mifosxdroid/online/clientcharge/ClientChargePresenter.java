package com.mifos.mifosxdroid.online.clientcharge;

import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.base.Presenter;
import com.mifos.objects.client.Charges;
import com.mifos.objects.client.Page;

import javax.inject.Inject;

import retrofit2.adapter.rxjava.HttpException;
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

    public void loadCharges(int id) {
        mClientChargeMvpView.showProgressbar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getClientCharges(id)
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
                        if (e instanceof HttpException) {
                            HttpException response = (HttpException) e;
                            mClientChargeMvpView.showFetchingErrorCharges(response.code());
                        }
                    }

                    @Override
                    public void onNext(Page<Charges> chargesPage) {
                        mClientChargeMvpView.showProgressbar(false);
                        mClientChargeMvpView.showChargesList(chargesPage);
                    }
                });
    }

    public void loadMoreClientCharges(int clientId) {
        mClientChargeMvpView.showProgressbar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getClientCharges(clientId)
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
                        if (e instanceof HttpException) {
                            HttpException response = (HttpException) e;
                            mClientChargeMvpView.showFetchingErrorCharges(response.code());
                        }
                    }

                    @Override
                    public void onNext(Page<Charges> chargesPage) {
                        mClientChargeMvpView.showProgressbar(false);
                        mClientChargeMvpView.showMoreClientCharges(chargesPage);
                    }
                });
    }

}
