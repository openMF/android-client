package com.mifos.mifosxdroid.online.loanaccountsummary;

import com.mifos.api.datamanager.DataManagerDataTable;
import com.mifos.api.datamanager.DataManagerLoan;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.accounts.loan.LoanWithAssociations;
import com.mifos.objects.noncore.DataTable;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Rajan Maurya on 07/06/16.
 */
public class LoanAccountSummaryPresenter extends BasePresenter<LoanAccountSummaryMvpView> {

    private final DataManagerLoan mDataManagerLoan;
    private final DataManagerDataTable mDataManagerDataTable;
    private CompositeSubscription mSubscriptions;
    private static final String LOAN_DATA_TABLE_NAME = "m_loan";

    @Inject
    public LoanAccountSummaryPresenter(DataManagerLoan dataManagerLoan,
                                       DataManagerDataTable dataManagerDataTable) {
        mDataManagerLoan = dataManagerLoan;
        mDataManagerDataTable = dataManagerDataTable;
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void attachView(LoanAccountSummaryMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        mSubscriptions.unsubscribe();
    }

    public void loadLoanDataTable() {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerDataTable.getDataTable(LOAN_DATA_TABLE_NAME)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<DataTable>>() {
                    @Override
                    public void onCompleted() {
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
                }));
    }

    public void loadLoanById(int loanAccountNumber) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerLoan.getLoanById(loanAccountNumber)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<LoanWithAssociations>() {
                    @Override
                    public void onCompleted() {
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
                }));
    }

}
