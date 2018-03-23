package com.mifos.mifosxdroid.online.datatabledata;

import com.google.gson.JsonArray;
import com.mifos.api.GenericResponse;
import com.mifos.api.datamanager.DataManagerDataTable;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.utils.MFErrorParser;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 7/6/16.
 */
public class DataTableDataPresenter extends BasePresenter<DataTableDataMvpView> {

    private final DataManagerDataTable dataManagerDataTable;
    private CompositeDisposable compositeDisposable;

    @Inject
    public DataTableDataPresenter(DataManagerDataTable dataManager) {
        dataManagerDataTable = dataManager;
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void attachView(DataTableDataMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        compositeDisposable.clear();
    }

    public void loadDataTableInfo(String table, int entityId) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        compositeDisposable.add(dataManagerDataTable.getDataTableInfo(table, entityId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<JsonArray>() {
                    @Override
                    public void onComplete() {
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
        compositeDisposable.add(dataManagerDataTable.deleteDataTableEntry(table, entity, rowId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<GenericResponse>() {
                    @Override
                    public void onComplete() {

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
