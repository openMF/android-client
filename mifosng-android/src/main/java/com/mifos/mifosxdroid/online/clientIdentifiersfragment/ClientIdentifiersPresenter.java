/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.clientIdentifiersfragment;

import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.base.Presenter;
import com.mifos.objects.noncore.Identifier;
import java.util.List;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 17/3/16.
 */
public class ClientIdentifiersPresenter implements Presenter<ClientIdentifiersMvpView> {


    private final DataManager mDataManager;
    public Subscription mSubscription;
    private ClientIdentifiersMvpView mClientIdentifiersMvpView;

    public ClientIdentifiersPresenter(DataManager dataManager){
        mDataManager = dataManager;
    }

    @Override
    public void attachView(ClientIdentifiersMvpView mvpView) {
        mClientIdentifiersMvpView = mvpView;
    }

    @Override
    public void detachView() {
        mClientIdentifiersMvpView = null;
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void loadIdentifiers(int clientid){
        mClientIdentifiersMvpView.showIdentifierProgressBar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getListOfIdentifiers(clientid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<Identifier>>() {
                    @Override
                    public void onCompleted() {
                        mClientIdentifiersMvpView.showIdentifierProgressBar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mClientIdentifiersMvpView.showIdentifierProgressBar(false);
                        mClientIdentifiersMvpView.showIdentifierFetchError();
                    }

                    @Override
                    public void onNext(List<Identifier> identifiers) {
                        mClientIdentifiersMvpView.showIdentifierProgressBar(false);
                        mClientIdentifiersMvpView.showIdentifiers(identifiers);
                    }
                });

    }
}
