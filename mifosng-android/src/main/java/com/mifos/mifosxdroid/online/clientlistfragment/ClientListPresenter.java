/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.clientlistfragment;

import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.base.Presenter;
import com.mifos.objects.client.Client;
import com.mifos.objects.client.Page;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 17/3/16.
 */
public class ClientListPresenter implements Presenter<ClientListMvpView> {

    private final DataManager mDataManager;
    public Subscription mSubscription;
    private ClientListMvpView mClientListMvpView;

    public ClientListPresenter(DataManager dataManager){
        mDataManager = dataManager;
    }

    @Override
    public void attachView(ClientListMvpView mvpView) {
        mClientListMvpView = mvpView;
    }

    @Override
    public void detachView() {
        mClientListMvpView = null;
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void loadclientlist(){
        mClientListMvpView.showprogressbar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getClientList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Page<Client>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mClientListMvpView.showprogressbar(false);
                        mClientListMvpView.showErrorFetchingList();
                    }

                    @Override
                    public void onNext(Page<Client> clientPage) {
                        mClientListMvpView.showprogressbar(false);
                        mClientListMvpView.showClientList(clientPage);
                    }
                });
    }

    public void loadmoreclientlist(int offset, int limit){
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getClientList(offset,limit)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Page<Client>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mClientListMvpView.showErrorFetchingList();
                    }

                    @Override
                    public void onNext(Page<Client> clientPage) {
                        mClientListMvpView.showMoreClientList(clientPage);
                    }
                });
    }
}
