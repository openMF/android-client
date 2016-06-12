package com.mifos.mifosxdroid.online.loanaccountsummary;

import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.accounts.loan.LoanWithAssociations;
import com.mifos.objects.noncore.DataTable;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 07/06/16.
 */
public class LoanAccountSummaryPresenter extends BasePresenter<LoanAccountSummaryMvpView> {

    private final DataManager mDataManager;
    private Subscription mSubscription;

    @Inject
    public LoanAccountSummaryPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(LoanAccountSummaryMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void loadLoanDataTable() {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getLoanDataTable()
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
                        getMvpView().showFetchingError("Loan DataTable not found.");
                    }

                    @Override
                    public void onNext(List<DataTable> dataTables) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showLoanDataTable(dataTables);
                    }
                });
    }

    public void loadLoanById(int loanAccountNumber) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getLoanById(loanAccountNumber)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<LoanWithAssociations>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError("Loan Account not found.");
                    }

                    @Override
                    public void onNext(LoanWithAssociations loanWithAssociations) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showLoanById(loanWithAssociations);
                    }
                });
    }

}
