/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.clientchoosefragment;

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
public class ClientChoosePresenter implements Presenter<ClientChooseMvpView> {

    private final DataManager mDataManager;
    public Subscription mSubscription;
    private ClientChooseMvpView mClientChooseMvpView;

    public ClientChoosePresenter(DataManager dataManager){
        mDataManager = dataManager;
    }
    @Override
    public void attachView(ClientChooseMvpView mvpView) {
        mClientChooseMvpView = mvpView;
    }

    @Override
    public void detachView() {
        mClientChooseMvpView = null;
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void loadchoosingclientlist(){
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
                        mClientChooseMvpView.showErrorFetching();
                    }

                    @Override
                    public void onNext(Page<Client> clientPage) {
                        mClientChooseMvpView.showclientlist(clientPage);
                    }
                });
    }
}
