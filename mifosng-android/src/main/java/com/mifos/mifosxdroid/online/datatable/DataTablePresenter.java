package com.mifos.mifosxdroid.online.datatable;

import com.mifos.api.datamanager.DataManagerDataTable;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.noncore.DataTable;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by Rajan Maurya on 12/02/17.
 */
public class DataTablePresenter extends BasePresenter<DataTableMvpView> {

    private final DataManagerDataTable dataManagerDataTable;
    private CompositeDisposable compositeDisposable;

    @Inject
    public DataTablePresenter(DataManagerDataTable dataManagerDataTable) {
        this.dataManagerDataTable = dataManagerDataTable;
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void attachView(DataTableMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        compositeDisposable.clear();
    }

    /**
     * This method load the DataTable.
     *
     * Table name can be
     * "m_client"
     * "m_group"
     * "m_loan"
     * "m_office"
     * "m_saving_account"
     * "m_product_loan"
     * "m_savings_product
     *
     * Response: List<DataTable></DataTable>
     */
    public void loadDataTable(String tableName) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        getMvpView().showResetVisibility();
        compositeDisposable.add(dataManagerDataTable.getDataTable(tableName)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<List<DataTable>>() {
                    @Override
                    public void onComplete() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError(R.string.failed_to_fetch_datatable);
                    }

                    @Override
                    public void onNext(List<DataTable> dataTables) {
                        getMvpView().showProgressbar(false);
                        if (!dataTables.isEmpty()) {
                            getMvpView().showDataTables(dataTables);
                        } else {
                            getMvpView().showEmptyDataTables();
                        }
                    }
                }));
    }

}
