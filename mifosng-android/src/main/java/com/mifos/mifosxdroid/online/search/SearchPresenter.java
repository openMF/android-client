package com.mifos.mifosxdroid.online.search;

import com.mifos.api.datamanager.DataManagerSearch;
import com.mifos.mifosxdroid.R;
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
public class SearchPresenter extends BasePresenter<SearchMvpView> {

    private final DataManagerSearch dataManagerSearch;
    private Subscription mSubscription;

    @Inject
    public SearchPresenter(DataManagerSearch dataManager) {
        dataManagerSearch = dataManager;
    }

    @Override
    public void attachView(SearchMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void searchResources(String query, String resources, Boolean exactMatch) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = dataManagerSearch.searchResources(query, resources, exactMatch)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<SearchedEntity>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showMessage(R.string.failed_to_fetch_resources_of_query);
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onNext(List<SearchedEntity> searchedEntities) {
                        getMvpView().showProgressbar(false);
                        if (searchedEntities.size() == 0) {
                            getMvpView().showNoResultFound();
                        } else {
                            getMvpView().showSearchedResources(searchedEntities);
                        }
                    }
                });
    }

}
