package com.mifos.mifosxdroid.online.loanaccountdisbursement;

import com.mifos.api.GenericResponse;
import com.mifos.api.datamanager.DataManagerLoan;
import com.mifos.api.model.APIEndPoint;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.accounts.loan.LoanDisbursement;
import com.mifos.objects.templates.loans.LoanTransactionTemplate;
import com.mifos.utils.MFErrorParser;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Rajan Maurya on 8/6/16.
 */
public class LoanAccountDisbursementPresenter
        extends BasePresenter<LoanAccountDisbursementMvpView> {

    private final DataManagerLoan dataManagerLoan;
    private CompositeSubscription subscriptions;

    @Inject
    public LoanAccountDisbursementPresenter(DataManagerLoan dataManager) {
        dataManagerLoan = dataManager;
        subscriptions = new CompositeSubscription();
    }

    @Override
    public void attachView(LoanAccountDisbursementMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        subscriptions.unsubscribe();
    }

    public void loadLoanTemplate(int loanId) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        subscriptions.add(dataManagerLoan.getLoanTransactionTemplate(loanId, APIEndPoint.DISBURSE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<LoanTransactionTemplate>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError("s");
                    }

                    @Override
                    public void onNext(LoanTransactionTemplate loanTransactionTemplate) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showLoanTransactionTemplate(loanTransactionTemplate);
                    }
                })
        );
    }

    public void disburseLoan(int loanId, LoanDisbursement loanDisbursement) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        subscriptions.add(dataManagerLoan.dispurseLoan(loanId, loanDisbursement)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<GenericResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError(MFErrorParser.errorMessage(e));
                    }

                    @Override
                    public void onNext(GenericResponse genericResponse) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showDisburseLoanSuccessfully(genericResponse);
                    }
                }));
    }

}
