package com.mifos.mifosxdroid.online.datatabledata;

import com.google.gson.JsonArray;
import com.mifos.api.GenericResponse;
import com.mifos.api.datamanager.DataManagerDataTable;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.utils.MFErrorParser;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Rajan Maurya on 7/6/16.
 */
public class DataTableDataPresenter extends BasePresenter<DataTableDataMvpView> {

    private final DataManagerDataTable dataManagerDataTable;
    private CompositeSubscription subscriptions;

    @Inject
    public DataTableDataPresenter(DataManagerDataTable dataManager) {
        dataManagerDataTable = dataManager;
        subscriptions = new CompositeSubscription();
    }

    @Override
    public void attachView(DataTableDataMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        subscriptions.unsubscribe();
    }

    public void loadDataTableInfo(String table, int entityId) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        subscriptions.add(dataManagerDataTable.getDataTableInfo(table, entityId)
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
                        getMvpView().showFetchingError(R.string.failed_to_fetch_datatable_info);
                    }

                    @Override
                    public void onNext(JsonArray jsonElements) {
                        getMvpView().showProgressbar(false);
                        if (jsonElements.size() == 0) {
                            getMvpView().showEmptyDataTable();
                        } else {
                            getMvpView().showDataTableInfo(jsonElements);
                        }
                    }
                }));
    }

    public void deleteDataTableEntry(String table, int entity, int rowId) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        subscriptions.add(dataManagerDataTable.deleteDataTableEntry(table, entity, rowId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<GenericResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError(MFErrorParser.errorMessage(e));
                    }

                    @Override
                    public void onNext(GenericResponse genericResponse) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showDataTableDeletedSuccessfully();
                    }
                })
        );
    }
}
