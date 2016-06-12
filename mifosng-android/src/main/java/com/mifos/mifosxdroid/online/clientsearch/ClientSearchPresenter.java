package com.mifos.mifosxdroid.online.clientsearch;

import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.SearchedEntity;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 06/06/16.
 */
public class ClientSearchPresenter extends BasePresenter<ClientSearchMvpView> {


    private final DataManager mDataManager;
    private Subscription mSubscription;

    @Inject
    public ClientSearchPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(ClientSearchMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void searchClients(String query) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.searchClientsByName(query)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<SearchedEntity>>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError("Failed to load clients");
                    }

                    @Override
                    public void onNext(List<SearchedEntity> searchedEntities) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showClientsSearched(searchedEntities);
                    }
                });
    }

}
