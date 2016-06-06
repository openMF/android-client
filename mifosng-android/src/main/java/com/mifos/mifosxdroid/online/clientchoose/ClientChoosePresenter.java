package com.mifos.mifosxdroid.online.clientchoose;

import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.client.Client;
import com.mifos.objects.client.Page;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 06/06/16.
 */
public class ClientChoosePresenter extends BasePresenter<ClientChooseMvpView> {

    private final DataManager mDataManager;
    private Subscription mSubscription;

    @Inject
    public ClientChoosePresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(ClientChooseMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void loadClientsList() {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getAllClients()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Page<Client>>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError(
                                "\"Cannot get clients, There might be some" + " problem!\"");
                    }

                    @Override
                    public void onNext(Page<Client> clientPage) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showClientList(clientPage);
                    }
                });


    }

    public void loadMoreClientList(int offset, int limit) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getAllClients(offset, limit)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Page<Client>>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError(
                                "\"Cannot get clients, There might be some" + " problem!\"");
                    }

                    @Override
                    public void onNext(Page<Client> clientPage) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showMoreClientList(clientPage);
                    }
                });

    }

}
