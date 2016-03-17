/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.clientsearchfragment;

import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.base.Presenter;
import com.mifos.objects.SearchedEntity;
import java.util.List;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 18/3/16.
 */
public class ClientSearchPresenter implements Presenter<ClientSearchMvpView> {

    private final DataManager mDataManager;
    private ClientSearchMvpView mClientSearchMvpView;
    private Subscription mSubscription;

    public ClientSearchPresenter(DataManager dataManager){
        mDataManager = dataManager;
    }

    @Override
    public void attachView(ClientSearchMvpView mvpView) {
        mClientSearchMvpView = mvpView;
    }

    @Override
    public void detachView() {
        mClientSearchMvpView = null;
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void loadsearchresult(String name){
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.searchClientsByName(name)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<SearchedEntity>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mClientSearchMvpView.showSearchNotFound();
                    }

                    @Override
                    public void onNext(List<SearchedEntity> searchedEntities) {
                        mClientSearchMvpView.showsearchresult(searchedEntities);
                    }
                });
    }
}
