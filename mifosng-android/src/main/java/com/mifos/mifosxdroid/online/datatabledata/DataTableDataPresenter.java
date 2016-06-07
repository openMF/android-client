package com.mifos.mifosxdroid.online.datatabledata;

import com.google.gson.JsonArray;
import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.base.BasePresenter;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 7/6/16.
 */
public class DataTableDataPresenter extends BasePresenter<DataTableDataMvpView> {

    private final DataManager mDataManager;
    private Subscription mSubscription;

    @Inject
    public DataTableDataPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(DataTableDataMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void loadDataTableInfo(String table, int entityId) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getDataTableInfo(table, entityId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<JsonArray>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError("Failed to fetch DataTableInfo");
                    }

                    @Override
                    public void onNext(JsonArray jsonElements) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showDataTableInfo(jsonElements);
                    }
                });
    }
}
