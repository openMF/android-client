package com.mifos.mifosxdroid.online.datatabledatafragment;

import com.google.gson.JsonArray;
import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.base.Presenter;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 18/3/16.
 */
public class DataTableDataPresenter implements Presenter<DataTableDataMvpView> {

    private final DataManager mDataManager;
    public Subscription mSubscription;
    public DataTableDataMvpView mDataTableDataMvpView;

    public DataTableDataPresenter(DataManager dataManager){
        mDataManager = dataManager;
    }

    @Override
    public void attachView(DataTableDataMvpView mvpView) {
        mDataTableDataMvpView = mvpView;
    }

    @Override
    public void detachView() {
        mDataTableDataMvpView = null;
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void loaddatatabledata(String datatablename , int entityId){
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getDataOfDataTable(datatablename,entityId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<JsonArray>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mDataTableDataMvpView.ResponseErrorDataTable("Failed Fetch DataTable");
                    }

                    @Override
                    public void onNext(JsonArray jsonElements) {
                        mDataTableDataMvpView.showDataTableData(jsonElements);
                    }
                });

    }
}
