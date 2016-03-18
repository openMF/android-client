package com.mifos.mifosxdroid.online.datatabledatafragment;

import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.base.Presenter;

import rx.Subscription;

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
}
