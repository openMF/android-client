package com.mifos.mifosxdroid.dialogfragments.datatablerowdialog;

import com.mifos.api.GenericResponse;
import com.mifos.api.datamanager.DataManagerDataTable;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.utils.MFErrorParser;

import java.util.Map;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Rajan Maurya on 08/06/16.
 */
public class DataTableRowDialogPresenter extends BasePresenter<DataTableRowDialogMvpView> {

    private final DataManagerDataTable dataManagerDataTable;
    private CompositeSubscription subscriptions;

    @Inject
    public DataTableRowDialogPresenter(DataManagerDataTable dataManager) {
        dataManagerDataTable = dataManager;
        subscriptions = new CompositeSubscription();
    }

    @Override
    public void attachView(DataTableRowDialogMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        subscriptions.unsubscribe();
    }

    public void addDataTableEntry(String table, int entityId, Map<String, Object> payload) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        subscriptions.add(dataManagerDataTable.addDataTableEntry(table, entityId, payload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<GenericResponse>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError(MFErrorParser.errorMessage(e));
                    }

                    @Override
                    public void onNext(GenericResponse genericResponse) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showDataTableEntrySuccessfully(genericResponse);
                    }
                }));
    }

}
