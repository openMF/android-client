package com.mifos.mifosxdroid.online.savingaccountsummary;

import com.mifos.api.GenericResponse;
import com.mifos.api.datamanager.DataManagerDataTable;
import com.mifos.api.datamanager.DataManagerSavings;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.accounts.savings.SavingsAccountWithAssociations;
import com.mifos.objects.noncore.DataTable;
import com.mifos.utils.Constants;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Rajan Maurya on 07/06/16.
 */
public class SavingsAccountSummaryPresenter extends BasePresenter<SavingsAccountSummaryMvpView> {

    private final DataManagerDataTable mDataManagerDataTable;
    private final DataManagerSavings mDataManagerSavings;
    private CompositeSubscription mSubscriptions;

    @Inject
    public SavingsAccountSummaryPresenter(DataManagerDataTable dataManagerDataTable,
                                          DataManagerSavings dataManagerSavings) {
        mDataManagerDataTable = dataManagerDataTable;
        mDataManagerSavings = dataManagerSavings;
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void attachView(SavingsAccountSummaryMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        mSubscriptions.unsubscribe();
    }

    public void loadSavingDataTable() {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerDataTable.getDataTable(Constants.DATA_TABLE_NAME_SAVINGS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<DataTable>>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError(R.string.failed_to_fetch_datatable);
                    }

                    @Override
                    public void onNext(List<DataTable> dataTables) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showSavingDataTable(dataTables);
                    }
                }));
    }

    //This Method will hit end point ?associations=transactions
    public void loadSavingAccount(String type, int accountId) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerSavings
                .getSavingsAccount(type, accountId, Constants.TRANSACTIONS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<SavingsAccountWithAssociations>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showErrorFetchingSavingAccount(
                                R.string.failed_to_fetch_savingsaccount);
                    }

                    @Override
                    public void onNext(
                            SavingsAccountWithAssociations savingsAccountWithAssociations) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showSavingAccount(savingsAccountWithAssociations);
                    }
                }));
    }


    public void activateSavings(int savingsAccountId, HashMap<String, Object> request) {
        checkViewAttached();
        mSubscriptions.add(mDataManagerSavings.activateSavings(savingsAccountId, request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<GenericResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showFetchingError(R.string.error_to_activate_savingsaccount);
                    }

                    @Override
                    public void onNext(GenericResponse genericResponse) {
                        getMvpView().showSavingsActivatedSuccessfully(genericResponse);
                    }
                }));
    }
}
