package com.mifos.mifosxdroid.online.savingaccountsummary;

import com.mifos.api.DataManager;
import com.mifos.api.GenericResponse;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.accounts.savings.SavingsAccountWithAssociations;
import com.mifos.objects.noncore.DataTable;

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
public class SavingAccountSummaryPresenter extends BasePresenter<SavingsAccountSummaryMvpView> {

    private final DataManager mDataManager;
    private CompositeSubscription mSubscriptions;

    @Inject
    public SavingAccountSummaryPresenter(DataManager dataManager) {
        mDataManager = dataManager;
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
        mSubscriptions.add(mDataManager.getSavingsDataTable()
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
                        getMvpView().showFetchingError("Saving DataTable not found");
                    }

                    @Override
                    public void onNext(List<DataTable> dataTables) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showSavingDataTable(dataTables);
                    }
                }));
    }

    public void loadSavingAccount(String type, int accountId, String association) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManager.getSavingsAccount(type, accountId, association)
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
                        getMvpView().showErrorFetchingSavingAccount("Internal Server Error");
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
        mSubscriptions.add(mDataManager.activateSavings(savingsAccountId, request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<GenericResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(GenericResponse genericResponse) {
                        getMvpView().showSavingsActivatedSuccessfully(genericResponse);
                    }
                }));
    }
}
