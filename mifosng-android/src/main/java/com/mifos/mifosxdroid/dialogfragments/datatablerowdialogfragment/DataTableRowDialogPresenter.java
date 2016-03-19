package com.mifos.mifosxdroid.dialogfragments.datatablerowdialogfragment;

import com.mifos.api.DataManager;
import com.mifos.api.GenericResponse;
import com.mifos.mifosxdroid.base.Presenter;
import java.util.Map;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 19/3/16.
 */
public class DataTableRowDialogPresenter implements Presenter<DataTableRowDialogMvpView>{

    private final DataManager mDatamanager;
    private Subscription mSubscription;
    private DataTableRowDialogMvpView mDataTableRowDialogMvpView;

    public DataTableRowDialogPresenter(DataManager dataManager){
        mDatamanager = dataManager;
    }

    @Override
    public void attachView(DataTableRowDialogMvpView mvpView) {
        mDataTableRowDialogMvpView = mvpView;
    }

    @Override
    public void detachView() {
        mDataTableRowDialogMvpView = null;
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void addDataTableEntry(String table, int entityId, Map<String, Object> payload){
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDatamanager.addDataTableEntry(table,entityId,payload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<GenericResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mDataTableRowDialogMvpView.ResponseCreationError(e.getMessage());
                    }

                    @Override
                    public void onNext(GenericResponse genericResponse) {
                        mDataTableRowDialogMvpView.showDatatableRawCreationEntry(genericResponse);
                    }
                });

    }
}
