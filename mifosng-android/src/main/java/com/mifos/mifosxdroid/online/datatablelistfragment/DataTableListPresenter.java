package com.mifos.mifosxdroid.online.datatablelistfragment;

import com.mifos.api.DataManager;
import com.mifos.api.datamanager.DataManagerLoan;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.accounts.loan.Loans;
import com.mifos.services.data.GroupLoanPayload;
import com.mifos.services.data.LoansPayload;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class DataTableListPresenter extends BasePresenter<DataTableListMvpView> {

    private final DataManagerLoan mDataManagerLoan;
    private final DataManager mDataManager;
    private CompositeSubscription mSubscription;

    @Inject
    public DataTableListPresenter(DataManagerLoan dataManager, DataManager manager) {
        mDataManagerLoan = dataManager;
        mDataManager = manager;
        mSubscription = new CompositeSubscription();
    }

    @Override
    public void attachView(DataTableListMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void createLoansAccount(LoansPayload loansPayload) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscription.add(mDataManagerLoan.createLoansAccount(loansPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Loans>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showMessage(R.string.generic_failure_message);
                    }

                    @Override
                    public void onNext(Loans loans) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showMessage(R.string.loan_creation_success);

                    }
                }));
    }

    public void createGroupLoanAccount(GroupLoanPayload loansPayload) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscription.add(mDataManager.createGroupLoansAccount(loansPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Loans>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showMessage(R.string.generic_failure_message);
                    }

                    @Override
                    public void onNext(Loans loans) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showMessage(R.string.loan_creation_success);
                    }
                })
        );
    }

}
